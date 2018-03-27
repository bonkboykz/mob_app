package vlimv.taxi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HP on 15-Mar-18.
 */

public class ServerRequest {
    private static ServerRequest mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    private static String baseUrl = "http://95.46.114.18:8090";
    private static String testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIrNzcwODE5NDk1ODIiLCJpc3MiOiJ1c2VyIiwiaWF0IjoxNTIyMDA2NzcyNDY5fQ.yPg4dZBQItmBdZjAqAOr5vSFRKrAbIBmqb24knPwFuA";
    NextActivity mInterface;

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

    public void getCookie() {
//        CookieStore cookieStore = new MyCookieStore();
//        CookieManager manager = new CookieManager( cookieStore, CookiePolicy.ACCEPT_ALL );
//        CookieHandler.setDefault(manager );
        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        HttpCookie c = new HttpCookie("userToken", "");
        URI uri = null;
        try {
            uri = new URI("http://95.46.114.18:8090");
            manager.getCookieStore().add(uri, c);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        List <HttpCookie> cookieList = manager.getCookieStore().getCookies();
        for (HttpCookie cookie : cookieList)
        {
            Log.d("Cookie", cookie.getValue());
        }
        CookieHandler.setDefault(manager);
    }

    public void signUp(final String phone) {
        String signUpUrl = baseUrl + "/auth/signup";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("phone", "+77051232233");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBody.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, signUpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("onresponse", response);
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            String token = obj.getString("userToken");
                            getCookie();
                            Log.d("Token", token);
                            SharedPref.saveToken(mCtx, token);
                            Toast.makeText(mCtx, obj.toString(), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            Log.e("onerror", e.getMessage());
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("ASD", error.getMessage().toString());
                Toast.makeText(mCtx, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

//
//            @Override
//            public Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put("phone", phone);
//                return map;
//            }
        };
        mInstance.addToRequestQueue(stringRequest);
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

//                            DriverActivity.driver = new Driver(data.getString("name"),
//                                    data.getString("lname"));
                            DriverActivity.driver = new Driver("Новый","водитель");
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

    public void createDriver(final String name, final String surname, final int age,
                           final String sex, final String role, final String phone, final Car car, Context context) {
        StringBuilder sb = new StringBuilder();

        sb.append("http://95.46.114.18:8090/?query=mutation{");
        sb.append("userCreateOne(record:{");
        sb.append("name:\"" + name + "\",");
        sb.append("lname:\"" + surname + "\",");
        sb.append("age:" + age + ",");
        sb.append("sex:" + (sex.equals("Мужской") ? "male" : "female") + ",");
        sb.append("role:" + role + ",");
        sb.append("phone:\"" + phone + "\"");
        sb.append("}");
        sb.append(")");
        sb.append("{");
        sb.append("recordId");
        sb.append("}");
        sb.append("}");
        if (context instanceof NextActivity) {
            mInterface = (NextActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NextActivity");
        }
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
//                                JSONArray errors = obj.getJSONArray("errors");
//                                for (int i = 0; i < errors.length(); i++) {
//                                    Log.d("ERROR", errors.get(i).toString());
//                                }
                                throw new JSONException(obj.getJSONObject("errors").getString("message"));
                            }

                            JSONObject data = obj.getJSONObject("data").getJSONObject("userCreateOne");

                            mInterface.goNext();
                            String id = data.getString("recordId");
                            Toast.makeText(mCtx, id, Toast.LENGTH_SHORT).show();
                            mInstance.createCar(car, id);

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
        mInstance.addToRequestQueue(stringRequest);
    }

    public void createCar(Car car, final String driverId) {
        StringBuilder sb = new StringBuilder();

        sb.append("http://95.46.114.18:8090/?query=mutation{");
        sb.append("vehicleCreateOne(record:{");
        sb.append("name:\"" + car.name + "\",");
        sb.append("model:\"" + car.model + "\",");
        sb.append("type:\"" + car.type + "\",");
        sb.append("gosNumber:\"" + car.gosNumber + "\",");
        sb.append("year:" + car.year + ",");
        sb.append("driverId:\"" + driverId + "\"");
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

                            JSONObject data = obj.getJSONObject("data").getJSONObject("vehicleCreateOne");

                            String carId = data.getString("recordId");
                            Toast.makeText(mCtx, carId, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            Toast.makeText(mCtx, "Не удалось создать авто", Toast.LENGTH_SHORT).show();
                            Log.e("Check2", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CheckEResponce", error.toString());
                Toast.makeText(mCtx, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        mInstance.addToRequestQueue(stringRequest);
    }

    public void createPassenger(final String name, final String surname, final int age,
                             final String sex, final String role, final String phone, Context context) {
        StringBuilder sb = new StringBuilder();

        sb.append("http://95.46.114.18:8090/?query=mutation{");
        sb.append("userCreateOne(record:{");
        sb.append("name:\"" + name + "\",");
        sb.append("lname:\"" + surname + "\",");
        sb.append("age:" + age + ",");
        sb.append("sex:" + (sex.equals("Мужской") ? "male" : "female") + ",");
        sb.append("role:" + role + ",");
        sb.append("phone:\"" + phone + "\"");
        sb.append("}");
        sb.append(")");
        sb.append("{");
        sb.append("recordId");
        sb.append("}");
        sb.append("}");
        if (context instanceof NextActivity) {
            mInterface = (NextActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NextActivity");
        }
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
                                JSONArray errors = obj.getJSONArray("errors");
                                for (int i = 0; i < errors.length(); i++) {
                                    Log.d("ERROR", errors.get(i).toString());
                                }
                                throw new JSONException(obj.getJSONObject("errors").getString("message"));
                            }

                            JSONObject data = obj.getJSONObject("data").getJSONObject("userCreateOne");

                            mInterface.goNext();
                            String id = data.getString("recordId");
                            Toast.makeText(mCtx, id, Toast.LENGTH_SHORT).show();

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
        mInstance.addToRequestQueue(stringRequest);
    }
    public interface NextActivity {
        public void goNext();
    }
}
