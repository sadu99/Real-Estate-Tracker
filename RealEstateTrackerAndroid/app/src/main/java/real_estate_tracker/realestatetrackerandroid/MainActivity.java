package real_estate_tracker.realestatetrackerandroid;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends NavigationActivity
        implements OnMapReadyCallback, SearchFragment.Listener, DetailFragment.Listener, GoogleMap.OnMarkerClickListener, Serializable, NetworkOperations.Listener {

    private GoogleMap mMap;
    private List<PropertyObject> mPropertiesList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private PropertyAdapter mAdapter;
    private LatLngBounds UKLatLongBounds = new LatLngBounds(
            new LatLng(50.05992, -6.577582), new LatLng(58.616225, 1.523947));
    private LatLng UKLocation = new LatLng(51.5074,-0.1278);
    private Integer mCounter;
    private ArrayList<Marker> mMarkers = new ArrayList<>();
    private NetworkOperations mNetworkOperations;
    private static final String DETAIL_FRAGMENT = "DETAIL_FRAGMENT";
    private static final String SEARCH_FRAGMENT = "SEARCH_FRAGMENT";
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNavigationView.setCheckedItem(R.id.nav_search);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_main, contentFrameLayout);
        mNetworkOperations = new NetworkOperations(this,this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new PropertyAdapter(mPropertiesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                PropertyObject propertyObject = mPropertiesList.get(position);
                mPosition = position;
                // Create an instance of the dialog fragment and show it
                DetailFragment dialog = DetailFragment.newInstance(propertyObject);
                dialog.show(getSupportFragmentManager(),DETAIL_FRAGMENT);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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
        mMap.setOnMarkerClickListener(this);
        // Constrain the camera target to the UK bounds.
        mMap.setLatLngBoundsForCameraTarget(UKLatLongBounds);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(UKLocation,5));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int position = (int)(marker.getTag());
        mRecyclerView.smoothScrollToPosition(position);
        return false;
    }

    public void addPropertyMarkers(){
        new Thread() {
            public void run() {
                mCounter = 0;
                while (mCounter < mPropertiesList.size()) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (PropertyObject elements: mPropertiesList){
                                    mCounter = mCounter + 1;
                                    LatLng latlang = new LatLng(elements.getLatitude(),elements.getLongitude());
                                    Marker poi = mMap.addMarker(new MarkerOptions().position(latlang).title(elements.getAddress())
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                    poi.setTag(mCounter-1);
                                    mMarkers.add(poi);
                                }
                            }
                        });
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void showDialog() {
        // Create an instance of the dialog fragment and show it
        SearchFragment dialog = new SearchFragment();
        dialog.show(getSupportFragmentManager(),SEARCH_FRAGMENT);
    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, EditText text) {
        String area = text.getText().toString() ;
        mNetworkOperations.getSearch(area,"");
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog) {
        dialog.dismiss();
    }

    @Override
    public void onDismissDetailFragment(Boolean isFavourite, Boolean prevFavourite, String listingID) throws JSONException {
        mNetworkOperations.addFavourites(isFavourite,prevFavourite,listingID);
        if (isFavourite){
            mPropertiesList.get(mPosition).setIsFavourite(isFavourite);
        }
    }

    @Override
    public void onGetFavouritesSuccess(ArrayList<PropertyObject> response) throws JSONException {
    }

    @Override
    public void onAddFavouritesSuccess(String response) throws JSONException {
        Toast.makeText(this,"Successfully added property to your favourites",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSearchSuccess(ArrayList<PropertyObject> response) throws JSONException {
        if (response != null){
            mPropertiesList.clear();
            mPropertiesList.addAll(response);
        }
        mMap.clear();
        addPropertyMarkers();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoginSuccess(String response) {

    }

    @Override
    public void onError(String error) {
        Toast.makeText(this,error,Toast.LENGTH_LONG).show();
    }
}
