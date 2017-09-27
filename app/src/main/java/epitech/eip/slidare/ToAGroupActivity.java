package epitech.eip.slidare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 42350 on 26/09/2017.
 */

public class ToAGroupActivity extends AppCompatActivity {

    static final String TAG = "ToAGroupActivity";

    private Context mContext;

    private boolean mFirst = true;

    private List<String> mGroupnames;
    private List<String> mCounts;
    private List<String> mEmails;
    private List<String> mIds;

    private SharingListAdapter mGroupListAdapter;
    private ListView mGroupList;
    private List<String> mList;

    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toagroup);

        Log.d(TAG, "----------> onCreate");

        mContext = getApplicationContext();

        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");

        try {
            fetchGroups(mToken);
        } catch (Exception error) {
            Log.d(TAG, "EXCEPTION ERROR : " + error);
        }

        mGroupListAdapter = new SharingListAdapter(mList, mContext, mToken);
        mGroupList = (ListView) findViewById(R.id.list_group);

    }

    public void fetchGroups(String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://34.227.142.101:50000/fetchGroups").header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("fetchGroups SUCCESS : ",response.toString());
                mFirst = false;

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));
                    //Log.d(TAG, "----------> result : "+data.getString("groups"));

                    mGroupnames = new ArrayList<String>();
                    mEmails = new ArrayList<String>();
                    mIds = new ArrayList<String>();
                    mCounts = new ArrayList<String>();
                    if (data.getString("groups").compareTo("null") != 0) {
                        JSONArray groups = data.getJSONArray("groups");
                        ArrayList<String> list = new ArrayList<String>();
                        for (int i = 0; i < groups.length(); ++i) {

                            JSONObject group = groups.getJSONObject(i);
                            list.add(group.getString("name"));
                            /*if (!group.isNull("users")) {
                                String ids = group.getString("users").replace("[","").replace("]","").replace("{","").replace("}","");
                                if (ids.contains(",")){
                                    //Log.d(TAG, "----------> MANY");
                                    String[] tab = ids.split(",");
                                    mCounts.add(String.valueOf(tab.length));
                                    for (int j = 0; j < tab.length; j++) {
                                        mIds.add(tab[j].replace("\"", ""));
                                    }
                                } else {
                                    //Log.d(TAG, "----------> ONE");
                                    mCounts.add("1");
                                    mIds.add(ids.replace("\"", ""));
                                }
                            } else {
                                //Log.d(TAG, "----------> NONE");
                                mCounts.add("0");
                                if (i == 0 && !group.isNull("name")){
                                    mGroups.add(mGroupnames.get(i));
                                    List<String> listTmp = new ArrayList<String>();
                                    listTmp.add("No contacts");
                                    mContacts.put(mGroups.get(i), listTmp);
                                    mGroupListAdapter = new CustomListAdapter(mContext, mGroups, mContacts);
                                    mGroupList.setAdapter(mGroupListAdapter);
                                    for ( int l = 0; l < mGroupListAdapter.getGroupCount(); l++)
                                        mGroupList.expandGroup(l);
                                }
                            }*/
                        }
                        mList = list;
                    }
                    else {
                        Toast.makeText(ToAGroupActivity.this, "You have no group yet.", Toast.LENGTH_SHORT).show();
                    }
                    /*for (int k = 0; k < mIds.size(); k++) {
                        try {
                            userContact(mIds.get(k), mIds.size(), mToken);
                        } catch (Exception error) {
                            Log.d(TAG, "EXCEPTION ERROR : " + error);
                        }
                    }*/
                } catch (Throwable tx) {
                    tx.printStackTrace();
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("fetchGroups FAILURE : ",response.toString());
            }
        });
    }
}
