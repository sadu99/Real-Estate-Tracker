package real_estate_tracker.realestatetrackerandroid;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Sanhar on 2017-03-20.
 */

public class PropertyObject implements Serializable {
    private String mArea;
    private String mBedroom;
    private LatLng mLocation;
    private String mPrice;
    private String mUrl;

    PropertyObject(String area, String bedroom, LatLng location, String price, String url){
        mArea = area;
        mBedroom = bedroom;
        mLocation = location;
        mPrice = price;
        mUrl = url;
    }

    public String getArea(){
        return mArea;
    }

    public String getBedroom(){
        return mBedroom;
    }

    public LatLng getLocation(){
        return mLocation;
    }

    public String getPrice(){
        return mPrice;
    }

    public String getUrl(){
        return mUrl;
    }
}
