package com.example.mapdir;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class AddCrimeActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ProgressDialog loadingBar;
    private EditText crimeType;
    private Button addtoDB;
    private String pincode="1";
    private String pincode1;
    private DatabaseReference mDatabase;
    ArrayList markerPoints= new ArrayList();
    Pincode data = new Pincode();
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crime);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        addtoDB = (Button) findViewById(R.id.add_crime_save_db);
        crimeType = (EditText)findViewById(R.id.add_crime_description);
        loadingBar = new ProgressDialog(this);

        reference = FirebaseDatabase.getInstance().getReference().child(pincode);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);



        addtoDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddingDetailsToFirebase();
            }
        });
    }

    private void AddingDetailsToFirebase() {
        String crimetypetext = crimeType.getText().toString();
        if(TextUtils.isEmpty(crimetypetext))
        {
            Toast.makeText(this, "Enter Crime Type", Toast.LENGTH_SHORT).show();
        }
        else
        {
            LatLng origin = (LatLng) markerPoints.get(0);

            //counter crime

            Random objGenerator = new Random();
            int randomNumber = objGenerator.nextInt(1000000);
            loadingBar.setTitle("Adding to Database");
            loadingBar.setMessage("Adding point "+origin+"\nCrime: "+crimetypetext+"\npincode"+pincode);
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            data.setPincode(pincode1);
            data.setCoordinates(origin);
            data.setThreatlevel(crimetypetext);
            loadingBar.dismiss();
            reference.child(pincode1).child(randomNumber+"").setValue(data);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;


        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(23.7746739, 90.3925568);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        //moveToCurrentLocation(sydney);
        ///mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (markerPoints.size() > 0) {
                    markerPoints.clear();
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    LatLng origin = (LatLng) markerPoints.get(0);
                    // pincode
                    String url = getDirectionsUrl(origin);
                    AddCrimeActivity.DownloadTask downloadTask = new AddCrimeActivity.DownloadTask();
                    downloadTask.execute(url);
                    Toast.makeText(AddCrimeActivity.this, origin.toString(), Toast.LENGTH_SHORT).show();
                }

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);



            }
        });
    }
    private class DownloadTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AddCrimeActivity.this);
            progressDialog.setMessage("Loading");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            JSONObject jsonObject = null;
            Log.i("info", result);

            int index = result.indexOf("pincode");
            index+=10;
            pincode="";
            while(result.charAt(index+1)!=',')
            {
                pincode+=result.charAt(index);
                index++;
            }

            pincode1=pincode;
            Toast.makeText(AddCrimeActivity.this, "res: " + pincode, Toast.LENGTH_LONG).show();
            try{
                jsonObject = new JSONObject(result);
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                List<LatLng> locationList = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("routes");
                JSONObject obj=jsonArray.getJSONObject(0);
                JSONObject geometryObject=obj.getJSONObject("geometry");

                //Toast.makeText(MapsActivity.this, "res: " + geometryObject.toString(), Toast.LENGTH_LONG).show();
                GeoJsonLayer layer = new GeoJsonLayer(mMap, geometryObject);
                layer.addLayerToMap();

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private String getDirectionsUrl(LatLng origin) {
        // Building the url to the web service
        //https://apis.mapmyindia.com/advancedmaps/v1/zdhlcatxe8tt5mr6c9w3wrhrdfbbr8h3/rev_geocode?lat=26.5645&lng=85.9914
        String url = "https://apis.mapmyindia.com/advancedmaps/v1/zdhlcatxe8tt5mr6c9w3wrhrdfbbr8h3/rev_geocode?lat=" +origin.latitude+"&lng="+origin.longitude;
        Log.i("info", url);

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}
