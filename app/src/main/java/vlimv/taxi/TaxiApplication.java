package vlimv.taxi;

/**
 * Created by HP on 15-Apr-18.
 */

import android.app.Application;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class TaxiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // init singletons

//        ServerSocket.getInstance(this);
//        ServerRequest.getInstance(this);
    }

    //    private static final String BASE_URL = "http://95.46.114.18:8090";
//
//    private Socket mSocket;
//    {
//        try {
//            mSocket = IO.socket(BASE_URL);
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public Socket getSocket() {
//        return mSocket;
//    }
}