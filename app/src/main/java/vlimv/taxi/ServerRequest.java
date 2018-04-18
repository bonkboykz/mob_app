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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HP on 15-Mar-18.
 */

public class ServerRequest {
    private static ServerRequest mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    private static String domainUrl = "http://95.46.114.18";
    private static String baseUrl = "http://95.46.114.18:8090";
    private NextActivity nextActivityInterface;
    private SaveCode saveCodeInterface;

    private ServerRequest(Context context) {
        CookieHandler.setDefault(new CookieManager());
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

    public void signUp(final String phone, final Context context) {
        if (context instanceof SaveCode) {
            saveCodeInterface = (SaveCode) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SaveCodeInterface");
        }
        String signUpUrl = baseUrl + "/auth/signup";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBody.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, signUpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("onresponse signup", response);
                        // TODO check if any error message
                        if (response.contains("User with this phone")) {
                            SharedPref.saveIsReg(context, true);
                            signIn(phone, (Context) saveCodeInterface);
                            return;
                        }
                        JSONObject obj;
                        try {
                            obj = new JSONObject(response);
                            String code = obj.getString("code");
                            Log.d("Code", code);
                            saveCodeInterface.saveCode(code);
                            Toast.makeText(mCtx, code, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            saveCodeInterface.tryAgain();
                            Log.e("onerror signup", e.getMessage());
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                saveCodeInterface.tryAgain();
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
                    saveCodeInterface.tryAgain();
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        mInstance.addToRequestQueue(stringRequest);
    }

    public void signIn(final String phone, final Context context) {
        if (context instanceof SaveCode) {
            saveCodeInterface = (SaveCode) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SaveCodeInterface");
        }
        String signInUrl = baseUrl + "/auth/login";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBody.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, signInUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("onresponse signin", response);
                        JSONObject obj;
                        try {
                            obj = new JSONObject(response);
                            String code = obj.getString("code");
                            Log.d("Code", code);
                            saveCodeInterface.saveCode(code);
                            Toast.makeText(mCtx, code, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            saveCodeInterface.tryAgain();
                            Log.e("onerror signin", e.getMessage());
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                saveCodeInterface.tryAgain();
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
                    saveCodeInterface.tryAgain();
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        mInstance.addToRequestQueue(stringRequest);
    }

    public void verify(final String code, final Context context) {
        if (context instanceof NextActivity) {
            nextActivityInterface = (NextActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NextActivity");
        }
        String verifyUrl = baseUrl + "/auth/verify";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBody.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, verifyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("onresponse verify", response);
                        JSONObject obj;
                        try {
                            obj = new JSONObject(response);
                            String token = obj.getString("userToken");
                            Log.d("Token", token);
                            SharedPref.saveToken(mCtx, token);
                            addTokenCookie(token);
                            Toast.makeText(mCtx, token, Toast.LENGTH_SHORT).show();
                            nextActivityInterface.goNext();
                        } catch (JSONException e) {
                            nextActivityInterface.tryAgain();
                            Log.e("onerror verify", e.getMessage());
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nextActivityInterface.tryAgain();
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
                    nextActivityInterface.tryAgain();
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        mInstance.addToRequestQueue(stringRequest);
    }
    public void addTokenCookie(String token) {
        CookieManager manager = new CookieManager();
        HttpCookie c = new HttpCookie("userToken", token);
        URI uri = null;
        try {
            uri = new URI(domainUrl);
        } catch (java.net.URISyntaxException e) {
            e.printStackTrace();
        }
        if (!manager.getCookieStore().get(uri).isEmpty()) manager.getCookieStore().get(uri).clear();
        manager.getCookieStore().add(uri, c);
        CookieHandler.setDefault(new CookieManager());
    }
    public void updateUser(final String userId, final String name, final String surname, final int age,
                           final String sex, final String role, Context context) {
        if (context instanceof NextActivity) {
            nextActivityInterface = (NextActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NextActivity");
        }
//        JSONObject jsonBody = new JSONObject();
//        try {
//            jsonBody.put("_id", userId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        final String requestBody = jsonBody.toString();
        StringBuilder sb = new StringBuilder();
        sb.append("http://95.46.114.18:8090/api?query=mutation{");
        sb.append("userUpdateById(record:{");
        sb.append("name:\"" + name + "\",");
        sb.append("lname:\"" + surname + "\",");
        sb.append("age:" + age + ",");
        sb.append("sex:" + sex + ",");
        sb.append("role:" + role + ",");
        sb.append("_id:\"" + userId + "\"");
        sb.append("}");
        sb.append(")");
        sb.append("{");
        sb.append("recordId");
        sb.append("}");
        sb.append("}");
        final String currentToken = SharedPref.loadToken(context);
        addTokenCookie(currentToken);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sb.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response update user", response);
                        JSONObject obj;
                        try {
                            obj = new JSONObject(response);
                            if (obj.has("errors")) {
                                throw new JSONException(obj.getJSONObject("errors").getString("message"));
                            }
                            nextActivityInterface.goNext();
                        } catch (JSONException e) {
                            nextActivityInterface.tryAgain();
                            Log.e("error in update user", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nextActivityInterface.tryAgain();
                Log.e("error in update user", error.toString());
                Toast.makeText(mCtx, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                // headers.put("Set-Cookie", "userToken=" + currentToken);
                Log.d("set header", currentToken);
                headers.put("x-user-token", currentToken);
                headers.put("Content-Type","application/x-www-form-urlencoded");

                return headers;
            }
//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }
//
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                try {
//                    return requestBody == null ? null : requestBody.getBytes("utf-8");
//                } catch (UnsupportedEncodingException uee) {
//                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
//                    return null;
//                }
//            }
        };
        mInstance.addToRequestQueue(stringRequest);
    }

    public void updateDriver(final String userId, final String name, final String surname, final int age,
                             final String sex, final String role, final Car car, Context context) {
        if (context instanceof NextActivity) {
            nextActivityInterface = (NextActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NextActivity");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("http://95.46.114.18:8090/api?query=mutation{");
        sb.append("userUpdateById(record:{");
        sb.append("name:\"" + name + "\",");
        sb.append("lname:\"" + surname + "\",");
        sb.append("age:" + age + ",");
        sb.append("sex:" + sex + ",");
        sb.append("role:" + role + ",");
        sb.append("_id:\"" + userId + "\"");
        sb.append("}");
        sb.append(")");
        sb.append("{");
        sb.append("recordId");
        sb.append("}");
        sb.append("}");
        final String currentToken = SharedPref.loadToken(context);
        addTokenCookie(currentToken);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sb.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response update user", response);
                        JSONObject obj;
                        try {
                            obj = new JSONObject(response);
                            if (obj.has("errors")) {
                                throw new JSONException(obj.getJSONObject("errors").getString("message"));
                            }
                            mInstance.createCar(userId, car.name, car.model, car.type, car.gosNumber,
                                    car.year, mCtx, nextActivityInterface);
                        } catch (JSONException e) {
                            nextActivityInterface.tryAgain();
                            Log.e("error in update user", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nextActivityInterface.tryAgain();
                Log.e("error in update user", error.toString());
                Toast.makeText(mCtx, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                // headers.put("Set-Cookie", "userToken=" + currentToken);
                Log.d("set header", currentToken);
                headers.put("x-user-token", currentToken);
                headers.put("Content-Type","application/x-www-form-urlencoded");

                return headers;
            }
        };
        mInstance.addToRequestQueue(stringRequest);
    }

    public void createCar(final String driverId, final String carName, final String carModel, final String carType,
                          final String carNumber, final int carYear, Context context,
                          final NextActivity nextActivityInterface) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://95.46.114.18:8090/api?query=mutation{");
        sb.append("vehicleCreateOne(record:{");
        sb.append("name:\"" + carName + "\",");
        sb.append("model:\"" + carModel + "\",");
        sb.append("type:\"" + carType + "\",");
        sb.append("gosNumber:\"" + carNumber + "\",");
        sb.append("year:" + carYear + ",");
        sb.append("driverId:\"" + driverId + "\"");
        sb.append("}");
        sb.append(")");
        sb.append("{");
        sb.append("recordId");
        sb.append("}");
        sb.append("}");
        final String currentToken = SharedPref.loadToken(context);
        addTokenCookie(currentToken);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sb.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response create car", response);
                        JSONObject obj;
                        try {
                            obj = new JSONObject(response);
                            if (obj.has("errors")) {
                                throw new JSONException(obj.getJSONObject("errors").getString("message"));
                            }
                            nextActivityInterface.goNext();
                        } catch (JSONException e) {
                            nextActivityInterface.tryAgain();
                            Log.e("error in create car", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nextActivityInterface.tryAgain();
                Log.e("error in create car", error.toString());
                Toast.makeText(mCtx, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                Log.d("set header", currentToken);
                headers.put("x-user-token", currentToken);
                headers.put("Content-Type","application/x-www-form-urlencoded");
                return headers;
            }
        };
        mInstance.addToRequestQueue(stringRequest);
    }

    public void createTrip(final String passengerId, final String from, final String to, final String cost,
                          final String addInfo, final boolean hasEscort, final boolean hasWheelchair, Context context) {
        if (context instanceof NextActivity) {
            nextActivityInterface = (NextActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NextActivityInterface");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("http://95.46.114.18:8090/api?query=mutation{");
        sb.append("tripCreateOne(record:{");
        sb.append("from:\"" + from + "\",");
        sb.append("to:\"" + to + "\",");
        sb.append("step:" + 0 + ",");
        sb.append("cost:\"" + cost + "\",");
        sb.append("addInfo:\"" + addInfo + "\",");
        sb.append("passengerId:\"" + passengerId + "\",");
        sb.append("escort:" + hasEscort + ",");
        sb.append("wheelchair:" + hasWheelchair + "");
        sb.append("}");
        sb.append(")");
        sb.append("{");
        sb.append("recordId");
        sb.append("}");
        sb.append("}");
        final String currentToken = SharedPref.loadToken(context);
        addTokenCookie(currentToken);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sb.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response create trip", response);
                        JSONObject obj;
                        try {
                            obj = new JSONObject(response);
                            if (obj.has("errors")) {
                                throw new JSONException(obj.getJSONObject("errors").getString("message"));
                            }
                            nextActivityInterface.goNext();
                        } catch (JSONException e) {
                            nextActivityInterface.tryAgain();
                            Log.e("error in create trip", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nextActivityInterface.tryAgain();
                Log.e("error in create trip", error.toString());
                Toast.makeText(mCtx, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                Log.d("set header", currentToken);
                headers.put("x-user-token", currentToken);
                headers.put("Content-Type","application/json");
                return headers;
            }
        };
        mInstance.addToRequestQueue(stringRequest);
    }

    public void updateTrip(final HashMap<String, String> map, Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://95.46.114.18:8090/api?query=mutation{");
        sb.append("tripUpdateOne(record:{");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + ":\"");
            sb.append(value + "\",");
        }
//        sb.setLength(sb.length() - 1);
//        sb.append("from:\"" + from + "\",");
//        sb.append("to:\"" + to + "\",");
//        sb.append("step:" + 0 + ",");
//        sb.append("cost:\"" + cost + "\",");
//        sb.append("addInfo:\"" + addInfo + "\",");
//        sb.append("passengerId:\"" + passengerId + "\",");
//        sb.append("escort:" + hasEscort + ",");
//        sb.append("wheelchair:" + hasWheelchair + "");
        sb.append("}");
        sb.append(")");
        sb.append("{");
        sb.append("recordId");
        sb.append("}");
        sb.append("}");
        final String currentToken = SharedPref.loadToken(context);
        addTokenCookie(currentToken);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sb.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response update trip", response);
                        JSONObject obj;
                        try {
                            obj = new JSONObject(response);
                            if (obj.has("errors")) {
                                throw new JSONException(obj.getJSONObject("errors").getString("message"));
                            }
                        } catch (JSONException e) {
                            Log.e("error in update trip", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nextActivityInterface.tryAgain();
                Log.e("error in update trip", error.toString());
                Toast.makeText(mCtx, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                Log.d("set header", currentToken);
                headers.put("x-user-token", currentToken);
                headers.put("Content-Type","application/json");
                return headers;
            }
        };
        mInstance.addToRequestQueue(stringRequest);
    }

    public void getUser(final String token, final Context context) {
        addTokenCookie(token);
        String mainUrl = baseUrl + "/main";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mainUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response in get user", response);
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            String id = obj.getString("_id");
                            String role = obj.getString("role");
                            String lname = obj.getString("lname");
                            String name = obj.getString("name");
                            SharedPref.saveUserName(context, name);
                            SharedPref.saveUserSurname(context, lname);
                            SharedPref.saveUserId(context, id);
                            SharedPref.saveUserType(context, role);
                            Log.d("id", id);
//                            try {
//
//                                if (!lname.isEmpty() && !lname.contains("default")) {
//                                    SharedPref.saveUserSurname(context, lname);
//                                }
//                            } catch (JSONException e) {
//                                Log.e("onerror in get user | lname ", e.getMessage());
//                            }

                        } catch (JSONException e) {
                            Log.e("onerror in get user", e.getMessage());
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("ASD", error.getMessage().toString());
//                Log.d("error response", "token:" + token);
//                Log.d("error response", "yiyi");
                Toast.makeText(mCtx, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Set-Cookie", "userToken=" + token);
                Log.d("set header", token);
                headers.put("x-user-token", token);
                return headers;
            }
        };
        mInstance.addToRequestQueue(stringRequest);
    }

    public void getUserTrips(final String token, final String id, final Context context) {
        addTokenCookie(token);
        String mainUrl = baseUrl + "/api?query={tripMany(filter:{";
        mainUrl += "driverId : \"" + id + "\"}){";
        mainUrl += "from, to, cost}}";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mainUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response in get trips", response);
                        JSONObject obj;
                        try {
                            obj = new JSONObject(response);
                            JSONArray array = obj.getJSONObject("data").getJSONArray("tripMany");
                            ArrayList<JSONObject> trips = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                trips.add(array.getJSONObject(i));
                            }
                            CompletedOrdersFragment.initArray(trips);
                        } catch (JSONException e) {
                            Log.e("onerror in get trips", e.getMessage());
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mCtx, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Set-Cookie", "userToken=" + token);
                Log.d("set header", token);
                headers.put("x-user-token", token);
                return headers;
            }
        };
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
            nextActivityInterface = (NextActivity) context;
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

                            nextActivityInterface.goNext();
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

    public void getDriverTrips(final String id) {
        String mainUrl = baseUrl + "/api";
        StringBuilder sb = new StringBuilder();
        sb.append(mainUrl);
        sb.append("?query=mutation{");
        sb.append("tripMany(filter:{");
        sb.append("_id:\"" + id + "\",");
        sb.append("}");
        sb.append(")");
        sb.append("{");
        sb.append("from,");
        sb.append("to,");
        sb.append("step,");
        sb.append("cost,");
        sb.append("addInfo");
        sb.append("}");
        sb.append("}");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mainUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("onresponse manyrtips", response);
                        JSONObject obj;
                        try {
                            obj = new JSONObject(response);
                            JSONObject data = obj.getJSONObject("data");
                            Log.d("Code", data.toString());
                            // saveCodeInterface.saveCode(code);
                            // Toast.makeText(mCtx, code, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Log.e("onerror manytrips", e.getMessage());
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mCtx, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface NextActivity {
        void goNext();
        void tryAgain();
    }
    public interface SaveCode {
        void saveCode(String code);
        void tryAgain();
    }
}
