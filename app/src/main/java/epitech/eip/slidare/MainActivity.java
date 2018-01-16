package epitech.eip.slidare;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Arrays;

import epitech.eip.slidare.request.Config;
import epitech.eip.slidare.request.User;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MainActivity";

    private String mBody;
    private String mToken;
    private String mUrlPicture;
    private String mId;

    private TextView mSignupText;
    private TextView mForget;

    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private Button mBtnSignin;

    private CallbackManager callbackManager;
    private LoginResult mLoginResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, Config.ONCREATE);

        mEmailEditText = (EditText) findViewById(R.id.email_field);
        mPasswordEditText = (EditText) findViewById(R.id.password_field);
        mBtnSignin = (Button) findViewById(R.id.btnsignin);
        mForget = (TextView) findViewById(R.id.forget);
        mSignupText = (TextView) findViewById(R.id.signup_text);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "user_friends", "public_profile"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, Config.ONSUCCESS);
                mLoginResult = loginResult;
                getUserDetailsFromFB(mLoginResult);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, Config.ONCANCEL);
                Toast.makeText(MainActivity.this, Config.LOGIN_CANCEL, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, Config.ONERROR);
                Toast.makeText(MainActivity.this, Config.LOGIN_FAIL, Toast.LENGTH_SHORT).show();
            }
        });

        View.OnClickListener mSigninListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            String email = mEmailEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();
            mBody = "{ \"email\": \"" + email + "\",\"password\": \"" + password + "\" }";

            try {
                Handler<String> handler = new Handler<String>() {
                    @Override
                    public void success(@NotNull Request request, @NotNull Response response, String s) {
                        Log.d("loginUser " + Config.ONSUCCESS ,response.toString());

                        try {
                            JSONObject data = new JSONObject(new String(response.getData()));
                            mToken = data.getString("token");
                            mId = data.getString("id");

                            Log.d(TAG, "DATA = " + data);

                            SharedPreferences settings = getSharedPreferences("USERDATA", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("userToken", mToken);
                            editor.putString("userId", mId);
                            editor.putString("fbUrlImage", mUrlPicture);
                            editor.apply();

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
                        Log.d("loginUser " + Config.FAILURE,response.toString());
                        Toast.makeText(MainActivity.this, Config.LOGIN_ERROR, Toast.LENGTH_SHORT).show();
                    }
                };
                User.loginUser(mBody, handler);
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
            }
        };

        View.OnClickListener mForgetListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, ForgetActivity.class);
            startActivity(intent);
            }
        };

        mForget.setOnClickListener(mForgetListener);
        mBtnSignin.setOnClickListener(mSigninListener);
        mSignupText.setOnClickListener(mSignupTextListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void getUserDetailsFromFB(LoginResult loginResult) {

        GraphRequest req = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
            try{
                String[] strings = object.getString("name").split(" ");
                mBody = "{ \"first_name\": \"" + strings[0] + "\",\"last_name\": \"" + strings[1] + "\",\"email\": \"" + object.getString("email") + "\",\"fb_token\": \"" + mLoginResult.getAccessToken().getToken() + "\",\"fb_user_id\": \"" + mLoginResult.getAccessToken().getUserId() + "\" }";
                mUrlPicture = "https://graph.facebook.com/" + mLoginResult.getAccessToken().getUserId() + "/picture?type=large";
                Handler<String> handler = new Handler<String>() {
                    @Override
                    public void success(@NotNull Request request, @NotNull Response response, String s) {
                        Log.d("createUser " + Config.ONSUCCESS,response.toString());
                        try {
                            Handler<String> handler = new Handler<String>() {
                                @Override
                                public void success(@NotNull Request request, @NotNull Response response, String s) {
                                    Log.d("loginUser " + Config.ONSUCCESS,response.toString());

                                    try {
                                        JSONObject data = new JSONObject(new String(response.getData()));
                                        mToken = data.getString("token");
                                        mId = data.getString("id");

                                        SharedPreferences settings = getSharedPreferences("USERDATA", 0);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString("userToken", mToken);
                                        editor.putString("userId", mId);
                                        editor.putString("fbUrlImage", mUrlPicture);
                                        editor.apply();

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
                                    Log.d("loginUser " + Config.FAILURE,response.toString());
                                    Toast.makeText(MainActivity.this, Config.LOGIN_ERROR, Toast.LENGTH_SHORT).show();
                                }
                            };
                            User.loginUser(mBody, handler);
                        }
                        catch (Exception error) {
                            Log.d(TAG, Config.EXCEPTION + error);
                        }
                    }

                    @Override
                    public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                        Log.d("createUser " + Config.FAILURE,response.toString());
                        Toast.makeText(MainActivity.this, new String(response.getData()), Toast.LENGTH_SHORT).show();
                    }
                };
                User.createUser(mBody, handler);
            }
            catch (Exception e){
                Log.d(TAG, Config.EXCEPTION + e);
            }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large)");
        req.setParameters(parameters);
        req.executeAsync();
    }
}