package project.eip.epitech.slidare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;


/**
 * Created by ferrei_e on 01/11/2016.
 */

public class LoginActivity extends AppCompatActivity{

    static final String TAG = "LoginActivity";

    private EditText mEmailEditText = null;
    private EditText mPasswordEditText = null;
    private Button mLoginButton = null;
    private String mBody;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "----------> onCreate");

        mEmailEditText = (EditText) findViewById(R.id.email);
        mPasswordEditText = (EditText) findViewById(R.id.password);
        mLoginButton = (Button) findViewById(R.id.login);

        View.OnClickListener mSubmitListener = new View.OnClickListener() {
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

        mLoginButton.setOnClickListener(mSubmitListener);

    }

    public void loginUser(String body) throws Exception {
        Fuel.post("http://54.152.163.253:50000/loginUser").body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("SUCCESS login User : ",response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));

                    Intent intent = new Intent(LoginActivity.this, SessionActivity.class);
                    intent.putExtra("token", data.getString("token"));
                    startActivity(intent);
                    finish();
                }
                catch (Throwable tx) {
                    tx.printStackTrace();
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("FAILURE login User : ",response.toString());
            }
        });
    }
}
