package com.dbvertex.dilsayproject.UserAuth;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    TextView addressTV;
    Intent intent;
    RequestQueue queue;
    String address="";
    Geocoder geocoder;
    double latitude;
    double longitude;
    LocationManager manager;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private Location mylocation;
    Button submitBTN;
    ProgressDialog progressDialog;
    SessionManager sessionManager;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 0;
    List<Place.Field> fields;
    MarkerOptions markerOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);

        markerOptions = new MarkerOptions();
        setUpGClient();
        queue = Volley.newRequestQueue(MyLocationActivity.this);
        sessionManager = new SessionManager(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        progressDialog = new ProgressDialog(MyLocationActivity.this);

        intent = getIntent();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("My Location");


        addressTV = findViewById(R.id.address);
        submitBTN = findViewById(R.id.submitBTN);

        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyBv7CImI7BPEiyAt9NAY35IXbC_5rk9BJg");
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);






        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        addressTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(MyLocationActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });





        submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.setLocation(address,String.valueOf(latitude),String.valueOf(longitude));
                Intent intent = new Intent(MyLocationActivity.this, LocationDescription.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyLocationActivity.this, ChooseInterestActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        submitDetail();


    }


    private void submitDetail()
    {

        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LOCATION_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("LOCATION_LIST", response);
                        try {

                            progressDialog.dismiss();
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");
//                            sessionManager.setStep("3");
                           // {"status":200,"message":"success","data":{"id":158,"latitude":"","longitude":"","location":"","description":""}}
                            if (status == 200 && message.equalsIgnoreCase("success"))
                            {
                                JSONObject jsonObject=obj.getJSONObject("data");
                                String lati=jsonObject.getString("latitude");
                                String longi=jsonObject.getString("longitude");
                                String location=jsonObject.getString("location");
                                String description=jsonObject.getString("description");
                                sessionManager.setLocation(location,lati,longi);
                                sessionManager.setDescription(description);

                            }

                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                ob.put("interest[]", sessionManager.getInterest().get(SessionManager.KEY_INTEREST));

                Log.e("params", ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Add a marker in Sydney and move the camera
        mMap = googleMap;
        mMap.clear();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);


        mMap.setMinZoomPreference(12);
        LatLng latLng = new LatLng(latitude, longitude);


        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(address);

// Changing marker icon


// adding marker


        if (sessionManager.getDetails().get(SessionManager.KEY_GENDER).equalsIgnoreCase("M"))
        {
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationboy));

        }

        else
        {
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_girl));

        }
        mMap.addMarker(marker);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));



        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        //  }


    }

    public void getLatLong(final String youraddress) {
        String uri = "https://maps.google.com/maps/api/geocode/json?key=AIzaSyA1UaGGboDf_r7jRT3WHRB6Cqdw5fQRp7w&address="
                + youraddress + "&sensor=true";
        uri = uri.replaceAll(" ", "%20");

        StringRequest postRequest = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("youraddress", response);
                        try {
                            Log.e("response", response);
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));

                            longitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");

                            latitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");



                            Log.e("latlong", latitude + "  " + longitude);

                            mMap.clear();
                            LatLng latLng = new LatLng(latitude, longitude);
                            Log.e("maplong", latitude + " " + longitude + " " + latLng);
                            markerOptions.position(latLng);
                            mMap.addMarker(markerOptions.title(address));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            //Log.e("latitude", "" + lat);
                            //Log.e("longitude", "" + lng);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {

        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            latitude = mylocation.getLatitude();
            longitude = mylocation.getLongitude();

            List<Address> addresses;
            geocoder = new Geocoder(MyLocationActivity.this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 4); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                Log.e("address",address);
                addressTV.setText(address);

            } catch (IOException e) {
                e.printStackTrace();
            }

            LatLng latLng = new LatLng(latitude, longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            mMap.clear();

            markerOptions.position(latLng);
            mMap.addMarker(markerOptions.title(address));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Do whatever you need
        //You can display a message here
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
    }


    private void getMyLocation()
    {
        if (googleApiClient != null)
        {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(MyLocationActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(MyLocationActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);

                                        Log.e("latlng", latitude +  " " + longitude+"  "+mylocation);


                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        Log.e("latlng", "REQUEST_CHECK_SETTINGS_GPS");

                                        status.startResolutionForResult(MyLocationActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    Log.e("latlng", "SETTINGS_CHANGE_UNAVAILABLE");

                                    break;

                                case ConnectionResult.SERVICE_MISSING:
                                    Toast.makeText(MyLocationActivity.this, "Google Play Services Missing", Toast.LENGTH_SHORT).show();
                                    break;
                                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                                    //The user has to update play services
                                    Toast.makeText(MyLocationActivity.this, "Update Google Play Services", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();

                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;

            case AUTOCOMPLETE_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Place place = Autocomplete.getPlaceFromIntent(data);
                        address = place.getName();
                        addressTV.setText(address);
                        getLatLong(address);

                        break;
                    case AutocompleteActivity.RESULT_ERROR:
                        Status status = Autocomplete.getStatusFromIntent(data);
                        break;
                }
                break;

        }
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(MyLocationActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MyLocationActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyLocationActivity.this, ChooseInterestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,
                R.anim.trans_left_out);
    }
}
