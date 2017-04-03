package real_estate_tracker.realestatetrackerandroid;

import android.content.Context;

import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sanhar on 2017-04-01.
 */

public class NetworkOperations implements NetworkRequest.Listener {
    private Context mContext;
    private NetworkRequest mNetworkRequest;
    private Listener mListener;
    private UserObject mUserObject;
    private ArrayList<PropertyObject> mPropertiesList = new ArrayList<>();
    private ArrayList<PropertyObject> mFavouritesList = new ArrayList<>();
    private static final String FAVOURITE_GET = "FAVOURITE_GET";
    private static final String FAVOURITE_ADD = "FAVOURITE_ADD";
    private static final String FAVOURITE_DEL = "FAVOURITE_DEL";
    private static final String SEARCH = "SEARCH";
    private static final String LOGIN = "LOGIN";

    public interface Listener {
        void onGetFavouritesSuccess(ArrayList<PropertyObject> response) throws JSONException;
        void onAddFavouritesSuccess(String response) throws JSONException;
        void onSearchSuccess(ArrayList<PropertyObject> response) throws JSONException;
        void onLoginSuccess(String response);
        void onError(String error);
    }


    NetworkOperations(Context context,Listener listener){
        mContext = context;
        mNetworkRequest = new NetworkRequest(this,mContext);
        mListener = listener;
        mUserObject = new UserObject();
    }


    public void addUser(String name, String userID, String email) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("userID",userID);
        json.put("name",name);
        json.put("email",email);
        mNetworkRequest.postRequest("user",json,LOGIN);
    }

    public void getFavourites(){
        String user = mUserObject.getUserID();
        String url = "favourites/?userID=" + user;
        mNetworkRequest.getRequest(url,FAVOURITE_GET);
    }

    public void addFavourites(Boolean isFavourite,Boolean prevFavourite, String listingID) throws JSONException {
        String type = FAVOURITE_ADD;
        if (prevFavourite != isFavourite){
            JSONObject json = new JSONObject();
            json.put("userID",mUserObject.getUserID());
            json.put("listingID",listingID);
            if (!isFavourite){
                json.put("removeListing","true");
                type = FAVOURITE_DEL;
            }
            mNetworkRequest.putRequest("user",json,type);
        }
    }

    public void getSearch(String area, String appendUrl){
        getFavourites();
        String url = "location/" + area;
        url = url + "?" + appendUrl;
        mNetworkRequest.getRequest(url,SEARCH);
    }

    public ArrayList<PropertyObject> getPropertyListings(JSONArray jsonArray, String type) throws JSONException {
        ArrayList<PropertyObject> propertyObjectArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            PropertyObject propertyObject = new PropertyObject();
            propertyObject.setObject((JSONObject) jsonArray.get(i));
            if (type == FAVOURITE_GET){
                propertyObject.setIsFavourite(true);
            } else {
                for (PropertyObject object:mFavouritesList){
                    if (object.getListingId().equals(propertyObject.getListingId())){
                        propertyObject.setIsFavourite(true);
                    }
                }
            }
            propertyObjectArrayList.add(propertyObject);
        }
        return propertyObjectArrayList;
    }

    @Override
    public void onSuccess(JSONObject response, String type) throws JSONException {
        if (type.equals(FAVOURITE_ADD)) {
            mListener.onAddFavouritesSuccess("Successfully added property");
        } else if (type.equals(FAVOURITE_DEL)){
            mListener.onAddFavouritesSuccess("Successfully removed property");
        } else if (response.get("count").toString().equals("0")){
            if (response.get("error") != null){
                mListener.onError(response.get("error").toString());
            }
            mListener.onError("Error");
        } else {
            if (type.equals(SEARCH)){
                JSONArray propertyListings = (JSONArray) response.get("listings");
                mListener.onSearchSuccess(getPropertyListings(propertyListings,type));
            } else if (type.equals(FAVOURITE_GET)) {
                JSONArray propertyListings = (JSONArray) response.get("listings");
                mFavouritesList = getPropertyListings(propertyListings,type);
                mListener.onGetFavouritesSuccess(mFavouritesList);
            } else if (type.equals(LOGIN)){
                mListener.onLoginSuccess("Successful Login");
            } else {
                mListener.onError("Error");
            }
        }
    }

    @Override
    public void onError(VolleyError error, Exception e) {
        String response;
        if (error.getMessage() == null){
            response = "Error";
        } else {
            response = error.getMessage();
        }
        mListener.onError(response);
    }
}
