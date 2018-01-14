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

import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import epitech.eip.slidare.request.Config;
import epitech.eip.slidare.request.User;

/**
 * Created by ferrei_e on 13/02/2017.
 */

public class SignupActivity extends AppCompatActivity {

    static final String TAG = "SignupActivity";

    private String mBody;

    private TextView mCancelText;

    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mEmailEditText;
    private EditText mEmailConfirmEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordConfirmEditText;

    private Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Log.d(TAG, Config.ONCREATE);

        mFirstNameEditText = (EditText) findViewById(R.id.firstname);
        mLastNameEditText = (EditText) findViewById(R.id.lastname);
        mEmailEditText = (EditText) findViewById(R.id.email);
        mEmailConfirmEditText = (EditText) findViewById(R.id.email_confirm);
        mPasswordEditText = (EditText) findViewById(R.id.password);
        mPasswordConfirmEditText = (EditText) findViewById(R.id.password_confirm);
        mSubmitButton = (Button) findViewById(R.id.submit);
        mCancelText = (TextView) findViewById(R.id.cancel);

        View.OnClickListener mSubmitListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            String firstname = mFirstNameEditText.getText().toString();
            String lastname = mLastNameEditText.getText().toString();
            String email = mEmailEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();

            if (password.compareTo(mPasswordConfirmEditText.getText().toString()) != 0)
                Toast.makeText(SignupActivity.this, Config.IDENTICAL_PWD, Toast.LENGTH_SHORT).show();
            else if (email.compareTo(mEmailConfirmEditText.getText().toString()) != 0)
                Toast.makeText(SignupActivity.this, Config.IDENTICAL_MAIL, Toast.LENGTH_SHORT).show();
            else {
                mBody = "{ \"first_name\": \"" + firstname + "\",\"last_name\": \"" + lastname + "\",\"email\": \"" + email + "\",\"password\": \"" + password + "\" }";
                try {
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
                                            String token = data.getString("token");
                                            String id = data.getString("id");

                                            SharedPreferences settings = getSharedPreferences("USERDATA", 0);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString("userToken", token);
                                            editor.putString("userId", id);
                                            editor.apply();

                                            Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                                            intent.putExtra("token", token);
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
                                        Toast.makeText(SignupActivity.this, new String(response.getData()), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SignupActivity.this, Config.CREATEUSER_FAIL, Toast.LENGTH_SHORT).show();
                        }
                    };
                    User.createUser(mBody, handler);
                }
                catch (Exception error) {
                    Log.d(TAG, Config.EXCEPTION + error);
                }
            }
            }
        };

        View.OnClickListener mCancelTextListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            }
        };

        mSubmitButton.setOnClickListener(mSubmitListener);
        mCancelText.setOnClickListener(mCancelTextListener);
    }
}
