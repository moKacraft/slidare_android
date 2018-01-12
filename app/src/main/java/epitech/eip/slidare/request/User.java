package epitech.eip.slidare.request;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.Handler;

/**
 * Created by 42350 on 03/01/2018.
 */

public class User {

    static final String TAG = "UserRequest";

    static public void loginUser(final String body, Handler<String> handler) throws Exception {

        Fuel.post(Config.URL_API + "loginUser").body(body.getBytes()).responseString(handler);
    }
}
