package epitech.eip.slidare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import epitech.eip.slidare.request.Config;
import epitech.eip.slidare.request.Contact;
import epitech.eip.slidare.request.Group;

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

    private boolean mBool = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_add_contact);

        Log.d(TAG, Config.ONCREATE);

        mContext = getApplicationContext();
        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");

        mGroupList = (ListView) findViewById(R.id.list_group);
        mNewGroupname = (EditText) findViewById(R.id.groupname);
        mNewEmail = (EditText) findViewById(R.id.new_email);
        mModifyBtn = (TextView) findViewById(R.id.modify);
        mSave = (TextView) findViewById(R.id.save);
        mDelete = (TextView) findViewById(R.id.delete);
        mDone = (TextView) findViewById(R.id.done_add);

        try {
            Handler<String> handler = new Handler<String>() {
                @Override
                public void success(@NotNull Request request, @NotNull Response response, String s) {
                    Log.d("fetchGroups " + Config.ONSUCCESS,response.toString());
                    try {
                        JSONObject data = new JSONObject(new String(response.getData()));
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
                                                String[] tab = ids.split(",");
                                                for (int j = 0; j < tab.length; j++) {
                                                    try {
                                                        Handler<String> handler = new Handler<String>() {
                                                            @Override
                                                            public void success(@NotNull Request request, @NotNull Response response, String s) {
                                                                Log.d("userContact " + Config.ONSUCCESS,response.toString());
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
                                                                Log.d("userContact " + Config.FAILURE,response.toString());
                                                            }
                                                        };
                                                        Contact.userContact(tab[j].replace("\"", ""), mToken, handler);
                                                    } catch (Exception error) {
                                                        Log.d(TAG, Config.EXCEPTION + error);
                                                    }
                                                }
                                            } else {
                                                try {
                                                    Handler<String> handler = new Handler<String>() {
                                                        @Override
                                                        public void success(@NotNull Request request, @NotNull Response response, String s) {
                                                            Log.d("userContact " + Config.ONSUCCESS,response.toString());
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
                                                            Log.d("userContact " + Config.FAILURE,response.toString());
                                                        }
                                                    };
                                                    Contact.userContact(ids.replace("\"", ""), mToken, handler);
                                                } catch (Exception error) {
                                                    Log.d(TAG, Config.EXCEPTION + error);
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
                            Toast.makeText(mContext, Config.NO_GROUP, Toast.LENGTH_SHORT).show();
                        }
                        mGroupListAdapter = new SharingListAdapter(mList, mContext, mToken);
                        mGroupList.setAdapter(mGroupListAdapter);
                    } catch (Throwable tx) {
                        tx.printStackTrace();
                    }
                }

                @Override
                public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                    Log.d("fetchGroups " + Config.FAILURE,response.toString());
                }
            };
            Group.fetchGroups(mToken, handler);
        } catch (Exception error) {
            Log.d(TAG, Config.EXCEPTION + error);
        }

        View.OnClickListener mSaveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (mGroupList.getCheckedItemPosition() > -1) {
                mGroupname = mGroupList.getItemAtPosition(mGroupList.getCheckedItemPosition()).toString();
            }
            mEmail = mNewEmail.getText().toString();
            if (mEmail != null && mGroupname != null) {
                try {
                    Handler<String> handler = new Handler<String>() {
                        @Override
                        public void success(@NotNull Request request, @NotNull Response response, String s) {
                            Log.d("userContacts " + Config.ONSUCCESS,response.toString());
                            try {
                                JSONObject data = new JSONObject(new String(response.getData()));
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
                                                str[i] = str[i].replace("\"", "");
                                                mBody = "{ \"contact_identifier\": \"" + mEmail + "\" }";
                                                //Log.d(TAG, "BODY = " + mBody);
                                                try {
                                                    Handler<String> handler = new Handler<String>() {
                                                        @Override
                                                        public void success(@NotNull Request request, @NotNull Response response, String s) {
                                                            Log.d("addToGrp " + Config.ONSUCCESS,response.toString());
                                                            Toast.makeText(mContext, Config.ADDTOGROUP, Toast.LENGTH_SHORT).show();
                                                        }

                                                        @Override
                                                        public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                                                            Log.d("addToGrp " + Config.FAILURE,response.toString());
                                                            Toast.makeText(mContext, Config.NOTACONTACT, Toast.LENGTH_SHORT).show();
                                                        }
                                                    };
                                                    Group.addToGroup(mGroupname, mBody, mToken, handler);
                                                } catch (Exception error) {
                                                    Log.d(TAG, Config.EXCEPTION + error);
                                                }
                                                lock = 0;
                                            }

                                            if (str[i].toString().equals("\"email\"")) {
                                                i++;
                                                str[i] = str[i].replace("\"", "");
                                                if (str[i].toString().equals(mEmail)){
                                                    lock = 1;
                                                    //Log.d(TAG, "EMAIL = " + mEmail);
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Throwable tx) {
                                tx.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                            Log.d("userContacts " + Config.FAILURE,response.toString());
                        }
                    };
                    Contact.userContacts(mToken, handler);
                }
                catch (Exception e){
                    Log.d(TAG, Config.EXCEPTION + e);
                }
            }
            else {
                Toast.makeText(mContext, Config.MISSGRPANDMAIL, Toast.LENGTH_SHORT).show();
            }
            }
        };

        View.OnClickListener mDeleteListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (mGroupList.getCheckedItemPosition() > -1) {
                mGroupname = mGroupList.getItemAtPosition(mGroupList.getCheckedItemPosition()).toString();
            }
            if (mGroupname != null) {
                try {
                    Handler<String> handler = new Handler<String>() {
                        @Override
                        public void success(@NotNull Request request, @NotNull Response response, String s) {
                            Log.d("removeGroup " + Config.ONSUCCESS,response.toString());
                            Toast.makeText(mContext, Config.GRPDELETED, Toast.LENGTH_SHORT).show();
                            mList.remove(mGroupList.getCheckedItemPosition());
                            mGroupListAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                            Log.d("removeGroup " + Config.FAILURE,response.toString());
                            Toast.makeText(mContext, Config.GRPDELFAILED, Toast.LENGTH_SHORT).show();
                        }
                    };
                    Group.removeGroup(mGroupname, mToken, handler);
                } catch (Exception error) {
                    Log.d(TAG, Config.EXCEPTION + error);
                }
            }
            else {
                Toast.makeText(mContext, Config.SELGRPTODEL, Toast.LENGTH_SHORT).show();
            }
            }
        };

        View.OnClickListener mModifyBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGroupList.getCheckedItemPosition() > -1) {
                    mGroupname = mGroupList.getItemAtPosition(mGroupList.getCheckedItemPosition()).toString();
                }
                mName = mNewGroupname.getText().toString();
                if (mGroupname != null && mName != null) {
                    SharedPreferences settings = getSharedPreferences("USERDATA", 0);
                    mBody = "{ \"name\": \"" + mGroupname + "\",\"new_name\": \"" + mName + "\",\"id\": \"" + settings.getString("userId", null) + "\" }";
                    try {
                        Handler<String> handler = new Handler<String>() {
                            @Override
                            public void success(@NotNull Request request, @NotNull Response response, String s) {
                                Log.d("renameGroup " + Config.ONSUCCESS,response.toString());
                                Toast.makeText(mContext, Config.GRPRENAMED, Toast.LENGTH_SHORT).show();
                                mList.set(mGroupList.getCheckedItemPosition(), mName);
                                mGroupListAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                                Log.d("renameGroup " + Config.FAILURE,response.toString());
                                Toast.makeText(mContext, Config.RENAMEFAIL, Toast.LENGTH_SHORT).show();
                            }
                        };
                        Group.renameGroup(mBody, mToken, handler);
                    } catch (Exception error) {
                        Log.d(TAG, Config.EXCEPTION + error);
                    }
                }
                else {
                    Toast.makeText(mContext, Config.RENAMEPLSE, Toast.LENGTH_SHORT).show();
                }
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
}
