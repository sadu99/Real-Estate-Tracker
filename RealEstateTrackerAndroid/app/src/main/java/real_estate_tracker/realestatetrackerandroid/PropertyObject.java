package real_estate_tracker.realestatetrackerandroid;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Sanhar on 2017-03-20.
 */

public class PropertyObject implements Serializable {
    private String mListingId,mBathrooms,mBedrooms,mUrl,mUrlThumbnail,mPropertyType,mAgentName
            ,mAgentAddress,mAgentPhone,mAddress,mPrice,mDescription,mCategory;
    private Double mLatitude,mLongitude;
    private boolean mIsFavourite;

    PropertyObject(){
    }

    public String getListingId(){
        return mListingId;
    }

    public String getBathrooms(){
        return mBathrooms;
    }

    public String getBedrooms(){
        return mBedrooms;
    }

    public String getUrl(){
        return mUrl;
    }

    public String getUrlThumbnail(){
        return mUrlThumbnail;
    }

    public Double getLatitude(){
        return mLatitude;
    }

    public Double getLongitude(){
        return mLongitude;
    }

    public String getPropertyType(){
        return mPropertyType;
    }

    public String getAgentName(){
        return mAgentName;
    }

    public String getAgentAddress(){
        return mAgentAddress;
    }

    public String getAgentPhone(){
        return mAgentPhone;
    }

    public String getAddress(){
        return mAddress;
    }

    public String getPrice(){
        return mPrice;
    }

    public String getDescription(){
        return mDescription;
    }

    public String getCategory(){
        return mCategory;
    }

    public boolean getIsFavourite(){return mIsFavourite;}

    public void setListingId(String listingId){
        mListingId = listingId;
    }

    public void setBathrooms(String bathrooms){
        mBathrooms = bathrooms;
    }

    public void setBedrooms(String bedrooms){
        mBedrooms = bedrooms;
    }

    public void setUrl(String url){
        mUrl = url;
    }

    public void setUrlThumbnail(String url){
        mUrlThumbnail = url;
    }

    public void setLatitude(Double Latitude){
        mLatitude = Latitude;
    }

    public void setLongitude(Double longitude){
        mLongitude = longitude;
    }

    public void setPropertyType(String propertyType){
        mPropertyType = propertyType;
    }

    public void setAgentName(String agentName){
        mAgentName = agentName;
    }

    public void setAgentAddress(String agentAddress){
        mAgentAddress = agentAddress;
    }

    public void setAgentPhone(String agentPhone){
        mAgentPhone = agentPhone;
    }

    public void setAddress(String address){
        mAddress = address;
    }

    public void setPrice(String price){
        mPrice = price;
    }

    public void setDescription(String description){
        mDescription = description;
    }

    public void setCategory(String category){
        mCategory = category;
    }

    public void setIsFavourite (Boolean isFavourite) {mIsFavourite = isFavourite;}

    public void setObject(JSONObject jsonObject) throws JSONException {
        setListingId(jsonObject.get("Listing_id").toString());
        setBathrooms(getValue(jsonObject.get("Num_bathrooms").toString(),"0"));
        setBedrooms(getValue(jsonObject.get("Num_bedrooms").toString(),"0"));
        setUrl(jsonObject.get("Image_url").toString());
        setUrlThumbnail(jsonObject.get("Thumbnail_image_url").toString());
        setLatitude(Double.parseDouble(jsonObject.get("Latitude").toString()));
        setLongitude(Double.parseDouble(jsonObject.get("Longitude").toString()));
        setPropertyType(getValue(jsonObject.get("Property_type").toString(),"N/A"));
        setAgentName(getValue(jsonObject.get("Agent_name").toString(),"N/A"));
        setAgentAddress(getValue(jsonObject.get("Agent_address").toString(),"N/A"));
        setAgentPhone(getValue(jsonObject.get("Agent_phone").toString(),"N/A"));
        setAddress(getValue(jsonObject.get("Displayable_address").toString(),"N/A)"));
        setPrice(getValue(jsonObject.get("Price").toString(),"N/A"));
        setDescription(getValue(jsonObject.get("Short_description").toString(),"N/A"));
        setCategory(getValue(jsonObject.get("Category").toString(),"N/A"));
    }

    public String getValue(String value, String defaultValue){
        if (value.isEmpty() || value == null){
            return defaultValue;
        } else {
            return value;
        }
    }

}
