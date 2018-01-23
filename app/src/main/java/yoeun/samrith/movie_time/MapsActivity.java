package yoeun.samrith.movie_time;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import static android.view.View.GONE;
//, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener{

    private GoogleMap mMap;
    private Database db;
    private ArrayList<Cinema> cinemas;
    private FloatingActionButton direction;
    private FloatingActionButton fab;
    private BottomSheetBehavior mBottomSheetBehavior;
    private View bottomSheet;
    private Button btnCall;
    private WebView web;
    private ProgressBar progressBar;
    private TextView txtDescription;
    private GoogleApiClient mGoogleApiClient = null;
    private MLocationListener mLocationListener;
    private Location mLastLocation;
    private String phoneNumber;
    private String origin;
    private String destination;
    private PolylineOptions polylineOptions;

    private LastLocation lastLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        /********   Obtain the SupportMapFragment and get notified when the map is ready to be used.   ********/

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

// for retrieving last known location

        NetworkManager.checkNetwork(this,findViewById(R.id.mainView));
        NetworkManager.checkGPS(this,findViewById(R.id.mainView));


        /********   Setting the xml component to the with the java code  ********/
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        direction = (FloatingActionButton) findViewById(R.id.direction);
        direction.setVisibility(GONE);
        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(polylineOptions!=null)polylineOptions.visible(false);
                mMap.clear();
                setCurrentLocation();
                initMap();
                polylineOptions=
                JSONRequest.GetDirection(getApplicationContext(),mMap,
                        origin,
                        destination


                );

            }
        });

        btnCall = (Button) findViewById(R.id.btnCall);


        web = (WebView) findViewById(R.id.webView);

        WebSettings settings = web.getSettings();
        web.getSettings().setBuiltInZoomControls(true);
        web.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        web.setScrollbarFadingEnabled(false);
        settings.setJavaScriptEnabled(true);
        web.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);

        txtDescription = (TextView) findViewById(R.id.textView);
        txtDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               switch (mBottomSheetBehavior.getState()){

//                   case BottomSheetBehavior.STATE_DRAGGING:
//                       onBackPressed();
//                       break;

                   case BottomSheetBehavior.STATE_COLLAPSED:
                       mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                       break;

                   case BottomSheetBehavior.STATE_EXPANDED:
                       onBackPressed();
                       break;

               }
            }
        });


/********   implementing BottomSheet Layout  ********/
        bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheet.setVisibility(GONE);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {

//                    case BottomSheetBehavior.STATE_DRAGGING:
//                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                        break;

                    case BottomSheetBehavior.STATE_HIDDEN:
                        fab.setVisibility(View.VISIBLE);
                        direction.setVisibility(View.GONE);
                        btnCall.setVisibility(View.GONE);

                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        fab.setVisibility(View.GONE);
                        direction.setVisibility(View.VISIBLE);
                        btnCall.setVisibility(View.GONE);

                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        btnCall.setVisibility(View.VISIBLE);
                        btnCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+phoneNumber));
                                startActivity(intent);
                            }
                        });
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

  /********   implementing BottomSheet Layout  ********/

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
        mMap.setInfoWindowAdapter(new MInfoWindowAdapter(this));
        mMap.setOnMarkerClickListener(this);



        /********    Connecting to the GoogleAPI Client   *******/
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
//
//        mGoogleApiClient.connect();
        lastLocation = new LastLocation(getApplicationContext());
        mLastLocation =lastLocation.getLastLocation();
        setCurrentLocation();

        /********    adding array of marker    *******/
        initMap();
//        mLocationListener = new MLocationListener() {
//            @Override
//            public void getLastKnownLocation(LatLng lastLocation) {
//            }
//        };


    }

    private void drawMarker(Cinema cinema) {


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(cinema.getLatlng())
                .title(cinema.getName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                .snippet(cinema.getLogo());

        mMap.addMarker(markerOptions).setTag(cinema.getPrice());
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if(marker.getTitle().equals("អ្នកនៅទីតាំងនេះ")) return false;

        int i =0;
        for(i=0;i<cinemas.size();i++){
            if(marker.getTitle().equals(cinemas.get(i).getName()))
                break;
        }

        origin =cinemas.get(i).getLat() + "," + cinemas.get(i).getLng();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(cinemas.get(i).getLatlng()));

        bottomSheet.setVisibility(View.VISIBLE);
        direction.setVisibility(View.VISIBLE);

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        fab.setVisibility(GONE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        web.getSettings().setJavaScriptEnabled(true);
        phoneNumber =cinemas.get(i).getPhone();

        txtDescription.setText(cinemas.get(i).getDescription());

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(getApplicationContext(), "Cannot load page", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
        web.loadUrl(cinemas.get(i).getUrl());
        return false;
    }

    @Override
    public void onClick(View view) {

//        setCurrentLocation();
        setCurrentLocation();


    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//
//
//        if(mLastLocation !=null){
//            LatLng lastLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//            mLocationListener.getLastKnownLocation(lastLocation);
//        }
//
//    }
//

//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }

    @Override
    public void onBackPressed() {
        switch (mBottomSheetBehavior.getState()){
            case BottomSheetBehavior.STATE_COLLAPSED:
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                break;

            case BottomSheetBehavior.STATE_DRAGGING:
            case BottomSheetBehavior.STATE_EXPANDED:
                       mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                       break;

            case BottomSheetBehavior.STATE_HIDDEN:
                finish();
                break;



        }
    }

    public void setCurrentLocation(){
        mLastLocation= lastLocation.getLastLocation();
        if(mLastLocation!=null){

//            drawMarker(new Cinema(111,"អ្នកនៅទីតាំងនេះ",(float)mLastLocation.getLatitude(),(float)mLastLocation.getLongitude(),"","","","",""));

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng((float)mLastLocation.getLatitude(),(float)mLastLocation.getLongitude()))
                    .title("អ្នកនៅទីតាំងនេះ")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .snippet("yourlocation.png");

            mMap.addMarker(markerOptions).setTag("you are here !");

            destination = mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))
//                    .title("អ្នកនៅទីតាំងនេះ")
//                    .snippet("you are here !")
//                    .icon(BitmapDescriptorFactory
//                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//
//            mMap.addMarker(markerOptions);
//            mMap.moveCamera(CameraUpdateFactory.newLatLng( new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude())));
            mMap.setMinZoomPreference(10.0f);
        }

    }

    public void initMap(){
        cinemas = new ArrayList<>();
        cinemas = db.getData(this).getData();



        int i;
        for (i = 0; i < cinemas.size(); i++) {
            drawMarker(cinemas.get(i));
        }


        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(cinemas.get(i - 1).getLat(), cinemas.get(i - 1).getLng())));
        mMap.setMinZoomPreference(10.0f);

    }
}
