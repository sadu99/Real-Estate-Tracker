package real_estate_tracker.realestatetrackerandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class FavouritesActivity extends NavigationActivity implements DetailFragment.Listener{

    private List<PropertyObject> mPropertiesList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private PropertyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNavigationView.setCheckedItem(R.id.nav_favourite);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_favourites, contentFrameLayout);

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
                DetailFragment dialog = DetailFragment.newInstance(propertyObject);
                dialog.show(getSupportFragmentManager(),"DetailFragment");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

//        preparePropertyData();
    }

//    private void preparePropertyData() {
//        String urlstr = "https://li.zoocdn.com/4393649839ebe2136c01226931ad0f5e862c243e_50_38.jpg";
//        PropertyObject propertyObject = new PropertyObject("asas", "asasa",new LatLng(51.5074,-0.1278),"asasas",urlstr);
//        mPropertiesList.add(propertyObject);
//        propertyObject = new PropertyObject("asas", "asasa",new LatLng(51.6074,-0.1278),"asasas",urlstr);
//        mPropertiesList.add(propertyObject);
//        propertyObject = new PropertyObject("asas", "asasa",new LatLng(51.6074,-0.1478),"asasas",urlstr);
//        mPropertiesList.add(propertyObject);
//        mAdapter.notifyDataSetChanged();
//    }

    @Override
    public void onFavouriteClick(DialogInterface dialog) {

    }
}
