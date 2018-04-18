package vlimv.taxi;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

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

    public static synchronized ServerSocket getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ServerSocket(context, activity);
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

    public void connect() {
        mSocket.on("hello", onHello);
        mSocket.on("active_trips", onActiveTrips);
        mSocket.on("trip_accepted", onTripAccepted);
        mSocket.on("driver_came", onDriverCame);
        mSocket.on("client_ready", onClientReady);
        mSocket.on("end_trip", onEndTrip);
        mSocket.on("report_trip", onReportTrip);
        mSocket.connect();
    }

    public void getOnline() {
        Log.d("getOnline", "Emitting online_user");
        mSocket.emit("online_user", SharedPref.loadUserId(mCtx));
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
            newTripObj.put("escort", escort);
            newTripObj.put("wheelchair", wheelchair);
        } catch (JSONException e) {
            Log.e("createNewTrip", e.getMessage());
        }
        mSocket.emit("new_trip", newTripObj);
    }
    public void sendTripAccepted(final String tripId, final String driverId, final String vehicleId, final String expTime) {
        Log.d("sendTripAccepted", "Emitting trip_accepted");
        JSONObject tripAcceptedObj = new JSONObject();
        try {
            tripAcceptedObj.put("id", tripId);
            tripAcceptedObj.put("driverId", driverId);
            tripAcceptedObj.put("vehicleId", vehicleId);
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
        mSocket.emit("report_trip", reportTripObj);
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
                    Log.d("onActiveTripsList", arr.getJSONObject(i).toString());
                    list.add(arr.getJSONObject(i));
                }
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
            // TODO trigger update client view
        }
    };
    private Emitter.Listener onDriverCame = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            for (Object arg: args) {
                Log.d("onDriverCame", arg.toString());
            }
            // TODO trigger update client view
        }
    };
    private Emitter.Listener onClientReady = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            for (Object arg: args) {
                Log.d("onClientReady", arg.toString());
            }
            // TODO trigger update driver view
        }
    };
    private Emitter.Listener onEndTrip = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            for (Object arg: args) {
                Log.d("onEndTrip", arg.toString());
            }
            // TODO trigger update client view
        }
    };
    private Emitter.Listener onReportTrip = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            for (Object arg: args) {
                Log.d("onReportTrip", arg.toString());
            }
            // TODO trigger update client view
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



 emit:   1) 'online_user' ====> принимает 'id' юзера он становится онлайн
 2) 'new_trip' ====> создание заказа принимает ' from, to, cost, addInfo, passengerId, carType, escort, wheelchair ' (сверху описание этих параметров)  ======> emit 'active_trips'
 3) 'trip_accepted'  ====> водитель принимает заказ, параметры такие { id (айди заказа), driverId, vehicleId, expTime }  ====> emit 'trip_accepted'
 4) 'driver_came'  ====> принимает 'id' (айди заказа)
 5)  'client_ready' ====> принимает 'id' (айди заказа)
 6) 'end_trip' ====> принимает 'id' (айди заказа)
 7) 'report_trip' ===> { id, comment, stars }
 */
