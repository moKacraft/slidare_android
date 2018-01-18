package epitech.eip.slidare.request;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.Handler;

import java.util.HashMap;
import java.util.Map;

public class Group {

    static final String TAG = "GroupRequest";

    static public void createGroup(final String body, final String token, Handler<String> handler) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer " + token);

        Fuel.post(Config.URL_API + "createGroup").header(header).body(body.getBytes()).responseString(handler);
    }

    static public void fetchGroups(final String token, Handler<String> handler) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer " + token);

        Fuel.get(Config.URL_API + "fetchGroups").header(header).responseString(handler);
    }

    static public void fetchGroupsExpandable(final String groupname, final String email, final String token, Handler<String> handler) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer " + token);

        Fuel.get(Config.URL_API + "fetchGroups").header(header).responseString(handler);
    }

    static public void renameGroup(final String body, final String token, Handler<String> handler) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.post(Config.URL_API + "renameGroup").header(header).body(body.getBytes()).responseString(handler);
    }

    static public void addToGroup(final String group, final String body, final String token, Handler<String> handler) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer " + token);

        Fuel.put(Config.URL_API + "addToGroup/" + group).header(header).body(body.getBytes()).responseString(handler);
    }

    static public void removeGroup(final String group, final String token, Handler<String> handler) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer " + token);

        Fuel.delete(Config.URL_API + "removeGroup/" + group).header(header).responseString(handler);
    }

    static public void removeFromGroup(final String name, final String body, final String token, Handler<String> handler) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer " + token);

        Fuel.put(Config.URL_API + "removeFromGroup/" + name).header(header).body(body.getBytes()).responseString(handler);
    }
}
