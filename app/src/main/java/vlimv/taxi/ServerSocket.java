package vlimv.taxi;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Driver;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by HP on 11-Apr-18.
 */

public class ServerSocket {
    private static ServerSocket mInstance;
    private static final String BASE_URL = "http://95.46.114.18:8090";

    private ServerSocket(Context context) {
        mCtx = context;
    }
    private ServerSocket(Context context, Activity a) {
        mCtx = context;
        activity = (Activity) context;
    }
    private ServerSocket(Context context, Application a) {
        mCtx = context;
        application = (Application) context;
    }
//    public static synchronized ServerSocket getInstance(Context context) {
//        if (mInstance == null) {
//            mInstance = new ServerSocket(context, activity);
//        }
//        return mInstance;
//    }
    public static synchronized ServerSocket getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ServerSocket(context, application);
        }
        return mInstance;
    }

    private static Socket mSocket;
    {
        try {
            mSocket = IO.socket(BASE_URL);
        } catch (URISyntaxException e) {
            Log.e("Serversocket", e.getMessage());
        }
    }
    private static Context mCtx;
    private static Activity activity;
    private static Application application;

    public void connect() {
        mSocket.on("hello", onHello);
        mSocket.on("active_trips", onActiveTrips);
        mSocket.on("trip_accepted", onTripAccepted);
        mSocket.on("driver_came", onDriverCame);
        mSocket.on("client_ready", onClientReady);
        mSocket.on("end_trip", onEndTrip);
        mSocket.on("report_trip", onReportTrip);
        mSocket.on("trip_created", onTripCreated);
        mSocket.on("driver_start", onDriverStart);
        mSocket.on("cancel_trip", onCancelTrip);
        mSocket.connect();
    }

    public void getOnline() {
        Log.d("getOnline", "Emitting online_user");
        Log.d("getOnline | id", SharedPref.loadUserId(mCtx));
        JSONObject getOnlineObj = new JSONObject();
        try {
            getOnlineObj.put("id", SharedPref.loadUserId(mCtx));
        } catch (JSONException e) {
            Log.e("getOnline", e.getMessage());
        }
        mSocket.emit("online_user", getOnlineObj);
        // TODO decide whether to call or not to call getActiveTrips here
        getActiveTrips();
    }

    public void getActiveTrips() {
        Log.d("getActiveTrips", "Emitting active_trips");
        mSocket.emit("active_trips");
    }

    public void createNewTrip(final String from, final String to, final String cost,
                              final String addInfo, final String passengerId, final String carType,
                              final boolean escort, final boolean wheelchair) {
        Log.d("createNewTrip", "Emitting new_trip");
        JSONObject newTripObj = new JSONObject();
        try {
            newTripObj.put("from", from);
            newTripObj.put("to", to);
            newTripObj.put("cost", cost);
            newTripObj.put("addInfo", (addInfo != null) ? addInfo : "");
            newTripObj.put("passengerId", passengerId);
            newTripObj.put("carType", (carType != null) ? carType : "");
            newTripObj.put("step", 0);
            newTripObj.put("escort", escort);
            newTripObj.put("wheelchair", wheelchair);
        } catch (JSONException e) {
            Log.e("createNewTrip", e.getMessage());
        }
        mSocket.emit("new_trip", newTripObj);
    }
    public void sendTripAccepted(final String tripId, final String driverId, final String vehicleId, final String expTime) {
        Log.d("sendTripAccepted", "Emitting trip_accepted");
        Log.d("tripInfo | tripId", tripId);
        Log.d("tripInfo | driverId", driverId);
//        Log.d("tripInfo | vehicleId", vehicleId);
        Log.d("tripInfo | expTime", expTime);
        JSONObject tripAcceptedObj = new JSONObject();
        try {
            tripAcceptedObj.put("id", tripId);
            tripAcceptedObj.put("driverId", driverId);
//            tripAcceptedObj.put("vehicleId", vehicleId);
            tripAcceptedObj.put("expTime", (expTime != null) ? expTime : "");
        } catch (JSONException e) {
            Log.e("sendTripAccepted", e.getMessage());
        }
        mSocket.emit("trip_accepted", tripAcceptedObj);
    }
    public void sendDriverCame(final String tripId) {
        Log.d("sendDriverCame", "Emitting driver_came");
        JSONObject driverCameObj = new JSONObject();
        try {
            driverCameObj.put("id", tripId);
        } catch (JSONException e) {
            Log.e("sendDriverCame", e.getMessage());
        }
        mSocket.emit("driver_came", driverCameObj);
    }
    public void sendClientReady(final String tripId) {
        Log.d("sendClientReady", "Emitting client_ready");
        JSONObject clientReadyObj = new JSONObject();
        try {
            clientReadyObj.put("id", tripId);
        } catch (JSONException e) {
            Log.e("sendClientReady", e.getMessage());
        }
        mSocket.emit("client_ready", clientReadyObj);
    }
    public void sendDriverStart(final String tripId) {
        Log.d("sendDriverStart", "Emitting driver_start");
        JSONObject driverStartObj = new JSONObject();
        try {
            driverStartObj.put("id", tripId);
        } catch (JSONException e) {
            Log.e("sendDriverStart", e.getMessage());
        }
        mSocket.emit("driver_start", driverStartObj);
    }
    public void sendEndTrip(final String tripId) {
        Log.d("sendEndTrip", "Emitting end_trip");
        JSONObject endTripObj = new JSONObject();
        try {
            endTripObj.put("id", tripId);
        } catch (JSONException e) {
            Log.e("sendEndTrip", e.getMessage());
        }
        mSocket.emit("end_trip", endTripObj);
    }
    public void sendReportTrip(final String tripId, final String comment, final float stars) {
        Log.d("sendReportTrip", "Emitting report_trip");
        JSONObject reportTripObj = new JSONObject();
        try {
            reportTripObj.put("id", tripId);
            reportTripObj.put("comment", (comment != null) ? comment : "");
            reportTripObj.put("stars", stars);
        } catch (JSONException e) {
            Log.e("sendReportTrip", e.getMessage());
        }
        Log.d("sendReportTrip", tripId + " " + comment + " " + stars);
        mSocket.emit("report_trip", reportTripObj);
    }

    public void sendCancelTrip(final String tripId) {
        Log.d("sendCancelTrip", "Emitting cancel_trip");
        JSONObject reportTripObj = new JSONObject();
        try {
            reportTripObj.put("id", tripId);
        } catch (JSONException e) {
            Log.e("sendCancelTrip", e.getMessage());
        }
        mSocket.emit("cancel_trip", reportTripObj);
    }

    private Emitter.Listener onHello = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            for (Object arg: args) {
                Log.d("onHello", arg.toString());
            }
            getOnline();
        }
    };

    private Emitter.Listener onActiveTrips = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            for (Object arg: args) {
                Log.d("onActiveTrips", arg.toString());
            }
            try {
                JSONArray arr = new JSONArray(args[0].toString());
                final ArrayList<JSONObject> list = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    // Log.d("onActiveTripsList", arr.getJSONObject(i).toString());
                    list.add(arr.getJSONObject(i));
                }
                Collections.sort(list, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject jsonObject, JSONObject t1) {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        df.setTimeZone(TimeZone.getTimeZone("UTC"));
                        try {
                            String costA = jsonObject.getString("cost");
                            String costB = t1.getString("cost");
                            boolean costAVol = costA.equals("Волонтерская поездка");
                            boolean costBVol = costB.equals("Волонтерская поездка");
//                            Log.d("compare", costA);
//                            Log.d("compare", costB);
//                            Log.d("compare", costAVol + "");
//                            Log.d("compare", costAVol + "");

                            if (costAVol || costBVol) {
                                if (costAVol && costBVol) {
                                    //
                                } else {
                                    return (costAVol) ? -1 : 1;
                                }
                            }
                        } catch (Exception e) {
                            Log.e("compare", e.getMessage());
                        }
                        try {
                            Date dateA = df.parse(jsonObject.getString("createdAt"));
                            Date dateB = df.parse(t1.getString("createdAt"));
                            // Log.d("compare", dateA.toString());
                            // Log.d("compare", dateB.toString());
                            // Log.d("compare", Integer.toString(dateA.compareTo(dateB)));
                            return (-1) * dateA.compareTo(dateB);
                        } catch (Exception e) {
                            Log.e("compare", e.getMessage());
                        }

                            // "cost":"Волонтерская поездка"
                        return 0;
                    }
                });
                if (DriverMainActivity.status != null) {
                    if (DriverMainActivity.status.equals("busy")) {
                        final ArrayList<JSONObject> listCur = new ArrayList<>();
                        for (JSONObject obj: list) {
                            try {
                                String cost = obj.getString("cost");
                                boolean costVol = cost.equals("Волонтерская поездка");
                                if (costVol) {
                                    listCur.add(obj);
                                }
                            } catch (Exception e) {
                                Log.e("busy filter", e.getMessage());
                            }
                        }
                        list.clear();
                        list.addAll(listCur);
                    }
                }
//                Collections.reverse(list);
                // TODO pass active trips to trips view
                NewOrdersFragment.initDataset(list);
            } catch (JSONException e) {
                Log.e("onActiveTripsListener", e.getMessage());
            }
        }
    };

    private Emitter.Listener onTripAccepted = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            for (Object arg: args) {
                Log.d("onTripAccepted", arg.toString());
            }
            int expTime = 0;
            String driverName = "", driverLastname = "", driverPhone = "", driverRating = "0.0";
            String vehicleName = "", vehicleModel = "", vehicleType = "", vehicleNumber = "";
            String from = "", to ="";
            JSONObject driverInfoObj;
            JSONObject vehicleInfoObj;
            JSONObject obj = null;
            try {
                obj = new JSONObject(args[0].toString());
                expTime = obj.getInt("expTime");
                driverInfoObj = obj.getJSONObject("driverId");
                driverLastname = driverInfoObj.getString("lname");
                driverPhone = driverInfoObj.getString("phone");
                driverName = driverInfoObj.getString("name");
                driverRating = driverInfoObj.getString("rating");
                from = obj.getString("from");
                to = obj.getString("to");
            } catch (JSONException e) {
                Log.e("onTripAccepted", e.getMessage());
            }
            try {
                vehicleInfoObj = obj.getJSONObject("vehicleId");
                vehicleName = vehicleInfoObj.getString("name");
                vehicleModel = vehicleInfoObj.getString("model");
                vehicleNumber = vehicleInfoObj.getString("gosNumber");
            } catch (JSONException e) {
                Log.e("onTripAccepted", e.getMessage());
            }
            // TODO trigger update client view
            PassengerCityFragment.tripAcceptedPassenger(from, to, driverName, driverPhone, vehicleName, vehicleModel, vehicleNumber, expTime, driverRating);
        }
    };
    private Emitter.Listener onDriverCame = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            for (Object arg: args) {
                Log.d("onDriverCame", arg.toString());
            }
            String tripId = "";
            try {
                JSONObject obj = new JSONObject(args[0].toString());
                tripId = obj.getString("_id");
            } catch (JSONException e) {
                Log.e("onDriverCame", e.getMessage());
            }
            // TODO trigger update client view
            PassengerCityFragment.driverCamePassenger(tripId);
        }
    };
    private Emitter.Listener onClientReady = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            for (Object arg: args) {
                Log.d("onClientReady", arg.toString());
            }
            // TODO trigger update driver view
            DriverCityFragment.clientReadyDriver();

        }
    };
    private Emitter.Listener onEndTrip = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            for (Object arg: args) {
                Log.d("onEndTrip", arg.toString());
            }
            String tripId = "";
            try  {
                JSONObject obj = new JSONObject(args[0].toString());
                tripId = obj.getString("_id");
            } catch (JSONException e) {
                Log.e("onEndTrip", e.getMessage());
            }
            // TODO trigger update client view
            PassengerCityFragment.tripEndedPassenger(tripId);
        }
    };
    private Emitter.Listener onReportTrip = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            for (Object arg: args) {
                Log.d("onReportTrip", arg.toString());
            }
            // TODO trigger update driver view
        }
    };
    private Emitter.Listener onTripCreated = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            for (Object arg: args) {
                Log.d("onTripCreated", arg.toString());
            }
            String tripId = "";
            try  {
                JSONObject obj = new JSONObject(args[0].toString());
                tripId = obj.getString("_id");
            } catch (JSONException e) {
                Log.e("onTripCreated", e.getMessage());
            }
            PassengerCityFragment.tripCreatedPassenger(tripId);
        }
    };
    private Emitter.Listener onDriverStart = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            for (Object arg: args) {
                Log.d("onTripCreated", arg.toString());
            }
            // TODO trigger update view on passenger
            PassengerCityFragment.tripStartedPassenger();
        }
    };

    private Emitter.Listener onCancelTrip = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            for (Object arg: args) {
                Log.d("onTripCreated", arg.toString());
            }
            // TODO trigger update view on passenger
            PassengerCityFragment.tripCanceledPassenger();
            // TODO trigger update view on passenger
            DriverCityFragment.tripCanceledDriver();
            getActiveTrips();
        }
    };
}

/**
 * from: {
 type: String,
 // required: true,
 description: 'Начальная точка маршрута',
 },
 to: {
 type: String,
 // required: true,
 description: 'Конечная точка маршрута',
 },
 step: {
 type: Number,
 description: 'Этап поездки',
 },
 cost: {
 type: mongoose.Schema.Types.Mixed,
 // required: true,
 description: 'Цена поездки, может быть строкой',
 },
 addInfo: {
 type: String,
 description: 'Дополнительная информация',
 },
 driverId: {
 type: mongoose.Schema.Types.ObjectId,
 // required: true,
 description: 'Водитель поездки',
 },
 vehicleId: {
 type: mongoose.Schema.Types.ObjectId,
 // required: true,
 description: 'Автомобиль поездки',
 },
 passengerId: {
 type: mongoose.Schema.Types.ObjectId,
 // required: true,
 description: 'Пассажир поездки',
 },
 carType: {
 type: String,
 description: 'Транстпортное средство поездки',
 },
 escort: {
 type: Boolean,
 description: 'Наличие сопровождения',
 },
 wheelchair: {
 type: Boolean,
 description: 'Наличие коляски клиента',
 },
 expTime: {
 type: Number,
 description: 'Время ожидания',
 },
 report: {
 comment: {
 type: String,
 description: 'Комментарий',
 },
 stars: {
 type: Number,
 description: 'Оценка поездки',
 },
 }





 listen:  1) 'hello' ===> сюда будет прилетать емит в ответ на него надо будет выслать id юзера на канал 'online_user'
 2) 'active_trips' ===> здесь будут все активные поездки
 3) 'trip_accepted' ===> если заявка была принята
 4) 'driver_came' ===> водитель приехал
 5) 'client_ready' ===> клиент готов
 6) 'end_trip' ===> завершение поездки
 7) 'report_trip' ===> отзыв и оценка поездки
 8) 'trip_created' ===> после new_trip


 emit:   1) 'online_user' ====> принимает 'id' юзера он становится онлайн
 2) 'new_trip' ====> создание заказа принимает ' from, to, cost, addInfo, passengerId, carType, escort, wheelchair ' (сверху описание этих параметров)  ======> emit 'active_trips'
 3) 'trip_accepted'  ====> водитель принимает заказ, параметры такие { id (айди заказа), driverId, vehicleId, expTime }  ====> emit 'trip_accepted'
 4) 'driver_came'  ====> принимает 'id' (айди заказа)
 5)  'client_ready' ====> принимает 'id' (айди заказа)
 6) 'end_trip' ====> принимает 'id' (айди заказа)
 7) 'report_trip' ===> { id, comment, stars }
 'driver_start' => { id }
 'cancel_trip' => { id }
 */
