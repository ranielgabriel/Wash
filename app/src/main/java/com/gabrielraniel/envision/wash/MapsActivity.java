package com.gabrielraniel.envision.wash;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gabrielraniel.envision.wash.adapters.ViewPagerAdapter;
import com.gabrielraniel.envision.wash.lists.ReviewList;
import com.gabrielraniel.envision.wash.miscs.DataParser;
import com.gabrielraniel.envision.wash.models.Image;
import com.gabrielraniel.envision.wash.models.Rating;
import com.gabrielraniel.envision.wash.models.Report;
import com.gabrielraniel.envision.wash.models.Restroom;
import com.gabrielraniel.envision.wash.models.Review;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private ProgressDialog progressDialog;

    private AdView mAdView;

    //FOR FIREBASE DATABASE, AUTH, STORAGE
    private DatabaseReference databaseRestrooms, databaseRatings, databaseReviews, databaseReports, databaseImages, databaseUsers, databaseBusinesses;
    private ValueEventListener vel_restrooms, vel_ratings, vel_reviews, vel_reports, vel_images, vel_users, vel_businesses, vel_reports_key;
    private StorageReference storageRestroomImage;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    //FROM PREVIOUS INTENT
    private Bundle bundle;
    private String _USER_EMAIL, _USER_TYPE, _USER_ID;

    //FOR THE LOCATION SEARCH BUTTON
    private ImageButton ibSearch;
    private EditText etLocation;

    //LIST FOR RETRIEVING DATA
    private List<Restroom> restroomList;
    private List<Review> reviewList;
    private List<Rating> ratingList;

    //FOR UPLOADING AND DOWNLOADING IMAGE
    private String imageEncoded;
    private List<String> imagesEncodedList;

    //FOR GETTING DIRECTIONS
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private LocationRequest mLocationRequest;
    private ArrayList<LatLng> MarkerPoints;
    private String distance = "";
    private String duration = "";

    //FOR FONTS
    private Typeface typeface_normal;
    private Typeface typeface_bold;

    //FOR VIEWPAGER
    private ViewPager viewPagerAddRestroom;
    private ImageButton ibChageImages, ibAddRestroomImage;
    private int dotsCounts;
    private ImageView[] dots;
    private LinearLayout sliderDotsPanel;

    //FOR SELECTING IMAGE
    private final static int GALLERY_INTENT = 11;

    //FOR PERMISSION REQUESTS
    final int MY_LOCATION_REQUEST_CODE = 0;
    final int MY_GALLERY_REQUEST_CODE = 1;
    final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 90;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSIONS_REQUEST_GALLERY = 98;

    //ADDING RESTROOM
    private FloatingActionButton fabAddRestroom, fabCancel, fabReset;
    private Marker addMarker;

    //NAV HEADER
    private TextView tvUserType, tvEmail, tvCopyright;
    private ImageView ivImageIconUserType;

    //SNACKBAR ROOT LAYOUT
    private CoordinatorLayout rootLayout;

    //FOR REPORT
    private boolean alreadyReported = false;

    //FOR STARTUPZOOM
    private boolean didZoomedIn = false;

    //FOR AVAILABILITY
    private boolean availabilityConfirmed;

    //FOR SHARING
    private CountDownTimer countDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        rootLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_maps);

        //FOR FONTS
        typeface_normal = Typeface.createFromAsset(getAssets(), "fonts/montserrat_regular.otf");
        typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/montserrat_bold.otf");

        //FOR FIREBASE INITIALIZATION
        //DATABASE REFERENCE CONNECTORS
        databaseRestrooms = FirebaseDatabase.getInstance().getReference("restrooms");
        databaseRatings = FirebaseDatabase.getInstance().getReference("ratings");
        databaseReviews = FirebaseDatabase.getInstance().getReference("reviews");
        databaseReports = FirebaseDatabase.getInstance().getReference("reports");
        databaseImages = FirebaseDatabase.getInstance().getReference("images");
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        databaseBusinesses = FirebaseDatabase.getInstance().getReference("businesses");
        //STORAGE REFERENCE CONNECTOR
        storageRestroomImage = FirebaseStorage.getInstance().getReference();

        //FIREBASE AUTHENTICATION
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        _USER_ID = user.getUid();
        _USER_EMAIL = user.getEmail();

        //LAYOUT INITIALIZATION
        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMap);
        setSupportActionBar(toolbar);

        //DRAWER
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NAVIGATION HEADER
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_view_map);

        //APP BAR LAYOUT CONNECTORS
        tvCopyright = (TextView) findViewById(R.id.textViewCopyright);
        fabAddRestroom = (FloatingActionButton) findViewById(R.id.fabAddRestroom);
        fabCancel = (FloatingActionButton) findViewById(R.id.fabCancel);
        fabReset = (FloatingActionButton) findViewById(R.id.fabReset);
        ibSearch = (ImageButton) findViewById(R.id.imageButtonSearch);
        etLocation = (EditText) findViewById(R.id.editTextSearch);

        //HEADER VIEW CONNECTORS
        tvEmail = (TextView) header.findViewById(R.id.textViewEmail);
        tvUserType = (TextView) header.findViewById(R.id.textViewUserType);
        ivImageIconUserType = (ImageView) header.findViewById(R.id.imageViewUserTypeIcon);

        //HEADER VIEW SETUP
        tvCopyright.setTypeface(typeface_normal);
        tvEmail.setTypeface(typeface_normal);
        tvUserType.setTypeface(typeface_bold);
        etLocation.setTypeface(typeface_normal);

        //INITIALIZATION OF VIEW
        fabAddRestroom.setOnClickListener(this);
        fabCancel.setOnClickListener(this);
        fabReset.setOnClickListener(this);
        ibSearch.setOnClickListener(this);
        etLocation.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        //SHOW CURRENT USER EMAIL AT HEADER
        tvEmail.setText(_USER_EMAIL);

        //PERMISSION CHECK
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //ARRAY LISTS INITIALIZATIONS
        restroomList = new ArrayList<>();
        reviewList = new ArrayList<>();
        ratingList = new ArrayList<>();
        MarkerPoints = new ArrayList<>();
//        imagesToDownload = new ArrayList<>();

        bundle = getIntent().getExtras();

        try {
            if (bundle != null) {
                _USER_EMAIL = bundle.getString("USER_EMAIL");
                Log.d("USER_EMAIL", _USER_EMAIL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //SEARCH USER TYPE AND PUT TO NAV HEADER
        final String _user_id = user.getUid();
        vel_users = databaseUsers.child(_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                _USER_TYPE = String.valueOf(dataSnapshot.child("user_type").getValue());
                if (_USER_TYPE.equals("business owner")) {
                    fabAddRestroom.setVisibility(View.VISIBLE);
                }
                String guide = String.valueOf(dataSnapshot.child("guide").getValue());

                switch (_USER_TYPE) {
                    case "business owner":
                        ivImageIconUserType.setImageResource(R.drawable.ic_owner);
                        tvUserType.setText("Business Establishment Owner");
                        if (guide.equals("no")) {
                            databaseUsers.child(_user_id).child("guide").setValue("yes");
                            Intent i = new Intent(MapsActivity.this, TutorialActivity.class);
                            i.putExtra("TYPE", "business owner");
                            startActivity(i);
                        }
                        break;
                    case "regular user":
                        tvUserType.setText("Registered User");
                        ivImageIconUserType.setImageResource(R.drawable.ic_registered);
                        if (guide.equals("no")) {
                            databaseUsers.child(_user_id).child("guide").setValue("yes");
                            Intent x = new Intent(MapsActivity.this, TutorialActivity.class);
                            x.putExtra("TYPE", "regular user");
                            startActivity(x);
                        }
                        break;

                    default:
                        tvUserType.setText("Guest User");
                        ivImageIconUserType.setImageResource(R.drawable.ic_guest);
                        break;
                }
                Log.d("USER_TYPE", _USER_TYPE);
                databaseUsers.child(_user_id).removeEventListener(vel_users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editTextSearch:
                callPlaceAutocompleteActivityIntent();
                break;
            case R.id.imageButtonSearch:
                if (isNetworkAvailable() && isLocationAvailable()) {
                    callPlaceAutocompleteActivityIntent();
                } else {
                    Toast.makeText(this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fabAddRestroom:
                //PAG MAG AADD NG BAGONG RESTROOM
                if (isNetworkAvailable() && isLocationAvailable()) {
                    //SHOW SNACKBAR
                    Snackbar snackbar_add = Snackbar.make(rootLayout, "Do you want to add a restroom?", Snackbar.LENGTH_LONG);
                    snackbar_add.setAction("ADD", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mLastLocation == null) {
                                showYouMustTurnOnGPS();
                                return;
                            }

                            switch (_USER_TYPE) {
                                case "business owner":
                                    showGuideAddingRestroom();
                                    break;
                            }

                            mMap.clear();

                            retrieveOwnersRestrooms();


                            fabAddRestroom.setVisibility(View.GONE);
                            fabCancel.setVisibility(View.VISIBLE);
                        }
                    });

                    View snackBarView = snackbar_add.getView();
                    snackBarView.setBackgroundColor(Color.parseColor("#4285f4")); // snackbar background color
                    snackbar_add.setActionTextColor(Color.parseColor("#ffffff")); // snackbar action text color
                    snackbar_add.show();
                } else {
                    Toast.makeText(this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fabCancel:
                //PAG MAG CACANCEL NG PAG AADD
                if (isNetworkAvailable() && isLocationAvailable()) {
                    //SHOW SNACKBAR
                    Snackbar snackbar_cancel = Snackbar.make(rootLayout, "Cancel adding a restroom?", Snackbar.LENGTH_LONG);
                    snackbar_cancel.setAction("CANCEL", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addMarker.remove();
                            retrieveRestrooms();

                            fabAddRestroom.setVisibility(View.VISIBLE);
                            fabCancel.setVisibility(View.GONE);
                        }
                    });

                    View snackBarViewReset = snackbar_cancel.getView();
                    snackBarViewReset.setBackgroundColor(Color.parseColor("#4285f4")); // snackbar background color
                    snackbar_cancel.setActionTextColor(Color.parseColor("#ffffff")); // snackbar action text color
                    snackbar_cancel.show();
                } else {
                    Toast.makeText(this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fabReset:
                if (isNetworkAvailable() && isLocationAvailable()) {
                    //SHOW SNACKBAR
                    Snackbar snackbar_reset = Snackbar.make(rootLayout, "Dismiss routing?", Snackbar.LENGTH_LONG);
                    snackbar_reset.setAction("DISMISS", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fabReset.setVisibility(View.GONE);
                            mMap.clear();
                            retrieveRestrooms();
                            //SEARCH USER TYPE
                            if (_USER_TYPE.equals("business owner")) {
                                fabAddRestroom.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    View snackBarViewCancel = snackbar_reset.getView();
                    snackBarViewCancel.setBackgroundColor(Color.parseColor("#387889")); // snackbar background color
                    snackbar_reset.setActionTextColor(Color.parseColor("#ffffff")); // snackbar action text color
                    snackbar_reset.show();
                    break;
                } else {
                    Toast.makeText(this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        countDownToShare();
    }

    @Override
    protected void onResume() {
        super.onResume();
        countDown.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        countDown.cancel();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_logout:
                if (mAuth.getCurrentUser().isAnonymous()) {
                    //IF THE USER IS ANONYMOUSLY SIGNED IN AND LOGGED OUT
                    //THAT ACCOUNT WILL BE DELETED.
                    mAuth.getCurrentUser().delete();
                }

                //SIGN OUT SESSION
                mAuth.signOut();

                //CLEARING VARIABLES
                _USER_TYPE = "";
                _USER_EMAIL = "";

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                break;

            case R.id.nav_view_settings:
                //GO TO SETTINGS ACTIVITY
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;

            case R.id.nav_view_help:
                //GO TO HELP ACTIVITY
                Intent x = new Intent(this, HelpActivity.class);
                startActivity(x);
                break;
            case R.id.nav_view_about:
                //GO TO ABOUT ACTIVITY
                Intent y = new Intent(this, AboutActivity.class);
                startActivity(y);
                break;
            case R.id.nav_view_share:
                try {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "Have you tried this app? " + "https://drive.google.com/open?id=1qwTI0Wjx-ULxIZwxqi-KgoMaaDRk-jPN" + "\nTry \"Wash: A Restroom Locator Android Mobile Application\".");
                    sharingIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sharingIntent, "Share via:"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        //ANIMATE TO PHILIPPINES
        LatLng ph_latlng = new LatLng(12.8797, 121.7740);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ph_latlng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(7));

        mMap.clear();

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (isNetworkAvailable() && isLocationAvailable()) {
                    try {
                        if (marker.getSnippet().equals("new restroom")) {
                            //WHEN ADDING NEW RESTROOM
                            showAddRestroomDialog(marker.getPosition());
                        } else {
                            //KWHEN RETRIEVING RESTROOM
                            String _business_id_restroom_id[] = marker.getSnippet().split(", ");
                            Log.d("MAPS_BUSINESS_ID", _business_id_restroom_id[1]);
                            Log.d("MAPS_RESTROOM_ID", _business_id_restroom_id[0]);
                            showRestroomInformation(_business_id_restroom_id[0], marker.getTitle(), marker.getPosition(), _business_id_restroom_id[1]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                try {
                    if (addMarker != null) {
                        addMarker.setPosition(mMap.getCameraPosition().target);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                marker.getPosition();
            }
        });

        //RETRIEVE ALL RESTROOMS
        retrieveRestrooms();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            } else {
                // Permission was denied. Display an error message.
            }
        }

        if (requestCode == MY_GALLERY_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
//                    Toast.makeText(this, "Gallery permission granted.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                // Permission was denied. Display an error message.
            }
        }

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Location permission denied, you must allow Location permission to use this application.", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GALLERY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent();
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.setAction(Intent.ACTION_GET_CONTENT);

                        intent.setType("image/*");

                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_INTENT);
                    }

                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Gallery permission denied, you must allow permission to select images.", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_GALLERY);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_GALLERY);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        checkLocationPermission();
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            imagesEncodedList = new ArrayList<String>();
            // When an Image is picked
            if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK
                    && null != data) {

                // Get the Image from data
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (data.getData() != null) {

                    Uri mImageUri = data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = mImageUri.toString();
                    imagesEncodedList.add(imageEncoded);
                    cursor.close();

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        if (mClipData.getItemCount() <= 5) {
                            ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {

                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                mArrayUri.add(uri);
                                // Get the cursor
                                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                                // Move to first row
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                //imageEncoded = cursor.getString(columnIndex);
                                imageEncoded = uri.toString();
                                imagesEncodedList.add(imageEncoded);
                                cursor.close();
                                Log.d("IMAGE_PATH", imagesEncodedList.get(i));

                            }
                            Log.v("IMAGE_SIZE", "Selected Images " + mArrayUri.size());
                        } else {
                            Toast.makeText(this, "You can only choose up to 5 images to upload.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                //GET THE VIEW PAGER ADAPTER
                ViewPagerAdapter viewPagerAdapter;
                viewPagerAdapter = new ViewPagerAdapter(imagesEncodedList, this);
                viewPagerAddRestroom.setAdapter(viewPagerAdapter);
                viewPagerAddRestroom.setVisibility(View.VISIBLE);
                ibAddRestroomImage.setVisibility(View.GONE);
                ibChageImages.setVisibility(View.VISIBLE);

                if (dots != null && dotsCounts != 0) {
                    for (int i = 0; i < dotsCounts; i++) {
                        try {
                            sliderDotsPanel.removeView(dots[i]);
                        } catch (Exception e) {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                //GET COUNTS OF DOTS
                dotsCounts = viewPagerAdapter.getCount();
                dots = new ImageView[dotsCounts];

                for (int i = 0; i < dotsCounts; i++) {
                    dots[i] = new ImageView(this);
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_nonactive_dot));

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(8, 0, 8, 0);

                    try {
                        sliderDotsPanel.addView(dots[i], params);
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active_dot));
            } else {

                imagesEncodedList = null;
                //Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            //Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG.show();
        }

        //SEARCHING A PLACE
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("PLACE", "Place:" + place.toString());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16));
                etLocation.setText(place.getName().toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i("PLACE", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {

            }
        }

    }

    private boolean isNetworkAvailable() {
        //CHECK IF NETWORK IS AVAILABLE
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        return true;
    }

    private boolean isLocationAvailable() {
        return true;
        //CHECK IF LOCATION SERVICE IS ENABLED
//        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//
//        try {
//            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch (Exception ex) {
//
//        }
//
//        try
//
//        {
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch (Exception ex) {
//
//        }
//
//        if (gps_enabled && network_enabled) {
//            return true;
//        }
//        return false;
    }

    private void callPlaceAutocompleteActivityIntent() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
//PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }

    }

    private void countDownToShare() {
        countDown = new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d("COUNTDOWN", "seconds remaining to share experience: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                showShareYourExperience();
            }
        };
    }

    private void showAddRestroomDialog(final LatLng latLng) {
        //FOR BUSINESS OWNERS WHEN ADDING RESTROOMS
        //START
        if (mAuth.getCurrentUser().isAnonymous() || !_USER_TYPE.equals("business owner")) {
            return;
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_layout_add_restroom, null);

        dialogBuilder.setView(dialogView);

        //CONNECTORS
        viewPagerAddRestroom = (ViewPager) dialogView.findViewById(R.id.viewPagerAddImages);
        ibChageImages = (ImageButton) dialogView.findViewById(R.id.imageButtonChangeImages);
        sliderDotsPanel = (LinearLayout) dialogView.findViewById(R.id.sliderDots);
        ibAddRestroomImage = (ImageButton) dialogView.findViewById(R.id.imageButtonAddRestroomImage);
        final TextView tvFloorNumber = (TextView) dialogView.findViewById(R.id.textViewFloorNumber);
        final TextView tvRestroomName = (TextView) dialogView.findViewById(R.id.textViewRestroomName);
        final ImageButton ibReport = (ImageButton) dialogView.findViewById(R.id.imageButtonReport);
        final ImageButton ibBack = (ImageButton) dialogView.findViewById(R.id.imageButtonBack);
        final ImageButton ibAddRestroom = (ImageButton) dialogView.findViewById(R.id.imageButtonAddRestroom);
        final Spinner spinnerFloorType = (Spinner) dialogView.findViewById(R.id.spinnerFloorChoices);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        //INITIAL SETUP OF DIALOG
        imagesEncodedList = null;
        tvFloorNumber.setTypeface(typeface_bold);
        tvRestroomName.setTypeface(typeface_bold);
        tvRestroomName.setSelected(true);
        tvRestroomName.setSingleLine(true);
        spinnerFloorType.setSelection(1);
        //PANG CLOSE NG ALERT DIALOG
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        //ADDING RESTROOM
        ibAddRestroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GETTING RESTROOM INFORMATIONS BEFORE ADDING
                String restroomLocation = latLng.latitude + ", " + latLng.longitude;
                String name = tvRestroomName.getText().toString();
                String floorLevel = "";

                switch (spinnerFloorType.getSelectedItemPosition()) {
                    case (0):
                        floorLevel = "LG";
                        break;
                    case (1):
                        floorLevel = "GF";
                        break;
                    case (2):
                        floorLevel = "2F";
                        break;
                    case (3):
                        floorLevel = "3F";
                        break;
                    case (4):
                        floorLevel = "4F";
                        break;
                    case (5):
                        floorLevel = "5F";
                        break;
                    case (6):
                        floorLevel = "6F";
                        break;
                    case (7):
                        floorLevel = "7F";
                        break;
                    case (8):
                        floorLevel = "8F";
                        break;
                    case (9):
                        floorLevel = "9F";
                        break;
                    case (10):
                        floorLevel = "10F";
                        break;
                }

                if (!TextUtils.isEmpty(name) && imagesEncodedList != null) {
                    //ADDING RESTROOM
                    addRestroom(restroomLocation, name, floorLevel);
                    alertDialog.dismiss();

                } else {
                    //PROMPT USER TO ADD RESTROOM IMAGE
                    showAddRestroomNameImage();
                }
            }
        });

        ibAddRestroomImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkGalleryPermission()) {
                    //GO TO GALLERY INTENT
                    //SELECTING IMAGES
                    Intent intent = new Intent();
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    intent.setType("image/*");

                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_INTENT);
                }
            }
        });

        ibChageImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GO TO GALLERY INTENT
                //CHANGE THE SELECTED IMAGES
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);

                intent.setType("image/*");

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_INTENT);
            }
        });

        //GETTING BUSINESS NAME
        vel_businesses = databaseBusinesses.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String businessName = dataSnapshot.child("business_name").getValue().toString();
                tvRestroomName.setText(businessName);
                databaseBusinesses.child(user.getUid()).removeEventListener(vel_businesses);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //PUTTING DOTS INDICATOR WHEN AN IMAGE IS SELECTED
        viewPagerAddRestroom.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotsCounts; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_nonactive_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //END
    }

    private void showRestroomInformation(final String restroomID, final String restroomName, final LatLng latLng, final String businessID) {
        //WHEN CLICKING MARKERS
        //START
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_layout_view_restroom_information, null);

        dialogBuilder.setView(dialogView);

        //CONNECTORS
        final ViewPager vpImages = (ViewPager) dialogView.findViewById(R.id.viewPagerImages);
        final LinearLayout sliderDotsPanel = (LinearLayout) dialogView.findViewById(R.id.sliderDots);
        final LinearLayout llRestroomAvailability = (LinearLayout) dialogView.findViewById(R.id.linearLayoutRestroomAvailability);
        final TextView tvRestroomName = (TextView) dialogView.findViewById(R.id.textViewRestroomName);
        final TextView tvBeTheFirstToReview = (TextView) dialogView.findViewById(R.id.textViewBeTheFirstToReview);
        final TextView tvIsThisOutOfOrder = (TextView) dialogView.findViewById(R.id.textViewIsThisRestroomOutOfOrder);
        final TextView tvRatingBar = (TextView) dialogView.findViewById(R.id.textViewRatingBar);
        final EditText etReview = (EditText) dialogView.findViewById(R.id.editTextReview);
        final ImageButton ibReport = (ImageButton) dialogView.findViewById(R.id.imageButtonReport);
        final ImageButton ibBack = (ImageButton) dialogView.findViewById(R.id.imageButtonBack);
        final ImageButton ibBackReview = (ImageButton) dialogView.findViewById(R.id.imageButtonBackReview);
        final ImageButton ibPostReview = (ImageButton) dialogView.findViewById(R.id.imageButtonAddReview);
        final ImageButton ibGetDirection = (ImageButton) dialogView.findViewById(R.id.imageButtonGetDirections);
        final ImageButton ibSubmitReview = (ImageButton) dialogView.findViewById(R.id.imageButtonSubmitReview);
        final ImageButton ibDeleteRestroom = (ImageButton) dialogView.findViewById(R.id.imageButtonDeleteRestroom);
        final RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.ratingBar);
        final ListView listViewReview = (ListView) dialogView.findViewById(R.id.listViewReview);
        final Switch sYesNo = (Switch) dialogView.findViewById(R.id.switchYesNo);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        //INITIAL SETUP OF DIALOG
        tvRestroomName.setTypeface(typeface_bold);
        tvRestroomName.setSelected(true);
        tvRestroomName.setSingleLine(true);
        tvIsThisOutOfOrder.setTypeface(typeface_bold);
        etReview.setTypeface(typeface_normal);
        tvRatingBar.setTypeface(typeface_bold);
        tvBeTheFirstToReview.setTypeface(typeface_bold);
        tvRestroomName.setText(restroomName);
        alreadyReported = false;
        //DISMISSING THE DIALOG WHEN CLICKING BACK
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        ibBackReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mAuth.getCurrentUser().isAnonymous()) {
                    ibReport.setVisibility(View.VISIBLE);
                    ibSubmitReview.setVisibility(View.GONE);
                    ibBackReview.setVisibility(View.GONE);
                    etReview.setVisibility(View.GONE);
                    ibPostReview.setVisibility(View.VISIBLE);
                    listViewReview.setVisibility(View.VISIBLE);
                    ibGetDirection.setVisibility(View.VISIBLE);
                    ratingBar.setIsIndicator(true);
                    ratingBar.setStepSize(0);

                    vel_ratings = databaseRatings.child(businessID).child(restroomID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ratingList.clear();

                            //VARIABLES FOR GETTING THE TALLY OF USERS
                            int _ones = 0, _twos = 0, _threes = 0, _fours = 0, _fives = 0;

                            for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                                Rating rating = ratingSnapshot.getValue(Rating.class);

                                Log.d("RATING", String.valueOf(ratingSnapshot.child("user_rating").getValue()));

                                //ACCUMULATING THE USERS WHO RATE THE RESTROOMS
                                switch (String.valueOf(ratingSnapshot.child("user_rating").getValue())) {

                                    case "1":
                                        _ones++;
                                        break;
                                    case "2":
                                        _twos++;
                                        break;
                                    case "3":
                                        _threes++;
                                        break;
                                    case "4":
                                        _fours++;
                                        break;
                                    case "5":
                                        _fives++;
                                        break;

                                }
                                ratingList.add(rating);
                            }

                            if (_ones != 0 || _twos != 0 || _threes != 0 || _fours != 0 || _fives != 0) {

                                //COMPUTATION OF AVERAGE RATING
                                float _rating = (ratingComputation(_ones, _twos, _threes, _fours, _fives));
                                ratingBar.setRating(_rating);
                                String s = String.format("%.2f", _rating);
                                tvRatingBar.setVisibility(View.VISIBLE);
                                tvRatingBar.setText(s);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(MapsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        //REPORTING RESTROOM
        ibReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vel_reports = databaseReports.child(businessID).child(restroomID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //CHECK IF THERE IS ALREADY A REPORT
                        if (dataSnapshot.getChildrenCount() == 0) {
                            if (isNetworkAvailable() && isLocationAvailable()) {
                                alertDialog.dismiss();
                                showReportChoices(restroomID, restroomName, businessID, latLng);
                            } else {
                                Toast.makeText(MapsActivity.this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                            }
                            databaseReports.child(businessID).child(restroomID).removeEventListener(vel_reports);
                            return;
                        } else {
                            for (DataSnapshot reportsSnapshot : dataSnapshot.getChildren()) {
                                //LOOP IN EVERY REPORT IF THE USER ALREADY REPORTED THE RESTROOM
                                final String _KEY = reportsSnapshot.getKey();
                                vel_reports_key = databaseReports.child(businessID).child(restroomID).child(_KEY).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            String name = dataSnapshot.child("user").getValue().toString();
                                            Log.d("REPORTER_USER", name);
                                            if (name.equals(_USER_EMAIL)) {
                                                //USER ALREADY REPORT
                                                showAlreadySubmittedAReport();
                                                alreadyReported = true;
                                                databaseReports.child(businessID).child(restroomID).child(_KEY).removeEventListener(vel_reports_key);
                                                databaseReports.child(businessID).child(restroomID).removeEventListener(vel_reports);
                                                progressDialog.dismiss();
                                                return;
                                            } else {
                                                databaseReports.child(businessID).child(restroomID).child(_KEY).removeEventListener(vel_reports_key);
                                                progressDialog.dismiss();
                                                return;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            //IF USER DOESNT SUBMITTED A REPORT ON THIS RESTROOM
                            if (alreadyReported == false) {
                                databaseReports.child(businessID).child(restroomID).removeEventListener(vel_reports);
                                progressDialog.dismiss();
                                if (isNetworkAvailable() && isLocationAvailable()) {
                                    //SHOW REPORT CHOICES
                                    alertDialog.dismiss();
                                    showReportChoices(restroomID, restroomName, businessID, latLng);
                                } else {
                                    Toast.makeText(MapsActivity.this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }

                            databaseReports.child(businessID).child(restroomID).removeEventListener(vel_reports);
                            return;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MapsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //GETTING DIRECTION
        ibGetDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable() && isLocationAvailable()) {
                    //CLEAR 2 POINTS IN ARRAY LIST
                    MarkerPoints.clear();

                    if (mLastLocation == null) {
                        showYouMustTurnOnGPS();
                        return;
                    }

                    //ADD POINTS IN ARRAY LIST
                    //CURRENT LOCATION
                    MarkerPoints.add(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    //DESTINATION LOCATION
                    MarkerPoints.add(latLng);

                    if (MarkerPoints.size() >= 2) {
                        //GET ORIGIN AND DESTINATION POINTS
                        LatLng origin = MarkerPoints.get(0);
                        LatLng dest = MarkerPoints.get(1);

                        //CLEAR THE MAP OR IF THERE IS EXISTING POLYLINES
                        mMap.clear();

                        //RETRIEVE ALL THE RESTROOMS AFTER CLEARING THE MAP
                        retrieveRestrooms();

                        //GETTING URL TO THE GOOLGE DIRECTIONS API
                        String url = getUrl(origin, dest);
                        Log.d("onMapClick", url.toString());
                        FetchUrl FetchUrl = new FetchUrl();

                        //START DOWNLOADING JSON DATA FROM GOOGLE DIRECTIONS API
                        FetchUrl.execute(url);

                        //MOVE MAP CAMERA
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

                        if (_USER_TYPE.equals("business owner")) {
                            fabAddRestroom.setVisibility(View.GONE);
                        }

                        fabReset.setVisibility(View.VISIBLE);
                        alertDialog.dismiss();
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //POSTING A REVIEW
        ibPostReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HIDE THE UNNECESSARY VIEWS
                if (!mAuth.getCurrentUser().isAnonymous()) {
                    ibReport.setVisibility(View.INVISIBLE);
                    ibSubmitReview.setVisibility(View.VISIBLE);
                    ibBackReview.setVisibility(View.VISIBLE);
                    etReview.setVisibility(View.VISIBLE);
                    ibPostReview.setVisibility(View.GONE);
                    listViewReview.setVisibility(View.GONE);
                    ibGetDirection.setVisibility(View.GONE);
                    tvRatingBar.setText("0.0");
                    ratingBar.setIsIndicator(false);
                    ratingBar.setRating(0);
                    ratingBar.setStepSize(1);
                    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                            tvRatingBar.setText(String.valueOf(v));
                            tvRatingBar.setVisibility(View.VISIBLE);
                        }
                    });

                } else {
                    showCreateAnAccountToRateReview();
                }
            }
        });

        ibSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable() && isLocationAvailable()) {
                    //GET USER REVIEW AND RATINGS IN TEXT FIELDS
                    String _userReview = etReview.getText().toString();
                    float _userRating = ratingBar.getRating();

                    if (!TextUtils.isEmpty(_userReview) && ratingBar.getRating() != 0) {

                        //CALLING ADDING REVIEW AND RATING METHOD
                        if (!mAuth.getCurrentUser().isAnonymous()) {
                            progressDialog.setMessage("Posting review and rating...");
                            progressDialog.setIndeterminate(true);
                            progressDialog.show();
                            addReview(restroomID, restroomName, _USER_EMAIL, _userReview, _userRating, businessID);
                            addRating(restroomID, restroomName, _USER_EMAIL, _userRating, businessID);
                            showRateAndReviewPosted(restroomID, restroomName, latLng, businessID);

                            //MAKING VIEWS VISIBLE AFTER POSTING REVIEWS
                            ibPostReview.setVisibility(View.VISIBLE);
                            ibSubmitReview.setVisibility(View.GONE);
                            etReview.setVisibility(View.GONE);
                            ibGetDirection.setVisibility(View.VISIBLE);
                            listViewReview.setVisibility(View.VISIBLE);
                            ratingBar.setIsIndicator(true);

                            progressDialog.dismiss();
                            alertDialog.dismiss();
                        }

                    } else if (TextUtils.isEmpty(_userReview)) {
                        showMustWriteAReview();
                    } else if (ratingBar.getRating() == 0) {
                        showMustRateExperience();
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            vel_reports = databaseReports.child(businessID).child(restroomID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() == 0) {
                        databaseReports.child(businessID).child(restroomID).removeEventListener(vel_reports);
                        return;
                    } else {
                        for (DataSnapshot reportsSnapshot : dataSnapshot.getChildren()) {
                            final String _KEY = reportsSnapshot.getKey();
                            vel_reports_key = databaseReports.child(businessID).child(restroomID).child(_KEY).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        String name = dataSnapshot.child("user").getValue().toString();
                                        Log.d("REPORTER_USER", name);
                                        if (name.equals(_USER_EMAIL)) {
                                            alreadyReported = true;
                                            databaseReports.child(businessID).child(restroomID).child(_KEY).removeEventListener(vel_reports_key);
                                            databaseReports.child(businessID).child(restroomID).removeEventListener(vel_reports);
                                            progressDialog.dismiss();
                                            return;
                                        } else {
                                            databaseReports.child(businessID).child(restroomID).child(_KEY).removeEventListener(vel_reports_key);
                                            progressDialog.dismiss();
                                            return;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        databaseReports.child(businessID).child(restroomID).removeEventListener(vel_reports);
                        return;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MapsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            vel_ratings = databaseRatings.child(businessID).child(restroomID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ratingList.clear();

                    //VARIABLES FOR GETTING THE TALLY OF USERS
                    int _ones = 0, _twos = 0, _threes = 0, _fours = 0, _fives = 0;

                    for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                        Rating rating = ratingSnapshot.getValue(Rating.class);

                        Log.d("RATING", String.valueOf(ratingSnapshot.child("user_rating").getValue()));

                        //ACCUMULATING THE USERS WHO RATE THE RESTROOMS
                        switch (String.valueOf(ratingSnapshot.child("user_rating").getValue())) {

                            case "1":
                                _ones++;
                                break;
                            case "2":
                                _twos++;
                                break;
                            case "3":
                                _threes++;
                                break;
                            case "4":
                                _fours++;
                                break;
                            case "5":
                                _fives++;
                                break;

                        }
                        ratingList.add(rating);
                    }

                    if (_ones != 0 || _twos != 0 || _threes != 0 || _fours != 0 || _fives != 0) {

                        //COMPUTATION OF AVERAGE RATING
                        float _rating = (ratingComputation(_ones, _twos, _threes, _fours, _fives));
                        ratingBar.setRating(_rating);
                        String s = String.format("%.2f", _rating);
                        tvRatingBar.setVisibility(View.VISIBLE);
                        tvRatingBar.setText(s);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MapsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            vel_reviews = databaseReviews.child(businessID).child(restroomID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    reviewList.clear();

                    for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                        //GETTING REVIEWS
                        Review review = reviewSnapshot.getValue(Review.class);
                        reviewList.add(review);
                    }
                    //SHOWING REVIEWS IN LIST
                    ReviewList adapter = new ReviewList(MapsActivity.this, reviewList);
                    listViewReview.setAdapter(adapter);

                    if(listViewReview.getAdapter().getCount()==0){
                        listViewReview.setVisibility(View.GONE);
                        tvBeTheFirstToReview.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MapsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }

            });

            vel_restrooms = databaseRestrooms.child(businessID).child(restroomID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //GET DATA IN THE SNAPSHOT
                    try {
                        String enabled = dataSnapshot.child("enabled").getValue().toString();
                        String businessOwner = dataSnapshot.child("restroom_owner").getValue().toString();
                        final int report_counts = Integer.parseInt(dataSnapshot.child("report_counts").getValue().toString());
                        String floorNumber = dataSnapshot.child("restroom_floor").getValue().toString();
                        tvRestroomName.setText("(" + floorNumber + ") " + restroomName);
                        if (businessOwner.equals(_USER_EMAIL)) {
                            //IF THE USER VIEWING THE RESTROOM IS THE OWNER OF THE RESTROOM
                            tvBeTheFirstToReview.setText(getResources().getString(R.string.be_the_first_to_review_owner));
                            ibPostReview.setVisibility(View.GONE);
                            ibGetDirection.setVisibility(View.GONE);
                            ibReport.setVisibility(View.INVISIBLE);
                            tvIsThisOutOfOrder.setVisibility(View.VISIBLE);
                            llRestroomAvailability.setVisibility(View.VISIBLE);
                            sYesNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isNetworkAvailable() && isLocationAvailable()) {
                                        if (!isChecked) {
                                            //IF SWITCH IS NOT CHECKED
                                            alertDialog.dismiss();
                                            showConfirmRestroomAvailability(sYesNo, businessID, restroomID);
                                            databaseRestrooms.child(businessID).child(restroomID).removeEventListener(vel_restrooms);
                                            dialogView.setBackgroundResource(R.drawable.dialog_bg_disabled);
                                        } else {
                                            //CHECK IF THE RESTROOM IS REPORTED 7 TIME AND DISABLED
                                            databaseRestrooms.child(businessID).child(restroomID).child("enabled").setValue("yes");
                                            if (report_counts >= 7) {
                                                //RE ENABLING WILL MAKE THE REPORT COUNTS BACK TO 0
                                                databaseRestrooms.child(businessID).child(restroomID).child("report_counts").setValue(0);
                                                databaseReports.child(businessID).child(restroomID).removeValue();
                                            }
                                            dialogView.setBackgroundResource(R.drawable.dialog_bg);
                                            ibPostReview.setBackgroundResource(R.drawable.btn_rate_and_review);
                                            ibGetDirection.setBackgroundResource(R.drawable.btn_get_direction);
                                            databaseRestrooms.child(businessID).child(restroomID).removeEventListener(vel_restrooms);
                                            retrieveRestrooms();

                                        }
                                    } else {
                                        Toast.makeText(MapsActivity.this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            ibDeleteRestroom.setVisibility(View.VISIBLE);
                            ibDeleteRestroom.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (isNetworkAvailable() && isLocationAvailable()) {
                                        alertDialog.dismiss();
                                        showAreYouSureDeleteRestroom(businessID, restroomID);
                                    } else {
                                        Toast.makeText(MapsActivity.this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        if (enabled.equals("no")) {
                            //IF THE RESTROOM IS DISABLED
                            if (!businessOwner.equals(_USER_EMAIL)) {
                                showThisRestroomIsDisabled();
                            }
                            dialogView.setBackgroundResource(R.drawable.dialog_bg_disabled);
                            ibPostReview.setImageResource(R.drawable.btn_rate_and_review_disabled);
                            ibGetDirection.setImageResource(R.drawable.btn_get_direction_disabled);
                            sYesNo.setChecked(false);
                            ibReport.setEnabled(false);
                            ibPostReview.setEnabled(false);
                            ibGetDirection.setEnabled(false);
                        } else {
                            //IF THE RESTROOM IS ENABLED
                            dialogView.setBackgroundResource(R.drawable.dialog_bg);
                            sYesNo.setChecked(true);
                            ibReport.setEnabled(true);
                            ibPostReview.setEnabled(true);
                        }
                    } catch (Exception e) {
                        alertDialog.dismiss();
                        showRestroomNotAvailable();
                        retrieveRestrooms();
                    }
                    databaseRestrooms.child(businessID).child(restroomID).removeEventListener(vel_restrooms);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MapsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            vel_images = databaseImages.child(businessID).child(restroomID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ViewPagerAdapter adapter;

                    List<String> imagesToDownload;
                    imagesToDownload = new ArrayList<>();

                    //RETRIEVING IMAGES DOWNLOAD URLs
                    int x = 0;
                    for (DataSnapshot imagesSnapshot : dataSnapshot.getChildren()) {
                        String child_name = imagesSnapshot.getKey();
                        Log.d("IMAGE_CHILD", child_name);

                        String download_url = imagesSnapshot.child("download_url").getValue().toString();
                        imagesToDownload.add(download_url);
                        Log.d("DL_URL", imagesToDownload.get(x));
                        x++;
                    }

                    adapter = new ViewPagerAdapter(imagesToDownload, MapsActivity.this);
                    vpImages.setAdapter(adapter);

                    final int dotsCounts;
                    final ImageView[] dots;

                    dotsCounts = adapter.getCount();
                    dots = new ImageView[dotsCounts];
                    sliderDotsPanel.removeAllViews();

                    for (int i = 0; i < dotsCounts; i++) {
                        dots[i] = new ImageView(MapsActivity.this);
                        dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_nonactive_dot));

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(8, 0, 8, 0);

                        try {
                            dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active_dot));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            sliderDotsPanel.addView(dots[i], params);
                        } catch (Exception e) {
                            Toast.makeText(MapsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        vpImages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int position) {
                                try {
                                    for (int i = 0; i < dotsCounts; i++) {
                                        dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_nonactive_dot));
                                    }
                                    dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active_dot));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MapsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            databaseRatings.child(businessID).child(restroomID).removeEventListener(vel_ratings);
            databaseReviews.child(businessID).child(restroomID).removeEventListener(vel_reviews);
            alertDialog.dismiss();
            showRestroomNotAvailable();
            retrieveRestrooms();
        }

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                //REMOVE ALL VALUE EVENT LISTENER
                databaseRatings.child(businessID).child(restroomID).removeEventListener(vel_ratings);
                databaseReviews.child(businessID).child(restroomID).removeEventListener(vel_reviews);
                databaseRestrooms.child(businessID).child(restroomID).removeEventListener(vel_restrooms);
                databaseReports.child(businessID).child(restroomID).removeEventListener(vel_reports);
                databaseImages.child(businessID).child(restroomID).removeEventListener(vel_images);

            }
        });

        progressDialog.dismiss();
        //END
    }

    private void showReportChoices(final String restroomID, final String restroomName, final String businessID, final LatLng latLng) {
        //START

        if (mAuth.getCurrentUser().isAnonymous()) {
            showCreateAnAccount();
            return;
        }


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dalog_layout_report_restroom, null);

        dialogBuilder.setView(dialogView);

        //CONNECTOR
        Spinner sReportChoices = (Spinner) dialogView.findViewById(R.id.spinnerReportChoices);
        ImageButton ibSubmitReport = (ImageButton) dialogView.findViewById(R.id.imageButtonSubmitReport);
        ImageButton ibCancel = (ImageButton) dialogView.findViewById(R.id.imageButtonCancelReport);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        final String _reportChoice = sReportChoices.getSelectedItem().toString();

        ibSubmitReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SUBMITTING A REPORT
                addReport(restroomID, restroomName, _USER_EMAIL, _reportChoice, businessID, latLng);
                alertDialog.dismiss();

            }
        });

        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                databaseRatings.child(businessID).child(restroomID).removeEventListener(vel_ratings);
                databaseReviews.child(businessID).child(restroomID).removeEventListener(vel_reviews);
                databaseRestrooms.child(businessID).child(restroomID).removeEventListener(vel_restrooms);
                databaseReports.child(businessID).child(restroomID).removeEventListener(vel_reports);
                databaseImages.child(businessID).child(restroomID).removeEventListener(vel_images);

            }
        });
        //END
    }

    private void addRating(String restroomId, String restroomName, String user, float userRating, String businessID) {
        //START
        if (mAuth.getCurrentUser().isAnonymous()) {
            return;
        }

        //ADDING RATING
        if (userRating != 0) {

            //GENERATE AN ID
            String id = databaseRatings.push().getKey();
            //PUSHING TO DATABASE
            Rating rating = new Rating(id, restroomName, user, userRating);
            databaseRatings.child(businessID).child(restroomId).child(id).setValue(rating);

            Log.d("RESTROOM_ID", restroomId);
            Log.d("RATING_ID", id);
            Log.d("USER_EMAIL", user);
            Log.d("USER_RATING", String.valueOf(userRating));

        } else {
            showMustRateExperience();
        }
        //END
    }

    private void addReview(String restroomId, String restroomName, String user, String userReview, float userRating, String businessID) {
        //START
        if (mAuth.getCurrentUser().isAnonymous()) {
            return;
        }

        //ADDING REVIEW
        if (TextUtils.isEmpty(userReview)) {
            showMustWriteAReview();
        } else {
            //GENERATE AN ID
            String id = databaseReviews.push().getKey();
            //PUSHING TO DATABASE
            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = df.format(c.getTime());
            Review review = new Review(id, restroomName, user, userReview, userRating, formattedDate);
            databaseReviews.child(businessID).child(restroomId).child(id).setValue(review);

            Log.d("RESTROOM_ID", restroomId);
            Log.d("REVIEW_ID", id);
            Log.d("RESTROOM_NAME", restroomName);
            Log.d("USER_REVIEW", userReview);
        }
        //END
    }

    private void addRestroom(final String latlng, final String restroomName, final String floorLevel) {
        //START
        if (mAuth.getCurrentUser().isAnonymous()) {
            return;
        }
        //ADDING RESTROOM
        final String enabled = "yes";
        final int reportCounts = 0;
        final String id = databaseRestrooms.push().getKey();
        final String _business_id;

        _business_id = user.getUid();
        //PUSHING TO DATABASE
        Restroom restroom = new Restroom(id, _business_id, latlng, restroomName, enabled, _USER_EMAIL, floorLevel, reportCounts);
        databaseRestrooms.child(_business_id).child(id).setValue(restroom).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                addRestroomImage(id, restroomName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        Log.d("RESTROOM_ID", id);
        Log.d("RESTROOM_LAT_LNG", latlng);
        Log.d("RESTROOM_NAME", restroomName);

        retrieveRestrooms();
        //END
    }

    private void addReport(final String restroomId, final String restroomName, final String user, final String reportChoice, final String businessID, final LatLng latLng) {
        //START
        if (mAuth.getCurrentUser().isAnonymous()) {
            return;
        }
        progressDialog.setMessage("Submitting report...");
        progressDialog.show();

        //CALL REPORT RESTROOM METHOD
        reportRestroom(restroomId, businessID, restroomName, latLng);

        //CREATING ID
        String id = databaseReports.push().getKey();

        //ADDING TO DATABASE
        Report report = new Report(id, restroomId, reportChoice, restroomName, user);
        databaseReports.child(businessID).child(restroomId).child(id).setValue(report);

        Log.d("RESTROOM_ID", restroomId);
        Log.d("REPORT_CHOICE", reportChoice);
        Log.d("RESTROOM_NAME", restroomName);
        Log.d("USER_EMAIL", user);

        progressDialog.dismiss();
        //END
    }

    private void addRestroomImage(final String restroomId, final String restroomName) {
        //START
        //CHECK KUNG MAY LAMAN
        if (imagesEncodedList != null) {
            String newRestroomName = restroomName;
            newRestroomName = newRestroomName.replace(".", "");
            newRestroomName = newRestroomName.replace("#", "");
            newRestroomName = newRestroomName.replace("$", "");
            newRestroomName = newRestroomName.replace("[", "");
            newRestroomName = newRestroomName.replace("]", "");

            final String _business_id = user.getUid();
            //LOOP PANG UPLOAD NG PICTURE
            for (int x = 0; x < imagesEncodedList.size(); x++) {
                //PANG CREATE NG REFERENCE SA STORAGE

                StorageReference filePath = storageRestroomImage.child("images").child("restrooms").child(_business_id).child(restroomId).child(newRestroomName + String.valueOf(x));

                final String fileName = newRestroomName + String.valueOf(x);
                //PANG UPLOAD
                filePath.putFile(Uri.parse(imagesEncodedList.get(x))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("UPLOAD", ":onSuccess:done uploading");

                        //PANG ADD SA DATABASE
                        final String id = databaseImages.push().getKey();
                        Image image = new Image(id, fileName, String.valueOf(taskSnapshot.getDownloadUrl()), _business_id);
                        Log.d("FILENAME", fileName);
                        databaseImages.child(_business_id).child(restroomId).child(id).setValue(image);

                        fabAddRestroom.setVisibility(View.VISIBLE);
                        fabCancel.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            showSuccessAddedRestroom();
        } else {
            Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show();
        }
        //END
    }

    private float ratingComputation(int ones, int twos, int threes, int fours, int fives) {
        //START
        //COMPUTING THE AVERAGE RATING OF A RESTROOM

        //VARIABLES
        float averageRating, _totalRatings;
        float _totalRespondents, _totalOnes, _totalTwos, _totalThrees, _totalFours, _totalFives;
        //TOTAL RESPONDENTS ARE THE TOTAL NUMBER OF PEOPLE WHO RATE A RESTROOM
        _totalRespondents = ones + twos + threes + fours + fives;

        //TOTAL ONES-FIVES ARE THE WEIGHTED MEAN
        _totalOnes = ones * 1;
        _totalTwos = twos * 2;
        _totalThrees = threes * 3;
        _totalFours = fours * 4;
        _totalFives = fives * 5;

        //TOTALRATINGS ARE THE SUMMATION OF WEIGHTED MEAN
        _totalRatings = _totalOnes + _totalTwos + _totalThrees + _totalFours + _totalFives;

        //AVERAGE RATING IS THE TOTAL NUMBER OF RATINGS OF A RESTROOM RECEIVED DIVIDED BY THE NUMBER OF RESPONDENTS
        averageRating = _totalRatings / _totalRespondents;

        Log.d("TOTAL_RESPONDENTS", String.valueOf(_totalRespondents));
        Log.d("TOTAL_RATINGS", String.valueOf(_totalRatings));
        Log.d("AVERAGE_RATINGS", String.valueOf(averageRating));

        return averageRating;
        //END
    }

    private void reportRestroom(final String restroomId, final String businessID, final String restroomName, final LatLng latLng) {
        //START
        if (mAuth.getCurrentUser().isAnonymous()) {
            return;
        }

        progressDialog.setMessage("Reporting restroom...");
        progressDialog.show();

        vel_restrooms = databaseRestrooms.child(businessID).child(restroomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //GET REPORT COUNTS
                int _reportCounts = Integer.parseInt(String.valueOf(dataSnapshot.child("report_counts").getValue()));
                Log.d("REPORT_COUNTS", String.valueOf(_reportCounts));

                _reportCounts++;
                Log.d("NEW_REPORT_COUNT", String.valueOf(_reportCounts));

                if (_reportCounts > 7) {
                    //DISABLING A RESTROOM IF IT GETS 8 VOTES
                    databaseRestrooms.child(businessID).child(restroomId).child("enabled").setValue("no")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    showReportSubmitted(restroomId, restroomName, latLng, businessID);
                                }
                            });

                    databaseRestrooms.child(businessID).child(restroomId).removeEventListener(vel_restrooms);
                    return;
                } else {
                    //ADDING +1 IN REPORT COUNTS
                    databaseRestrooms.child(businessID).child(restroomId).child("report_counts").setValue(_reportCounts)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    showReportSubmitted(restroomId, restroomName, latLng, businessID);
                                }
                            });
                    databaseRestrooms.child(businessID).child(restroomId).removeEventListener(vel_restrooms);
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog.dismiss();
        //END
    }

    private void retrieveRestrooms() {
        //RETRIEVING RESTROOMS
        mMap.clear();
        //NORMAL RESTROOM ENABLED
        Drawable enableDrawable = getResources().getDrawable(R.drawable.ic_logo_marker);
        final BitmapDescriptor markerIconEnabled = getMarkerIconFromDrawable(enableDrawable);
        //NORMAL RESTROOM DISABLED
        Drawable disableDrawable = getResources().getDrawable(R.drawable.ic_logo_marker_disabled);
        final BitmapDescriptor markerIconDisabled = getMarkerIconFromDrawable(disableDrawable);
        //OWNER RESTROOM ENABLED
        Drawable ownerDrawable = getResources().getDrawable(R.drawable.ic_logo_marker_owner);
        final BitmapDescriptor markerIconOwner = getMarkerIconFromDrawable(ownerDrawable);
        //OWNER RESTROOM DISABLED
        Drawable ownerDisabledDrawable = getResources().getDrawable(R.drawable.ic_logo_marker_owner_disabled);
        final BitmapDescriptor markerIconOwnerDisabled = getMarkerIconFromDrawable(ownerDisabledDrawable);

        try {
            databaseBusinesses.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mMap.clear();
                    for (final DataSnapshot businessSnapshot : dataSnapshot.getChildren()) {
                        vel_restrooms = databaseRestrooms.child(businessSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                restroomList.clear();

                                for (DataSnapshot restroomSnapshot : dataSnapshot.getChildren()) {
                                    //GETTING RESTROOM INFORMATION
                                    String business_id = String.valueOf(restroomSnapshot.child("business_ID").getValue());
                                    String id = String.valueOf(restroomSnapshot.child("restroom_ID").getValue());
                                    String restroomName = String.valueOf(restroomSnapshot.child("restroom_name").getValue());
                                    String enabled = String.valueOf(restroomSnapshot.child("enabled").getValue());
                                    String[] latlong = String.valueOf(restroomSnapshot.child("location").getValue()).split(",");
                                    String restroomOwner = String.valueOf(restroomSnapshot.child("restroom_owner").getValue());

                                    double latitude = Double.parseDouble(latlong[0]);
                                    double longitude = Double.parseDouble(latlong[1]);

                                    LatLng _restroomLocationMarker = new LatLng(latitude, longitude);
                                    Restroom restroom = restroomSnapshot.getValue(Restroom.class);

                                    Log.d("RestroomName", restroomName);
                                    Log.d("LatLng", latlong[0] + ", " + latlong[1]);

                                    if (restroomOwner.equals(_USER_EMAIL) && enabled.equals("yes")) {
                                        //IF RESTROOM OWNER AND ENABLED
                                        mMap.addMarker(new MarkerOptions().position(_restroomLocationMarker).title(restroomName).snippet(id + ", " + business_id).icon(markerIconOwner));
                                    } else if (restroomOwner.equals(_USER_EMAIL) && enabled.equals("no")) {
                                        //IF RESTROOM OWNER AND DISABLED
                                        mMap.addMarker(new MarkerOptions().position(_restroomLocationMarker).title(restroomName).snippet(id + ", " + business_id).icon(markerIconOwnerDisabled));
                                    } else if (enabled.equals("yes")) {
                                        //IF ENABLED RESTROOM
                                        mMap.addMarker(new MarkerOptions().position(_restroomLocationMarker).title(restroomName).snippet(id + ", " + business_id).icon(markerIconEnabled));
                                    } else {
                                        //IF DISABLED RESTROOM
                                        mMap.addMarker(new MarkerOptions().position(_restroomLocationMarker).title(restroomName).snippet(id + ", " + business_id).icon(markerIconDisabled));
                                    }
                                    restroomList.add(restroom);
                                }
                                //                            databaseRestrooms.child(businessSnapshot.getKey()).removeEventListener(vel_restrooms);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(MapsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MapsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            retrieveRestrooms();
        }
    }

    private void retrieveOwnersRestrooms() {
        //RETRIEVE ALL RESTROOMS OWNED
        //OWNER RESTROOM ENABLED
        Drawable ownerDrawable = getResources().getDrawable(R.drawable.ic_logo_marker_owner);
        final BitmapDescriptor markerIconOwner = getMarkerIconFromDrawable(ownerDrawable);
        //OWNER RESTROOM DISABLED
        Drawable ownerDisabledDrawable = getResources().getDrawable(R.drawable.ic_logo_marker_owner_disabled);
        final BitmapDescriptor markerIconOwnerDisabled = getMarkerIconFromDrawable(ownerDisabledDrawable);

        try {
            databaseBusinesses.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mMap.clear();

                    Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_logo_marker_owner);
                    BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);

                    LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    addMarker = mMap.addMarker(new MarkerOptions().icon(markerIcon).position(latLng).snippet("new restroom"));

                    for (final DataSnapshot businessSnapshot : dataSnapshot.getChildren()) {
                        vel_restrooms = databaseRestrooms.child(businessSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                restroomList.clear();

                                for (DataSnapshot restroomSnapshot : dataSnapshot.getChildren()) {
                                    //GETTING RESTROOM INFORMATION
                                    String enabled = String.valueOf(restroomSnapshot.child("enabled").getValue());
                                    String[] latlong = String.valueOf(restroomSnapshot.child("location").getValue()).split(",");
                                    String restroomOwner = String.valueOf(restroomSnapshot.child("restroom_owner").getValue());

                                    double latitude = Double.parseDouble(latlong[0]);
                                    double longitude = Double.parseDouble(latlong[1]);

                                    LatLng _restroomLocationMarker = new LatLng(latitude, longitude);
                                    Restroom restroom = restroomSnapshot.getValue(Restroom.class);

                                    Log.d("LatLng", latlong[0] + ", " + latlong[1]);

                                    if (restroomOwner.equals(_USER_EMAIL) && enabled.equals("yes")) {
                                        //IF RESTROOM OWNER AND ENABLED
                                        mMap.addMarker(new MarkerOptions().position(_restroomLocationMarker).icon(markerIconOwner));
                                    } else if (restroomOwner.equals(_USER_EMAIL) && enabled.equals("no")) {
                                        //IF RESTROOM OWNER AND DISABLED
                                        mMap.addMarker(new MarkerOptions().position(_restroomLocationMarker).icon(markerIconOwnerDisabled));
                                    }
                                    restroomList.add(restroom);
                                }
                                //                            databaseRestrooms.child(businessSnapshot.getKey()).removeEventListener(vel_restrooms);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(MapsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MapsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            retrieveRestrooms();
        }
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        //CONVERTING SVG IMAGE TO MARKER
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //FOR ROUTING DIRECTIONS START//

    private String getUrl(LatLng origin, LatLng dest) {

        //ORIGIN OF ROUTE
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        //DESTINATION OF ROUTE
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        //SENSOR ENABLED
        String sensor = "sensor=false";

        //BUILDING THE PARAMETER TO THE WEB SERVICE
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        //OUTPUT FORMAT
        String output = "json";

        //BUILDING THE URL TO THE WEB SERVICE
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            //CREATING AN HTTP CONNECTION TO COMMUNICATE WITH URL
            urlConnection = (HttpURLConnection) url.openConnection();

            //CONNECTIN TO URL
            urlConnection.connect();

            //READING DATA FROM URL
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    //FETCHES DATA FROM URL PASSED
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            //FOR STORING DATA FROM WEB SERVICE
            String data = "";

            try {
                //FETCHING THE DATA FROM WEB SERVICE
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            //INVOKES THE THREAD FOR PARSING THE JSON DATA
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        //PARSING THE DATA IN NON-UI THREAD
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                //STARTS PARSING DATA
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        //EXECUTES IN UI THREAD, AFTER THE PARSING PROCESS
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            distance = "";
            duration = "";

            //TRAVERSING THROUGH ALL THE ROUTES
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                //FETCHING i-th ROUTE
                List<HashMap<String, String>> path = result.get(i);

                //FETCHING ALL THE POINTS IN i-th ROUTE
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // GET DISTANCE FROM THE LIST
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // GET DURATION FROM THE LIST
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                //ADDING ALL THE POINTS IN THE ROUTE TO lineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.parseColor("#29ABE2"));

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");
            }

            //DRAWING POLYLINE IN THE GOOGLE MAP FOR THE i-th ROUTE
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("CURRENT_LATLNG", new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()) + "");
        //MOVE CAMERA ON FIRST RUN OF THE APP
        if (!didZoomedIn) {
            didZoomedIn = true;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        }

        //stop location updates
//        if (mGoogleApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    //FOR ROUTING DIRECTIONS END//

    //WARNINGS & PROMPTS
    private void showAddRestroomNameImage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_add_restroom_name_image, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewAddRestroomNameImage);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayAddRestroomNameImage);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showReportSubmitted(final String restroomID, final String restroomName, final LatLng latLng, final String businessID) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_success_report_submitted, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewReportSubmitted);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayReportSubmitted);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showRestroomInformation(restroomID, restroomName, latLng, businessID);
            }
        });
    }

    private void showConfirmRestroomAvailability(final Switch sYesNo, final String businessID, final String restroomID) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_confirm_restroom_availability, null);
        availabilityConfirmed = true;
        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewYouConfirmRestroomAvailability);
        ImageButton ibNo = (ImageButton) dialogView.findViewById(R.id.imageButtonNoConfirmRestroomAvailability);
        ImageButton ibYes = (ImageButton) dialogView.findViewById(R.id.imageButtonYesConfirmRestroomAvailability);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                sYesNo.setChecked(true);
                retrieveRestrooms();
            }
        });
        ibYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DISABLING THE RESTROOM
                alertDialog.dismiss();
                databaseRestrooms.child(businessID).child(restroomID).child("enabled").setValue("no");
                sYesNo.setChecked(false);
                retrieveRestrooms();
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                databaseRatings.child(businessID).child(restroomID).removeEventListener(vel_ratings);
                databaseReviews.child(businessID).child(restroomID).removeEventListener(vel_reviews);
                databaseRestrooms.child(businessID).child(restroomID).removeEventListener(vel_restrooms);
                databaseReports.child(businessID).child(restroomID).removeEventListener(vel_reports);
                databaseImages.child(businessID).child(restroomID).removeEventListener(vel_images);

            }
        });
    }

    private void showAreYouSureDeleteRestroom(final String businessID, final String restroomID) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_are_you_sure_delete_restroom, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewAreYouSureDeleteRestroom);
        ImageButton ibNo = (ImageButton) dialogView.findViewById(R.id.imageButtonNoAreYouSureDeleteRestroom);
        ImageButton ibYes = (ImageButton) dialogView.findViewById(R.id.imageButtonYesAreYouSureDeleteRestroom);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable() && isLocationAvailable()) {
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(MapsActivity.this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ibYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable() && isLocationAvailable()) {
                    databaseRestrooms.child(businessID).child(restroomID).removeValue();
                    databaseRatings.child(businessID).child(restroomID).removeValue();
                    databaseReviews.child(businessID).child(restroomID).removeValue();
                    databaseImages.child(businessID).child(restroomID).removeValue();
                    alertDialog.dismiss();
                    showRestroomDeleted();
                    retrieveRestrooms();
                } else {
                    Toast.makeText(MapsActivity.this, "You must be connected to the internet and turn on your location.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                databaseRestrooms.child(businessID).child(restroomID).removeEventListener(vel_restrooms);
                databaseRatings.child(businessID).child(restroomID).removeEventListener(vel_ratings);
                databaseReviews.child(businessID).child(restroomID).removeEventListener(vel_reviews);
                databaseImages.child(businessID).child(restroomID).removeEventListener(vel_images);

            }
        });
    }

    private void showRateAndReviewPosted(final String restroomID, final String restroomName, final LatLng latLng, final String businessID) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_success_rate_and_review_posted, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewRateAndReviewPosted);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayRateAndReviewPosted);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                fabReset.setVisibility(View.GONE);
                mMap.clear();
                retrieveRestrooms();
                //SEARCH USER TYPE
                if (_USER_TYPE.equals("business owner")) {
                    fabAddRestroom.setVisibility(View.VISIBLE);
                }
                showRestroomInformation(restroomID, restroomName, latLng, businessID);
            }
        });

    }

    private void showMustWriteAReview() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_must_write_a_review, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewMustWriteAReview);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayMustWriteAReview);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showAlreadySubmittedAReport() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_already_submitted_report, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewAlreadySubmittedReport);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayAlreadySubmittedReport);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showMustRateExperience() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_must_rate_your_experience, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewMustRateYourExperience);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayMustRateYourExperience);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showCreateAnAccount() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_create_an_account, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewAddCreateAnAccountToReport);
        ImageButton ibCancel = (ImageButton) dialogView.findViewById(R.id.imageButtonCancelAddCreateAnAccountToReport);
        ImageButton ibYes = (ImageButton) dialogView.findViewById(R.id.imageButtonYesAddCreateAnAccountToReport);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        ibYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, RegisterFirstStepActivity.class);
                i.putExtra("GMAIL", "no");
                i.putExtra("FACEBOOK", "no");
                startActivity(i);
            }
        });
    }

    private void showShareYourExperience() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_share_your_experience, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewShareYourExperience);
        ImageButton ibCancel = (ImageButton) dialogView.findViewById(R.id.imageButtonCancelShareYourExperience);
        ImageButton ibYes = (ImageButton) dialogView.findViewById(R.id.imageButtonYesShareYourExperience);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        try {
            alertDialog.show();

            ibCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            ibYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    try {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Have you tried this app? " + "https://drive.google.com/open?id=1qwTI0Wjx-ULxIZwxqi-KgoMaaDRk-jPN" + "\nTry \"Wash: A Restroom Locator Android Mobile Application\".");
                        sharingIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sharingIntent, "Share via:"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCreateAnAccountToRateReview() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_cannot_rate_or_review, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewCannotRateOrReview);
        ImageButton ibCancel = (ImageButton) dialogView.findViewById(R.id.imageButtonCancelCannotRateOrReview);
        ImageButton ibYes = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayCannotRateOrReview);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        ibYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, RegisterFirstStepActivity.class);
                i.putExtra("GMAIL", "no");
                i.putExtra("FACEBOOK", "no");
                startActivity(i);
            }
        });
    }

    private void showYouMustTurnOnGPS() {
        checkLocationPermission();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_you_must_turn_on_gps, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewYouMustTurnOnGPS);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayYouMustTurnOnGPS);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showSuccessAddedRestroom() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_success_added_restroom, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewSuccessAddedRestroom);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkaySuccessAddedRestroom);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showRestroomDeleted() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_success_restroom_deleted, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewRestroomDeleted);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayRestroomDeleted);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showRestroomNotAvailable() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_restroom_not_available, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewRestroomNotAvailable);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayRestroomNotAvailable);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showThisRestroomIsDisabled() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_warning_this_restroom_is_disabled, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewThisRestroomIsDisabled);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayThisRestroomIsDisabled);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showGuideAddingRestroom() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.alert_dialog_guide_adding_restroom, null);

        dialogBuilder.setView(dialogView);

        TextView tv = (TextView) dialogView.findViewById(R.id.textViewGuideAddingRestroom);
        ImageButton ibOkay = (ImageButton) dialogView.findViewById(R.id.imageButtonOkayGuideAddingRestroom);
        tv.setTypeface(typeface_bold);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ibOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

}
