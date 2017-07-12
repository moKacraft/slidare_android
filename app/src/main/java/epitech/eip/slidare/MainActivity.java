package epitech.eip.slidare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
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

/**
 * Created by ferrei_e on 13/02/2017.
 */

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MainActivity";

    private Context mContext;

    private String mBody;
    private String mToken;
    private String mUrlPicture;

    private TextView mSignupText;

    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private Button mBtnSignin;

    private CallbackManager callbackManager;
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
        SpannableString content = new SpannableString("No account ? Sign up");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        mSignupText.setText(content);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "user_friends", "public_profile"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "----------> FACEBOOK onSuccess");
                mLoginResult = loginResult;
                getUserDetailsFromFB(mLoginResult);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "----------> onCancel");
                Toast.makeText(MainActivity.this, "Login attempt canceled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "----------> onError");
                Toast.makeText(MainActivity.this, "Login attempt failed.", Toast.LENGTH_SHORT).show();
            }
        });

        View.OnClickListener mSigninListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Log.d("Email : ", "value = " + mEmailEditText.getText().toString());
                //Log.d("Password : ", "value = " + mPasswordEditText.getText().toString());

                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                mBody = "{ \"email\": \"" + email + "\",\"password\": \"" + password + "\" }";

                try {
                    loginUser(mBody);
                }
                catch (Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR : " + error);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);

        Log.d(TAG, "----------> ON ACTIVITY RESULT");
    }

    public void loginUser(String body) throws Exception {

        Log.d(TAG, "----------> loginUser");

        Fuel.post("http://34.227.142.101:50000/loginUser").body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("loginUser SUCCESS : ",response.toString());

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
                Log.d("loginUser FAILURE : ",response.toString());
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
                    //Log.d(TAG, "----------> FIRSTNAME " + strings[0]);
                    //Log.d(TAG, "----------> LASTNAME " + strings[1]);
                    mBody = "{ \"first_name\": \"" + strings[0] + "\",\"last_name\": \"" + strings[1] + "\",\"email\": \"" + object.getString("email") + "\",\"fb_token\": \"" + mLoginResult.getAccessToken().getToken() + "\",\"fb_user_id\": \"" + mLoginResult.getAccessToken().getUserId() + "\" }";
                    //Log.d(TAG, "----------> BODY " + mBody);
                    mUrlPicture = "https://graph.facebook.com/" + mLoginResult.getAccessToken().getUserId() + "/picture?type=large";
                    /*String email =  object.getString("email");
                    String birthday = object.getString("birthday");
                    String gender = object.getString("gender");
                    String name = object.getString("name");
                    String id = object.getString("id");
                    String photourl =object.getJSONObject("picture").getJSONObject("data").getString("url");*/
                    try {
                        //Log.d(TAG, "----------> BODY BIS :" + mBody);
                        createUser(mBody);
                    }
                    catch (Exception e){
                        Log.d(TAG, "EXCEPTION ERROR : " + e);
                    }
                }
                catch (JSONException e) {
                    Log.d(TAG, "EXCEPTION ERROR : " + e);
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large)");
        req.setParameters(parameters);
        req.executeAsync();
    }

    public void createUser(String body) throws Exception {

        Fuel.post("http://34.227.142.101:50000/createUser").body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("createUser SUCCESS : ",response.toString());
                try {
                    loginUser(mBody);
                }
                catch (Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR : " + error);
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("createUser FAILURE: ",response.toString());
                try {
                    loginUser(mBody);
                }
                catch (Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR : " + error);
                }
            }
        });
    }
}
