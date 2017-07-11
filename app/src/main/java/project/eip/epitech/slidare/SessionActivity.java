package project.eip.epitech.slidare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SessionActivity extends AppCompatActivity {

    static final String TAG = "SessionActivity";
    private TextView notificationStatus;
    private Switch notifcationSwitch;
    private TextView alertStatus;
    private Switch alertSwitch;

    private String mFirstname;
    private String mLastname;
    private String mUsername;
    private String mEmail;
    private String mPassword;
    private String mToken;
    private String mId;

    private TextView mPseudo;
    private TextView mEmailAddress;

    private TextView editProfile;

    private Button mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        Log.d(TAG, "---------> onCreate");

        Intent intent = getIntent();
        String token = intent.getStringExtra("token");

        try {
            this.fetchUser(token);

            /*mFirstname = "Dany";
            mLastname = "Omer";
            mUsername = "Dany Omer";
            mEmail = "dany@mail.fr";
            mPassword = "azerty";
            mToken = "";
            mId = "";

            mPseudo = (TextView) findViewById(R.id.pseudo);
            mEmailAddress = (TextView) findViewById(R.id.email);
            mPseudo.setText(mFirstname);
            mEmailAddress.setText(mEmail);*/
        }
        catch (Exception error){
            Log.d("ERROR = ", error.toString());
        }

        editProfile = (TextView) findViewById(R.id.editProfile);
        View.OnClickListener editListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SessionActivity.this, EditProfileActivity.class);
                intent.putExtra("token", mToken);
                startActivity(intent);
            }
        };
        editProfile.setOnClickListener(editListener);

        notificationStatus = (TextView) findViewById(R.id.notificationStatus);
        notifcationSwitch = (Switch) findViewById(R.id.notificationSwitch);
        alertStatus = (TextView) findViewById(R.id.alertStatus);
        alertSwitch = (Switch) findViewById(R.id.alertSwitch);

        notifcationSwitch.setChecked(true);
        //alertSwitch.setChecked(true);
        notifcationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                /*if(isChecked){
                }else{
                }*/

            }
        });


        mLogout = (Button) findViewById(R.id.logout);
        View.OnClickListener mLogoutListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
        mLogout.setOnClickListener(mLogoutListener);
    }

    /*@Override
    protected void onResume() {
        super.onResume();

        try {
            this.fetchUser(mToken);
        }
        catch (Exception error){
            Log.d("ERROR = ", error.toString());
        }
    }*/

    public void fetchUser(String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://34.227.142.101:50000/fetchUser").header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("SUCCESS fetch User : ",response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));

                    mFirstname = data.getString("first_name");
                    mLastname = data.getString("last_name");
                    mUsername = data.getString("username");
                    mEmail = data.getString("email");
                    mPassword = data.getString("password");
                    mToken = data.getString("token");
                    mId = data.getString("id");

                    mPseudo = (TextView) findViewById(R.id.pseudo);
                    mEmailAddress = (TextView) findViewById(R.id.email);
                    mPseudo.setText(mFirstname);
                    mEmailAddress.setText(mEmail);

                } catch (Throwable tx) {
                    tx.printStackTrace();
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("FAILURE fetch User : ",response.toString());
            }
        });
    }
}
