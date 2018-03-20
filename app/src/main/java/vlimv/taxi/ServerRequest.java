package vlimv.taxi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HP on 15-Mar-18.
 */

public class ServerRequest {
    private static ServerRequest mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private ServerRequest(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized ServerRequest getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ServerRequest(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void getPassenger() {
        String url = "http://95.46.114.18:8090/?query={userOne(filter:{role: passenger}) {}}";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("Check", response);
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            JSONObject data = obj.getJSONObject("data").getJSONObject("userOne");
                            Log.d("Check", data.getString("name") +
                                    data.getString("lname") + "");
                            Toast.makeText(mCtx, data.toString(), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            Log.e("Check", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mCtx, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        mInstance.addToRequestQueue(stringRequest);
    }

    public void getDriver() {
        String url = "http://95.46.114.18:8090/?query={userOne(filter:{role: driver}) {name, lname}}";
        //String url = "http://95.46.114.18:8090/?query={userCount(filter:{})}";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("Check", response);
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            JSONObject data = obj.getJSONObject("data").getJSONObject("userOne");
                            Log.d("Check", data.getString("name") +
                                    data.getString("lname") + "");
                            DriverActivity.driver = new Driver(data.getString("name"),
                                    data.getString("lname"));
                            Toast.makeText(mCtx, data.toString(), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            Log.e("Check", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mCtx, error.toString(), Toast.LENGTH_LONG).show();
            }
        });


        mInstance.addToRequestQueue(stringRequest);
    }

    public void createUser(final String name, final String surname, final int age,
                           final String sex, final String role, final String phone) {
        StringBuilder sb = new StringBuilder();

        sb.append("http://95.46.114.18:8090/?query=mutation{");
        sb.append("userCreateOne(record:{");
        sb.append("name:\"" + name + "\",");
        sb.append("lname:\"" + surname + "\",");
        sb.append("age:" + age + ",");
        sb.append("sex:" + sex + ",");
        sb.append("role:" + role + ",");
        sb.append("phone:\"" + phone + "\"");
        sb.append("}");
        sb.append(")");
        sb.append("{");
        sb.append("recordId");
        sb.append("}");
        sb.append("}");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sb.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("Check", response);
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            if (obj.has("errors")) {
//                                JSONArray arr = obj.getJSONArray("errors");
                                throw new JSONException(obj.getJSONObject("errors").getString("message"));
                            }

                            JSONObject data = obj.getJSONObject("data").getJSONObject("userCreateOne");

                            Toast.makeText(mCtx, data.getString("recordId"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            Toast.makeText(mCtx, "Не удалось создать пользователя", Toast.LENGTH_SHORT).show();
                            Log.e("Check2", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CheckEResponce", error.toString());
                Toast.makeText(mCtx, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
//        String url = "http://95.46.114.18:8090";
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                //This code is executed if the server responds, whether or not the response contains data.
//                //The String 'response' contains the server's response.
//            }
//        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //This code is executed if there is an error.
//
//            }
//        }) {
//            protected Map<String, String> getParams() {
//                Map<String, String> data = new HashMap<String, String>();
//                data.put("name", name);
//                data.put("lname", surname);
//                data.put("phone", "+77771112233");
////                data.put("", name);
////                data.put("name", name);
//                data.put("role", role);
//                return data;
//            }
//        };

        mInstance.addToRequestQueue(stringRequest);
    }
}
