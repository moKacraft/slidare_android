package epitech.eip.slidare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
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
 * Created by ferrei_e on 13/02/2017.
 */

public class GroupActivity extends AppCompatActivity {

    static final String TAG = "GroupActivity";

    private Context mContext;

    private boolean mFirst = true;

    private static int index;

    private String mToken;
    private String mEmail;

    private TextView mContactSwitch;

    private ImageView mHomeView;
    private ImageView mGroupView;
    private ImageView mProfilView;
    private ImageView mAddGroup;
    private ImageView mSettings;

    private List<String> mGroups;
    private List<String> mGroupnames;
    private List<String> mCounts;
    private List<String> mEmails;
    private List<String> mIds;

    private ExpandableListAdapter mGroupListAdapter;
    private ExpandableListView mGroupList;
    private HashMap<String, List<String>> mContacts;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_activity);

        Log.d(TAG, "----------> create");

        mContext = getApplicationContext();
        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");

        try {
            fetchGroups(mToken);
        } catch (Exception error) {
            Log.d(TAG, "EXCEPTION ERROR : " + error);
        }

        mGroups = new ArrayList<String>();
        mContacts = new HashMap<String, List<String>>();
        mHomeView = (ImageView) findViewById(R.id.ico_home);
        mGroupView = (ImageView) findViewById(R.id.ico_group);
        mProfilView = (ImageView) findViewById(R.id.ico_profil);
        mAddGroup = (ImageView) findViewById(R.id.ico_addGroup);
        mContactSwitch = (TextView) findViewById(R.id.contact_off);
        mGroupList = (ExpandableListView) findViewById(R.id.group_listview);
        mSettings = (ImageView) findViewById(R.id.ico_settings);

        View.OnClickListener mHomeViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "----------> HOME");

                Intent intent = new Intent(GroupActivity.this, HomeActivity.class);
                intent.putExtra("token", mToken);
                startActivity(intent);
                finish();
            }
        };

        View.OnClickListener mGroupViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "----------> CONTACT");

                Intent intent = new Intent(GroupActivity.this, ContactActivity.class);
                intent.putExtra("token", mToken);
                startActivity(intent);
                finish();
            }
        };

        View.OnClickListener mProfilViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "----------> PROFIL");

                Intent intent = new Intent(GroupActivity.this, SettingsActivity.class);
                intent.putExtra("token", mToken);
                startActivity(intent);
                finish();
            }
        };

        View.OnClickListener mAddGroupListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "----------> ADD GROUP");

                Intent intent = new Intent(GroupActivity.this, AddGroupActivity.class);
                intent.putExtra("token", mToken);
                startActivity(intent);
            }
        };

        View.OnClickListener mSettingsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "----------> SETTINGS");

                Intent intent = new Intent(GroupActivity.this, AddContactToGroupActivity.class);
                intent.putExtra("token", mToken);
                startActivity(intent);
            }
        };

        View.OnClickListener mContactSwitchListener = mGroupViewListener;

        mHomeView.setOnClickListener(mHomeViewListener);
        mGroupView.setOnClickListener(mGroupViewListener);
        mProfilView.setOnClickListener(mProfilViewListener);
        mAddGroup.setOnClickListener(mAddGroupListener);
        mSettings.setOnClickListener(mSettingsListener);
        mContactSwitch.setOnClickListener(mContactSwitchListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mFirst == false) {
            mGroups = new ArrayList<String>();
            mContacts = new HashMap<String, List<String>>();
            mGroupListAdapter = new ExpandableListAdapter(mContext, mGroups, mContacts);
            try {
                fetchGroups(mToken);
            } catch (Exception error) {
                Log.d(TAG, "EXCEPTION ERROR : " + error);
            }
        }
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
                        for (int i = 0; i < groups.length(); ++i) {

                            JSONObject group = groups.getJSONObject(i);
                            mGroupnames.add(group.getString("name"));
                            if (!group.isNull("users")) {
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
                                    mGroupListAdapter = new ExpandableListAdapter(mContext, mGroups, mContacts);
                                    mGroupList.setAdapter(mGroupListAdapter);
                                    for ( int l = 0; l < mGroupListAdapter.getGroupCount(); l++)
                                        mGroupList.expandGroup(l);
                                }
                            }
                        }
                    }
                    else {
                        Toast.makeText(GroupActivity.this, "You have no group yet.", Toast.LENGTH_SHORT).show();
                    }
                    for (int k = 0; k < mIds.size(); k++) {
                        try {
                            userContact(mIds.get(k), mIds.size(), mToken);
                        } catch (Exception error) {
                            Log.d(TAG, "EXCEPTION ERROR : " + error);
                        }
                    }
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

    public void userContact(String id, final int size, String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://34.227.142.101:50000/userContact/" + id).header(header).responseString(new Handler<String>() {

            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("userContact SUCCESS : ",response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));
                    if (data.getString("contact").compareTo("null") != 0) {
                        JSONObject contacts = data.getJSONObject("contact");
                        mEmail = contacts.getString("email");
                        mEmails.add(mEmail);
                        index++;
                        if (index == size){
                            index = 0;
                            int k = 0;
                            for (int i = 0; i < mGroupnames.size(); i++){
                                //Log.d(TAG, "----------> GROUPNAME : " + mGroupnames.get(i));
                                mGroups.add(mGroupnames.get(i));
                                //Log.d(TAG, "----------> COUNT : " + mCounts.get(i));
                                if (mCounts.get(i).compareTo("0") != 0){
                                    List<String> listEmails = new ArrayList<String>();
                                    for (int j = 0; j < Integer.parseInt(mCounts.get(i)); j++) {
                                        //Log.d(TAG, "----------> EMAIL : " + mEmails.get(k));
                                        listEmails.add(mEmails.get(k));
                                        k++;
                                    }
                                    mContacts.put(mGroups.get(i), listEmails);
                                } else {
                                    //Log.d(TAG, "----------> EMAIL : No contacts");
                                    List<String> listTmp = new ArrayList<String>();
                                    listTmp.add("No contacts");
                                    mContacts.put(mGroups.get(i), listTmp);
                                }
                            }
                            mGroupListAdapter = new ExpandableListAdapter(mContext, mGroups, mContacts);
                            mGroupList.setAdapter(mGroupListAdapter);
                            for ( int i = 0; i < mGroupListAdapter.getGroupCount(); i++)
                                mGroupList.expandGroup(i);
                            mFirst = false;
                        }
                    }
                } catch (Throwable tx) {
                    tx.printStackTrace();
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("userContact FAILURE : ",response.toString());
            }
        });
    }
}
