package epitech.eip.slidare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
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

public class AddContactToGroupActivity extends AppCompatActivity {

    static final String TAG = "AddCtcToGrpActivity";

    private Context mContext;

    private String mToken;
    private String mBody;
    private String mEmail;
    private String mName;
    private String mGroupname;

    private TextView mNewGroupname;
    private TextView mNewEmail;
    private TextView mDelete;
    private TextView mModifyBtn;
    private TextView mDone;
    private TextView mSave;

    private SharingListAdapter mGroupListAdapter;
    private ListView mGroupList;
    private List<String> mList;

    private boolean mExist = false;
    private boolean mBool = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_add_contact);

        Log.d(TAG, "---------> onCreate");

        mContext = getApplicationContext();

        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width),(int)(height));

        mGroupList = (ListView) findViewById(R.id.list_group);
        mNewGroupname = (EditText) findViewById(R.id.groupname);
        mModifyBtn = (TextView) findViewById(R.id.modify);
        mNewEmail = (EditText) findViewById(R.id.new_email);
        mDelete = (TextView) findViewById(R.id.delete);
        mSave = (TextView) findViewById(R.id.save);
        mDone = (TextView) findViewById(R.id.done_add);

        if (mGroupList.getCheckedItemPosition() > -1)
            mGroupname = mGroupList.getItemAtPosition(mGroupList.getCheckedItemPosition()).toString();

        try {
            fetchGroups(mToken);
        } catch (Exception error) {
            Log.d(TAG, "EXCEPTION ERROR : " + error);
        }

        View.OnClickListener mSaveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEmail = mNewEmail.getText().toString();
                mName = mNewGroupname.getText().toString();
                if (!mEmail.isEmpty() && !mName.isEmpty()) {
                    try {
                        fetchGroups(mToken);
                    } catch (Exception error) {
                        Log.d(TAG, "EXCEPTION ERROR : " + error);
                    }
                }
                else {
                    Toast.makeText(AddContactToGroupActivity.this, "Please enter group name and contact email.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        View.OnClickListener mDeleteListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mName = mNewGroupname.getText().toString();
                if (!mName.isEmpty()) {
                    try {
                        removeGroup(mName,mToken);
                    } catch (Exception error) {
                        Log.d(TAG, "EXCEPTION ERROR : " + error);
                    }
                }
                else {
                    Toast.makeText(AddContactToGroupActivity.this, "Please enter a group name to delete.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        View.OnClickListener mModifyBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        View.OnClickListener mDoneListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };

        mModifyBtn.setOnClickListener(mModifyBtnListener);
        mDelete.setOnClickListener(mDeleteListener);
        mSave.setOnClickListener(mSaveListener);
        mDone.setOnClickListener(mDoneListener);
    }

    /*public void fetchGroups(String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://34.238.153.180:50000/fetchGroups").header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("fetchGroups SUCCESS : ",response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));
                    //Log.d(TAG, "----------> result : "+data.getString("groups"));
                    if (data.getString("groups").compareTo("null") != 0) {
                        JSONArray groups = data.getJSONArray("groups");
                        for (int i = 0; i < groups.length(); ++i) {

                            JSONObject group = groups.getJSONObject(i);
                            //Log.d(TAG, "-----> NAME = " + mName);
                            //Log.d(TAG, "-----> GROUPS : " + group.getString("name"));
                            if(mName.compareTo(group.getString("name")) == 0){
                                mExist = true;
                                try {
                                    userContacts(mToken);
                                } catch (Exception error) {
                                    Log.d(TAG, "EXCEPTION ERROR = " + error);
                                }
                            }
                        }
                        if (mExist == false){
                            Toast.makeText(AddContactToGroupActivity.this, "This group does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(AddContactToGroupActivity.this, "You have no group yet.", Toast.LENGTH_SHORT).show();
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
    }*/

    public void fetchGroups(String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://34.238.153.180:50000/fetchGroups").header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("fetchGroups SUCCESS : ",response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));
                    //Log.d(TAG, "----------> result : "+data.getString("groups"));

                    if (data.getString("groups").compareTo("null") != 0) {
                        JSONArray groups = data.getJSONArray("groups");
                        ArrayList<String> list = new ArrayList<String>();
                        for (int i = 0; i < groups.length(); ++i) {
                            JSONObject group = groups.getJSONObject(i);
                            list.add(group.getString("name"));
                            if (mBool){
                                if (group.getString("name").compareTo(mGroupname) == 0){
                                    if (!group.isNull("users")) {
                                        String ids = group.getString("users").replace("[","").replace("]","").replace("{","").replace("}","");
                                        if (ids.contains(",")){
                                            //Log.d(TAG, "----------> MANY");
                                            String[] tab = ids.split(",");
                                            for (int j = 0; j < tab.length; j++) {
                                                try {
                                                    userContact(tab[j].replace("\"", ""), mToken);
                                                } catch (Exception error) {
                                                    Log.d(TAG, "EXCEPTION ERROR : " + error);
                                                }
                                            }
                                        } else {
                                            try {
                                                userContact(ids.replace("\"", ""), mToken);
                                            } catch (Exception error) {
                                                Log.d(TAG, "EXCEPTION ERROR : " + error);
                                            }
                                        }
                                    } else {
                                        Log.d(TAG, "----------> NONE");
                                    }
                                }
                            }
                        }
                        mList = list;
                        mBool = false;
                    }
                    else {
                        Toast.makeText(mContext, "You have no group yet.", Toast.LENGTH_SHORT).show();
                    }
                    mGroupListAdapter = new SharingListAdapter(mList, mContext, mToken);
                    mGroupList.setAdapter(mGroupListAdapter);
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

    public void userContacts(String token) throws Exception {

        Log.d(TAG, "----------> userContacts");

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://34.238.153.180:50000/userContacts").header(header).responseString(new Handler<String>() {
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
                        int lock = 0;
                        for (String elem : tab) {
                            String[] str = elem.split(":");

                            for (int i = 0; i < (str.length - 1) ; i++) {

                                if (str[i].toString().equals("\"id\"") && lock == 1) {
                                    i++;
                                    //Log.d(TAG, "ID = " + str[i]);
                                    str[i] = str[i].replace("\"", "");
                                    mBody = "{ \"contact_identifier\": \"" + str[i] + "\" }";
                                    //Log.d(TAG, "TEST = " + mBody);
                                    try {
                                        addToGroup(mNewGroupname.getText().toString(), mBody, mToken);
                                    } catch (Exception error) {
                                        Log.d(TAG, "EXCEPTION ERROR : " + error);
                                    }
                                    lock = 0;
                                }

                                if (str[i].toString().equals("\"email\"")) {
                                    i++;
                                    str[i] = str[i].replace("\"", "");
                                    if (str[i].toString().equals(mEmail)){
                                        lock = 1;
                                    }
                                }
                            }
                        }
                    }
                    else {
                        /*userListContacts = new String[1];
                        userListContacts[0] = "0 contacts";*/
                        Toast.makeText(AddContactToGroupActivity.this, "You do not have this contact.", Toast.LENGTH_SHORT).show();
                    }
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

    public void userContact(String id, String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://34.238.153.180:50000/userContact/" + id).header(header).responseString(new Handler<String>() {

            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("userContact SUCCESS : ",response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));
                    if (data.getString("contact").compareTo("null") != 0) {
                        JSONObject contacts = data.getJSONObject("contact");
                        String email = contacts.getString("email");
                        ShareActivity.mEmails.put(email);
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

    public void addToGroup(String group, String body, String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.put("http://34.238.153.180:50000/addToGroup/" + group).header(header).body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {

                Log.d("addToGrp SUCCESS : ",response.toString());
                Toast.makeText(AddContactToGroupActivity.this, "Contact successfully added to the group.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {

                Log.d("addToGrp FAILURE : ",response.toString());
                Toast.makeText(AddContactToGroupActivity.this, "This is not one of your contacts.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeGroup(String group, String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.delete("http://34.238.153.180:50000/removeGroup/" + group).header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {

                Log.d("rmGrp SUCCESS : ",response.toString());
                Toast.makeText(AddContactToGroupActivity.this, "Group successfully deleted.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {

                Log.d("rmGrp FAILURE : ",response.toString());
                Toast.makeText(AddContactToGroupActivity.this, "This group cannot be deleted.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
