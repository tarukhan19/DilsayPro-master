package com.dbvertex.dilsayproject.UserAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.dbvertex.dilsayproject.Fragment.ChatListFragment;
import com.dbvertex.dilsayproject.Fragment.DasboardFragment;
import com.dbvertex.dilsayproject.Fragment.HomeFragment;
import com.dbvertex.dilsayproject.Fragment.InstaVerificationFragment;
import com.dbvertex.dilsayproject.Fragment.InstanceFragment;
import com.dbvertex.dilsayproject.Fragment.MyActivityFragment;
import com.dbvertex.dilsayproject.Fragment.MyProfileFragment;
import com.dbvertex.dilsayproject.Fragment.SettingFragment;

import com.dbvertex.dilsayproject.LocationUtil.PermissionUtils;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.dbvertex.dilsayproject.views.DuoDrawerLayout;
import com.dbvertex.dilsayproject.views.DuoMenuView;
import com.dbvertex.dilsayproject.widgets.DuoDrawerToggle;

import org.json.JSONException;
import org.json.JSONObject;
import com.dbvertex.dilsayproject.LocationUtil.PermissionUtils.PermissionResultCallback;
import com.dbvertex.dilsayproject.Adapter.MenuAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomePageActivity extends AppCompatActivity implements DuoMenuView.OnMenuClickListener , GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, PermissionResultCallback {
    private static final String TAG = "Tag";
    private MenuAdapter mMenuAdapter;
    private ViewHolder mViewHolder;

    LinearLayout logoutLL;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager sessionManager;
    Intent intent;
    TextView namenav, emailnav, mobilenav, useridnav;
    private ArrayList<String> mTitles = new ArrayList<>();
    Toolbar toolbar;
    String opendialog;
    private Location mLastLocation;

    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;

    private GoogleApiClient mGoogleApiClient;
    Dialog dialog;
    double latitude;
    double longitude;
    // list of permissions

    ArrayList<String> permissions=new ArrayList<>();
    PermissionUtils permissionUtils;

    boolean isPermissionGranted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        permissionUtils=new PermissionUtils(HomePageActivity.this);

        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionUtils.check_permission(permissions,"Need GPS permission for getting your location",1);



        if (checkPlayServices()) {

            buildGoogleApiClient();
        }

        getLocation();



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));
        sessionManager = new SessionManager(HomePageActivity.this);
        progressDialog = new ProgressDialog(HomePageActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(HomePageActivity.this);

        intent=getIntent();

        if (intent.hasExtra("opendialog"))
        {
            openDialog();

        }
        logoutLL = findViewById(R.id.logoutlayout);
        namenav = findViewById(R.id.usernamenav);
        emailnav = findViewById(R.id.emailnav);
        mobilenav = findViewById(R.id.mo);
        useridnav = findViewById(R.id.userid);
        sessionManager = new SessionManager(HomePageActivity.this);

        namenav.setText(sessionManager.getFacebookData().get(SessionManager.KEY_USERNAME));
        emailnav.setText(sessionManager.getFacebookData().get(SessionManager.KEY_EMAIL));

        mViewHolder = new ViewHolder();
        handleToolbar();
        handleMenu();

        if (intent.hasExtra("from"))
        {
            String from=intent.getStringExtra("from");

            if (from.equalsIgnoreCase("SettingFragment"))
            {
                goToFragment(new SettingFragment(), false);
                //titleh.setText("Settings");
                mMenuAdapter.setViewSelected(5, true);
                //toolbar.setBackgroundResource((R.drawable.gradient));

                setTitle(mTitles.get(5));
            }
            else if (from.equalsIgnoreCase("MyProfileFragment"))
            {
                goToFragment(new MyProfileFragment(), false);
                //titleh.setText("Settings");
                mMenuAdapter.setViewSelected(1, true);
                //toolbar.setBackgroundResource((R.drawable.gradient));

                setTitle(mTitles.get(1));
            }
            else if (from.equalsIgnoreCase("chatfrag"))
            {
                goToFragment(new ChatListFragment(), false);
                //titleh.setText("Settings");
                mMenuAdapter.setViewSelected(6, true);
                //toolbar.setBackgroundResource((R.drawable.gradient));

                setTitle(mTitles.get(6));
            }

            else if (from.equalsIgnoreCase("filter"))
            {
                goToFragment(new HomeFragment(), false);
                //titleh.setText("Settings");
                mMenuAdapter.setViewSelected(0, true);
                //toolbar.setBackgroundResource((R.drawable.gradient));

                setTitle(mTitles.get(0));
            }
        }
        else
        {
            goToFragment(new HomeFragment(), false);

            mMenuAdapter.setViewSelected(0, true);
            setTitle(mTitles.get(0));
        }

        logoutLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogOutDialog();

            }
        });

    }

    private void openDialog()
    {
        final Dialog dialog = new Dialog(HomePageActivity.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_camerarentalsuccess);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LinearLayout submit = dialog.findViewById(R.id.submit);
        ImageView cross = dialog.findViewById(R.id.cross);
        dialog.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

    }

    private void showLogOutDialog()
    {

        final Dialog dialog = new Dialog(HomePageActivity.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_logout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LinearLayout ok = (LinearLayout) dialog.findViewById(R.id.ok);
        LinearLayout cancel = (LinearLayout) dialog.findViewById(R.id.cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logOut();
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });


        dialog.show();

    }

    private void logOut()
    {
        progressDialog.setMessage("loading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.LOGOUT, new Response.Listener<String>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("response", response);
                    progressDialog.dismiss();
                    JSONObject obj = new JSONObject(response);
//{"status":200,"message":"success","data":{"user_id":"18","device_id":"hh","device_type":"android"}}
                    if (obj.getString("status").equalsIgnoreCase("200"))
                    {
                        Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        sessionManager.logoutUser();
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in,
                                R.anim.trans_left_out);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                // Toast.makeText(dialog.getContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }


        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                headers.put("device_id", "hh");
                headers.put("device_type", "android");

                Log.e("params", headers.toString());


                return headers;
            }
        };


        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }


    private void handleToolbar() {
        setSupportActionBar(mViewHolder.mToolbar);
    }

    public void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle;
        duoDrawerToggle = new DuoDrawerToggle(this,
                mViewHolder.mDuoDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

    }

    private void handleMenu() {
        mMenuAdapter = new MenuAdapter(mTitles);
        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
        mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
    }

    @Override
    public void onFooterClicked() {
        Toast.makeText(this, "onFooterClicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHeaderClicked() {
        // Toast.makeText(this, "onHeaderClicked", Toast.LENGTH_SHORT).show();
    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        // transaction.add(R.id.container, fragment).commit();
    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {
        // Set the toolbar title
        setTitle(mTitles.get(position));

        // Set the right options selected
        mMenuAdapter.setViewSelected(position, true);
        //Fragment fragment;
        // Navigate to the right fragment
        switch (position) {
            case 0:
                goToFragment(new HomeFragment(), false);
                break;

            case 1:
                goToFragment(new MyProfileFragment(), false);
                break;
            case 2:
                goToFragment(new DasboardFragment(), false);
                break;
            case 3:
                goToFragment(new MyActivityFragment(), false);
                break;
            case 4:
                goToFragment(new InstanceFragment(), false);
                break;
            case 5:
                goToFragment(new SettingFragment(), false);
                break;

            case 6:
                goToFragment(new ChatListFragment(), false);
                break;

            default:

                goToFragment(new HomeFragment(), false);
        }

        mViewHolder.mDuoDrawerLayout.closeDrawer();
    }

    public  void clickEvents()
    {
        mViewHolder.mDuoDrawerLayout.openDrawer();
    }


    private class ViewHolder
    {
        private DuoDrawerLayout mDuoDrawerLayout;
        private DuoMenuView mDuoMenuView;
        private Toolbar mToolbar;

        ViewHolder()
        {
            mDuoDrawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }




    private void getLocation() {

        if (isPermissionGranted) {

            try
            {
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
                Log.e("location",mLastLocation+"");

                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                    Log.e("latitude1",""+latitude);
                    Log.e("longitude1",""+longitude);
                    sessionManager.setLocation("",String.valueOf(latitude),String.valueOf(longitude));
                   // getAddress();

                }
            }
            catch (SecurityException e)
            {
                e.printStackTrace();
                Log.e("SecurityException",""+e);
            }

        }

    }

    public Address getAddress(double latitude, double longitude)
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }


    public void getAddress()
    {

        Address locationAddress=getAddress(latitude,longitude);

        if(locationAddress!=null)
        {

            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();
            Log.e("city",city);
            sessionManager.setLocation(address,String.valueOf(latitude),String.valueOf(longitude));


            String currentLocation;

            if(!TextUtils.isEmpty(address))
            {
                currentLocation=address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation+="\n"+address1;

                if (!TextUtils.isEmpty(city))
                {
                    currentLocation+="\n"+city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+=" - "+postalCode;
                }
                else
                {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+="\n"+postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation+="\n"+state;

                if (!TextUtils.isEmpty(country))
                    currentLocation+="\n"+country;



            }

        }

    }

    /**
     * Creating google api client object
     * */

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(HomePageActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });


    }




    /**
     * Method to verify google play services on the device
     * */

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this,resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    // Permission check functions


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // redirects to utils
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }




    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION","GRANTED");
        isPermissionGranted=true;
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY","GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION","DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION","NEVER ASK AGAIN");
    }

    public void showToast(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }





}

