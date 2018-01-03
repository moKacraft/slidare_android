package epitech.eip.slidare.request;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import epitech.eip.slidare.HomeListAdapter;
import epitech.eip.slidare.R;

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
