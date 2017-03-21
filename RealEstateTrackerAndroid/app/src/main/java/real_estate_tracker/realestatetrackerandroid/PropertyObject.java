package real_estate_tracker.realestatetrackerandroid;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Sanhar on 2017-03-20.
 */

public class PropertyObject {
    private String mArea;
    private String mBedroom;
    private LatLng mLocation;
    private String mPrice;

    PropertyObject(String area, String bedroom, LatLng location, String price){
        mArea = area;
        mBedroom = bedroom;
        mLocation = location;
        mPrice = price;
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
}
