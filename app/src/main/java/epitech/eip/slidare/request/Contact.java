package epitech.eip.slidare.request;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.Handler;

import java.util.HashMap;
import java.util.Map;

public class Contact {

    static final String TAG = "ContactRequest";

    static public void userContact(final String id, final String token, Handler<String> handler) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer " + token);

        Fuel.get(Config.URL_API + "userContact/" + id).header(header).responseString(handler);
    }

    static public void userContacts(final String token, Handler<String> handler) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer " + token);

        Fuel.get(Config.URL_API + "userContacts").header(header).responseString(handler);
    }

    static public void addContact(final String body, final String token, Handler<String> handler) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer " + token);

        Fuel.post(Config.URL_API + "addContact").header(header).body(body.getBytes()).responseString(handler);
    }
}
