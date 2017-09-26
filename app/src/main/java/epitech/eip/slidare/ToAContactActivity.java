package epitech.eip.slidare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 42350 on 26/09/2017.
 */

public class ToAContactActivity extends AppCompatActivity {

    static final String TAG = "ToAContactActivity";

    private Context mContext;

    //private boolean mFirst = true;

    private SharingListAdapter mAdapter;
    private ListView mListView;
    private List<String> mList;

    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toacontact);

        Log.d(TAG, "----------> onCreate");

        mContext = getApplicationContext();

        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");

        //mAdapter = new SharingListAdapter(mList, mContext, mToken);

        try {
            userContacts(mToken);
        } catch (Exception error) {
            Log.d(TAG, "EXCEPTION ERROR = " + error);
        }

        mListView = (ListView) findViewById(R.id.list_contact);

    }

    public void userContacts(String token) throws Exception {

        Log.d(TAG, "----------> userContacts");

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://34.227.142.101:50000/userContacts").header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("userContacts SUCCESS : ",response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));
                    //Log.d(TAG, "----------> result : "+data.getString("contacts"));

                    if (data.getString("contacts").compareTo("null") != 0) {
                        String result = data.getString("contacts");
                        String[] tab = result.split(",");
                        ArrayList<String> list = new ArrayList<String>();
                        for (String elem : tab) {
                            String[] str = elem.split(":");
                            for (int i = 0; i < (str.length - 1) ; i++) {
                                if (str[i].toString().equals("\"email\"")) {
                                    i++;
                                    str[i]= str[i].replace("\"", "");
                                    list.add(str[i]);
                                    Log.d(TAG, "ELEM = " + str[i]);
                                }
                            }
                        }
                        mList = list;
                    }
                    else {
                        Toast.makeText(ToAContactActivity.this, "You have no contact yet.", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter = new SharingListAdapter(mList, mContext, mToken);
                    mListView.setAdapter(mAdapter);
                    //mFirst = false;
                } catch (Throwable tx) {
                    tx.printStackTrace();
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("userContacts FAILURE : ",response.toString());
            }
        });
    }
}
