package epitech.eip.slidare.request;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.Handler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 42350 on 03/01/2018.
 */

public class User {

    static final String TAG = "UserRequest";

    static public void createUser(final String body, Handler<String> handler) throws Exception {

        Fuel.post(Config.URL_API + "createUser").body(body.getBytes()).responseString(handler);
    }

    static public void loginUser(final String body, Handler<String> handler) throws Exception {

        Fuel.post(Config.URL_API + "loginUser").body(body.getBytes()).responseString(handler);
    }

    static public void resetPassword(final String body, Handler<String> handler) throws Exception {

        Fuel.post(Config.URL_API + "resetPassword").body(body.getBytes()).responseString(handler);
    }

    static public void fetchUser(final String token, Handler<String> handler) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer " + token);

        Fuel.get(Config.URL_API + "fetchUser").header(header).responseString(handler);
    }
}
