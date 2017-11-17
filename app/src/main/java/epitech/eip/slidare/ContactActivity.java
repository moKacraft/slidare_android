package epitech.eip.slidare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
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
 * Created by ferrei_e on 13/02/2017.
 */

public class ContactActivity extends AppCompatActivity {

    static final String TAG = "ContactActivity";

    private Context mContext;

    private boolean mFirst = true;

    private String mToken;

    private TextView mGroupSwitch;

    private ImageView mHomeView;
    private ImageView mProfilView;
    private ImageView mAddContact;

    private CustomListAdapter mAdapter;
    private ListView mListView;
    private List<String> mList;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Log.d(TAG, "----------> onCreate");

        mContext = getApplicationContext();
        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");
        mAdapter = new CustomListAdapter(mList, mContext, mToken);

        try {
            userContacts(mToken);
        } catch (Exception error) {
            Log.d(TAG, "EXCEPTION ERROR = " + error);
        }

        mListView = (ListView) findViewById(R.id.listView);
        mHomeView = (ImageView) findViewById(R.id.ico_home);
        mProfilView = (ImageView) findViewById(R.id.ico_profil);
        mAddContact = (ImageView) findViewById(R.id.ico_addContact);
        mGroupSwitch = (TextView) findViewById(R.id.group_off);

        View.OnClickListener mHomeViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "----------> HOME");

                Intent intent = new Intent(ContactActivity.this, HomeActivity.class);
                intent.putExtra("token", mToken);
                startActivity(intent);
                finish();
            }
        };

        View.OnClickListener mProfilViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "----------> PROFIL");

                Intent intent = new Intent(ContactActivity.this, SettingsActivity.class);
                intent.putExtra("token", mToken);
                startActivity(intent);
                finish();
            }
        };

        View.OnClickListener mAddContactListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "----------> ADD CONTACT");

                Intent intent = new Intent(ContactActivity.this, AddContactActivity.class);
                intent.putExtra("token", mToken);
                startActivity(intent);
            }
        };

        View.OnClickListener mGroupSwitchListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "-------------> GROUPSWITCH");

                Intent intent = new Intent(ContactActivity.this, GroupActivity.class);
                intent.putExtra("token", mToken);
                startActivity(intent);
                finish();
            }
        };

        mHomeView.setOnClickListener(mHomeViewListener);
        mProfilView.setOnClickListener(mProfilViewListener);
        mAddContact.setOnClickListener(mAddContactListener);
        mGroupSwitch.setOnClickListener(mGroupSwitchListener);

        mSearchView = (android.widget.SearchView) findViewById(R.id.search);
        mSearchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.d(TAG, "SUBMIT");

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Log.d(TAG, "CHANGE = " + newText);
                mAdapter.getFilter().filter(newText.toString());
                mListView.setAdapter(mAdapter);
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mFirst == false) {
            mList = new ArrayList<String>();
            mAdapter = new CustomListAdapter(mList, mContext, mToken);
            try {
                userContacts(mToken);
            } catch (Exception error) {
                Log.d(TAG, "EXCEPTION ERROR : " + error);
            }
        }
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
                        for (String elem : tab) {
                            String[] str = elem.split(":");
                            for (int i = 0; i < (str.length - 1) ; i++) {
                                if (str[i].toString().equals("\"email\"")) {
                                    i++;
                                    str[i]= str[i].replace("\"", "");
                                    list.add(str[i]);
                                    //Log.d(TAG, "ELEM = " + str[i]);
                                }
                            }
                        }
                        mList = list;
                    }
                    else {
                        Toast.makeText(ContactActivity.this, "You have no contact yet.", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter = new CustomListAdapter(mList, mContext, mToken);
                    mListView.setAdapter(mAdapter);
                    mFirst = false;
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
