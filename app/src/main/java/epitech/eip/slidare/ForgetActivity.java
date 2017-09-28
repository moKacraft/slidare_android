package epitech.eip.slidare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;

/**
 * Created by 42350 on 28/09/2017.
 */

public class ForgetActivity extends AppCompatActivity {

    static final String TAG = "ForgetActivity";

    private Context mContext;

    private String mBody;

    private EditText mEmail;

    private TextView mSendPassword;
    private TextView mCancel;
    private TextView mDone;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        mContext = getApplication();

        mEmail = (EditText) findViewById(R.id.email);
        mSendPassword = (TextView) findViewById(R.id.send_password);
        mCancel = (TextView) findViewById(R.id.cancel_forget);
        mDone = (TextView) findViewById(R.id.done_forget);

        View.OnClickListener mSendPasswordListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String email = mEmail.getText().toString();
                    mBody = "{ \"email\": \"" + email + "\" }";

                    Log.d(TAG, "BODY = " + mBody);
                    resetPassword(mBody);
                }
                catch (Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR : " + error);
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
        mDone.setOnClickListener(mCancelListener);
    }

    public void resetPassword(String body) throws Exception {

        Fuel.post("http://34.227.142.101:50000/resetPassword").body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("createUser SUCCESS : ",response.toString());
                Toast.makeText(ForgetActivity.this, "A new password have been send to " + mEmail.getText().toString() + ".", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("createUser FAILURE: ",response.toString());
                Toast.makeText(ForgetActivity.this, "Error on sending email.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
