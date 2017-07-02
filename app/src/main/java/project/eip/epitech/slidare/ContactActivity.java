package project.eip.epitech.slidare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.Map;

public class ContactActivity extends AppCompatActivity {

    static final String TAG = "ContactActivity";

    private String mToken = null;
    private String[] userListContacts;
    private ArrayList<String> mList;

    private ImageView mHomeView;
    private ImageView mGroupView;
    private ImageView mProfilView;
    private ImageView mAddContact;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Log.d(TAG, "----------> onCreate");

        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");

        mListView = (ListView) findViewById(R.id.listView);

        try {
            userContacts(mToken);
        } catch (Exception error) {
            Log.d(TAG, "EXCEPTION ERROR = " + error);
        }

        mHomeView = (ImageView) findViewById(R.id.ico_home);
        mGroupView = (ImageView) findViewById(R.id.ico_group);
        mProfilView = (ImageView) findViewById(R.id.ico_profil);
        mAddContact = (ImageView) findViewById(R.id.ico_addContact);

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

        View.OnClickListener mGroupViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "----------> GROUP");

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

                Intent intent = new Intent(ContactActivity.this, EditProfileActivity.class);
                intent.putExtra("token", mToken);
                startActivity(intent);

                try {
                    userContacts(mToken);
                } catch (Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR = " + error);
                }
            }
        };

        mHomeView.setOnClickListener(mHomeViewListener);
        mGroupView.setOnClickListener(mGroupViewListener);
        mProfilView.setOnClickListener(mProfilViewListener);
        mAddContact.setOnClickListener(mAddContactListener);
    }

    public void userContacts(String token) throws Exception {

        Log.d(TAG, "----------> userContacts");

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://54.224.110.79:50000/userContacts").header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("SUCCESS userContacts : ",response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));

                    Log.d(TAG, "----------> result : "+data.getString("contacts"));

                    if (data.getString("contacts").compareTo("null") != 0) {
                        String result = data.getString("contacts");
                        String[] tab = result.split(",");
                        ArrayList<String> list = new ArrayList<String>();
                        for (String elem : tab) {
                            Log.d(TAG, "RESULTATS = " + elem);
                            String[] str = elem.split(":");
                            for (int i = 0; i < (str.length - 1) ; i++) {
                                Log.d(TAG, "STR = " + str[i]);
                                if (str[i].toString().equals("\"email\"")) {
                                    i++;
                                    list.add(str[i]);
                                }
                            }
                        }
                        /*String[] arrayList = new String[list.size()];
                        arrayList = list.toArray(arrayList);
                        Log.d(TAG, "LIST = " + arrayList[0]);
                        userListContacts = arrayList;*/
                        mList = list;
                    }
                    else {
                        userListContacts = new String[1];
                        userListContacts[0] = "0 contacts";
                        Toast.makeText(ContactActivity.this, "You have no contact yet.", Toast.LENGTH_SHORT).show();
                    }
                    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(ContactActivity.this, android.R.layout.simple_list_item_1, userListContacts);
                    CustomListAdapter adapter = new CustomListAdapter(mList, getApplicationContext());
                    mListView.setAdapter(adapter);

                } catch (Throwable tx) {
                    tx.printStackTrace();
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("FAILURE userContacts : ",response.toString());
            }
        });
    }

    public void userContact(String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://54.224.110.79:50000/userContact/{contact_identifier}").header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("SUCCESS userContact : ",response.toString());

            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("FAILURE userContact : ",response.toString());
            }
        });
    }

    public void addContact(String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://54.224.110.79:50000/addContact").header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("SUCCESS addContact : ",response.toString());

            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("FAILURE addContact : ",response.toString());
            }
        });
    }

    public void removeContact(String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://54.224.110.79:50000/removeContact/{contact_identifier}").header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("SUCCESS rmContact : ",response.toString());

            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("FAILURE rmContact : ",response.toString());
            }
        });
    }

}


