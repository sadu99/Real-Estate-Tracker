package real_estate_tracker.realestatetrackerandroid;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by Sanhar on 2017-03-28.
 */

public class NetworkRequest {
    Listener mListener;
    Context mContext;

    public interface Listener {
        void onSuccess(JSONObject response);
        void onError(VolleyError error, Exception e);
    }

    NetworkRequest(Listener listener, Context context){
        mListener = listener;
        mContext = context;
    }

    public void postRequest(String url,JSONObject postObj){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            JsonObjectRequest jsonObj = new JsonObjectRequest(url,postObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(mListener != null)
                        mListener.onSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(mListener != null)
                        mListener.onError(error,null);
                }
            });

            queue.add(jsonObj);

        }catch(Exception e){
            if(mListener != null)
                mListener.onError(null,e);
        }
    }

    public void getRequest(String url){
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            JsonObjectRequest jsonObj = new JsonObjectRequest(url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(mListener != null)
                        mListener.onSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(mListener != null)
                        mListener.onError(error,null);
                }
            });

            queue.add(jsonObj);

        }catch(Exception e){
            if(mListener != null)
                mListener.onError(null,e);
        }
    }
}
