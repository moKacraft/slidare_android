package epitech.eip.slidare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;

import epitech.eip.slidare.request.Config;
import epitech.eip.slidare.request.User;

public class ForgetActivity extends AppCompatActivity {

    static final String TAG = "ForgetActivity";

    private String mBody;

    private EditText mEmail;

    private TextView mSendPassword;
    private TextView mCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        mEmail = (EditText) findViewById(R.id.email);
        mSendPassword = (TextView) findViewById(R.id.send_password);
        mCancel = (TextView) findViewById(R.id.cancel_forget);

        View.OnClickListener mSendPasswordListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            try {
                String email = mEmail.getText().toString();
                mBody = "{ \"email\": \"" + email + "\" }";

                Handler<String> handler = new Handler<String>() {
                    @Override
                    public void success(@NotNull Request request, @NotNull Response response, String s) {
                        Log.d("resetPwd " + Config.ONSUCCESS,response.toString());
                        Toast.makeText(ForgetActivity.this, Config.NEW_PWD + mEmail.getText().toString() + ".", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                        Log.d("resetPwd " + Config.FAILURE,response.toString());
                        Toast.makeText(ForgetActivity.this, "Error on sending email.", Toast.LENGTH_SHORT).show();
                    }
                };
                User.resetPassword(mBody, handler);
            }
            catch (Exception error) {
                Log.d(TAG, Config.EXCEPTION + error);
            }
            }
        };

        View.OnClickListener mCancelListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent intent = new Intent(ForgetActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            }
        };

        mSendPassword.setOnClickListener(mSendPasswordListener);
        mCancel.setOnClickListener(mCancelListener);
    }
}
