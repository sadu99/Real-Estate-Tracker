package real_estate_tracker.realestatetrackerandroid;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class FavouritesActivity extends NavigationActivity implements DetailFragment.Listener, NetworkOperations.Listener {

    private List<PropertyObject> mPropertiesList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private PropertyAdapter mAdapter;
    private NetworkOperations mNetworkOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetworkOperations = new NetworkOperations(this,this);
        mNavigationView.setCheckedItem(R.id.nav_favourite);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_favourites, contentFrameLayout);
        mNetworkOperations.getFavourites();
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
    }

    @Override
    public void onDismissDetailFragment(Boolean isFavourite,Boolean prevFavourite, String listingID) throws JSONException {
        mNetworkOperations.addFavourites(isFavourite,prevFavourite,listingID);
    }

    @Override
    public void onGetFavouritesSuccess(ArrayList<PropertyObject> response) throws JSONException {
        if (response != null){
            mPropertiesList.clear();
            mPropertiesList.addAll(response);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAddFavouritesSuccess(String response) throws JSONException {
        Toast.makeText(this,"Successfully added property to your favourites",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSearchSuccess(ArrayList<PropertyObject> response) throws JSONException {

    }

    @Override
    public void onLoginSuccess(String response) {

    }

    @Override
    public void onError(String error) {
        Toast.makeText(this,error,Toast.LENGTH_LONG).show();
    }
}
