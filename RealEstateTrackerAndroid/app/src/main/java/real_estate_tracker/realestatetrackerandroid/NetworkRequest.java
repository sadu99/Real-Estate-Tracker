package real_estate_tracker.realestatetrackerandroid;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sanhar on 2017-03-28.
 */

public class NetworkRequest {

    private static final String ENDPOINT = "http://52.168.81.137:3000/";
    private Listener mListener;
    private Context mContext;

    public interface Listener {
        void onSuccess(JSONObject response,String type) throws JSONException;
        void onError(VolleyError error, Exception e);
    }

    NetworkRequest(Listener listener, Context context){
        mListener = listener;
        mContext = context;
    }

    public void postRequest(String url, JSONObject postObj, final String type){
        url = ENDPOINT+url;
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            JsonObjectRequest jsonObj = new JsonObjectRequest(Request.Method.POST,url,postObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(mListener != null)
                        try {
                            mListener.onSuccess(response,type);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    public void putRequest(String url, JSONObject postObj, final String type){
        url = ENDPOINT+url;
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            JsonObjectRequest jsonObj = new JsonObjectRequest(Request.Method.PUT,url,postObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(mListener != null)
                        try {
                            mListener.onSuccess(response,type);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    public void getRequest(String url, final String type){
        url = ENDPOINT+url;
        try {
            RequestQueue queue = Volley.newRequestQueue(mContext);

            JsonObjectRequest jsonObj = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(mListener != null)
                        try {
                            mListener.onSuccess(response,type);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(mListener != null)
                        mListener.onError(error,null);
                }
            });

            jsonObj.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObj);

        }catch(Exception e){
            if(mListener != null)
                mListener.onError(null,e);
        }
    }
}
