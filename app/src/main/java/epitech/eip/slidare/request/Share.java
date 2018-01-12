package epitech.eip.slidare.request;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.Handler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 42350 on 03/01/2018.
 */

public class Share {

    static final String TAG = "ShareRequest";

    static public void getFiles(final String token, Handler<String> handler) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer " + token);

        Fuel.get(Config.URL_API + "getUserFiles").header(header).responseString(handler);
    }
}
