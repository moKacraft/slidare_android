package project.eip.epitech.slidare;

import project.eip.epitech.slidare.request.ApiRequest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";

    private Button mBtnSignin;
    private EditText mEmailEditText = null;
    private EditText mPasswordEditText = null;
    private TextView mSignupText;
    private CallbackManager callbackManager;
    private String mBody;
    private String mBodyUpdatePicture;
    private String mUrlPicture;
    private String mToken;
    private Context mContext;
    private LoginResult mLoginResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "----------> onCreate");

        mContext = getApplicationContext();
        mBody = new String();
        mEmailEditText = (EditText) findViewById(R.id.email_field);
        mPasswordEditText = (EditText) findViewById(R.id.password_field);
        mBtnSignin = (Button) findViewById(R.id.btnsignin);
        mSignupText = (TextView) findViewById(R.id.signup_text);
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "user_friends", "public_profile"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "----------> SUCCESS");

                mLoginResult = loginResult;
                //getUserDetailsFromFB(mLoginResult);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "----------> CONCEL");
                Toast.makeText(MainActivity.this, "Login attempt canceled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "----------> ERROR");
                Toast.makeText(MainActivity.this, "Login attempt failed.", Toast.LENGTH_SHORT).show();
            }
        });

        View.OnClickListener mSigninListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Email : ", "value = " + mEmailEditText.getText().toString());
                Log.d("Password : ", "value = " + mPasswordEditText.getText().toString());

                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                mBody = "{ \"email\": \"" + email + "\",\"password\": \"" + password + "\" }";

                try {
                    loginUser(mBody);
                }
                catch (Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR = " + error);
                }
            }
        };

        View.OnClickListener mSignupTextListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        };

        mBtnSignin.setOnClickListener(mSigninListener);
        mSignupText.setOnClickListener(mSignupTextListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public void loginUser(String body) throws Exception {

        Log.d(TAG, "----------> LOGIN USER");

        Fuel.post("http://54.224.110.79:50000/loginUser").body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("SUCCESS login User : ",response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));

                    mToken = data.getString("token");

                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("token", mToken);
                    intent.putExtra("fbUrlImage", mUrlPicture);
                    startActivity(intent);
                    finish();
                }
                catch (Throwable tx) {
                    tx.printStackTrace();
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("FAILURE login User ",response.toString());
                Log.d("FAILURE request ",request.toString());
                Log.d("FAILURE fuelerror ",fuelError.toString());

                Toast.makeText(MainActivity.this, "Login or password incorrect.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUserDetailsFromFB(LoginResult loginResult) {

        GraphRequest req = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try{

                    String[] strings = object.getString("name").split(" ");
                    Log.d(TAG, "----------> FIRSTNAME " + strings[0]);
                    Log.d(TAG, "----------> LASTNAME " + strings[1]);

                    mBody = "{ \"first_name\": \"" + strings[0] + "\",\"last_name\": \"" + strings[1] + "\",\"email\": \"" + object.getString("email") + "\",\"fb_token\": \"" + mLoginResult.getAccessToken().getToken() + "\",\"fb_user_id\": \"" + mLoginResult.getAccessToken().getUserId() + "\" }";
                    Log.d(TAG, "----------> BODY " + mBody);

                    mUrlPicture = "https://graph.facebook.com/" + mLoginResult.getAccessToken().getUserId() + "/picture?type=large";
                    /*String email =  object.getString("email");
                    String birthday = object.getString("birthday");
                    String gender = object.getString("gender");
                    String name = object.getString("name");
                    String id = object.getString("id");
                    String photourl =object.getJSONObject("picture").getJSONObject("data").getString("url");*/
                    try {

                        Log.d(TAG, "----------> BODY BIS :" + mBody);
                        createUser(mBody);
                    }catch (Exception e){
                        Log.d("ERROR LOGIN USER : ",e.toString());
                    }

                }catch (JSONException e)
                {
                    Log.d("ERROR : ",e.toString());
                }

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large)");
        req.setParameters(parameters);
        req.executeAsync();
    }

    public void createUser(String body) throws Exception {

        Fuel.post("http://54.224.110.79:50000/createUser").body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("SUCCESS create User : ",response.toString());
                try {
                    loginUser(mBody);
                }
                catch (Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR = " + error);
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("FAILURE create User : ",response.toString());
                try {
                    loginUser(mBody);
                }
                catch (Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR = " + error);
                }
            }
        });
    }

    public void updateUserPicture(String body, String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.post("http://54.224.110.79:50000/updateUserPicture").header(header).body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("SUCCESS PICTURE : ",response.toString());

                Toast.makeText(MainActivity.this, "Picture successfully updated.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("FAIL PICTURE : ",response.toString());

                Toast.makeText(MainActivity.this, "Picture update failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
