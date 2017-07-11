package project.eip.epitech.slidare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    static final String TAG = "EditProfileActivity";

    private String mToken;
    private String mBody;

    private TextView mNewEmail;
    private TextView mConfirm;

    private Button mSaveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        Log.d(TAG, "---------> onCreate");

        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.2));

        mNewEmail = (EditText) findViewById(R.id.new_email);
        //mConfirm = (EditText) findViewById(R.id.conf_email);

        mSaveButton = (Button) findViewById(R.id.save);

        View.OnClickListener mSaveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Email : ", "value = " + mNewEmail.getText().toString());
                //Log.d("Confirm Email : ", "value = " + mConfirm.getText().toString());

                String email = mNewEmail.getText().toString();
                //String confirm = mConfirm.getText().toString();

                if (!email.isEmpty()) {
                    mBody = "{ \"email\": \"" + email + "\" }";

                    Log.d("TEST = ", mBody);

                    try {
                        addContact(mBody, mToken);
                    } catch (Exception error) {
                        Log.d(TAG, "EXCEPTION ERROR = " + error);
                    }
                }
                finish();
            }
        };

        mSaveButton.setOnClickListener(mSaveListener);
    }

    public void addContact(String body, String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.post("http://34.227.142.101:50000/addContact").header(header).body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("SUCCESS EMAIL : ",response.toString());

                Toast.makeText(EditProfileActivity.this, "Contact successfully added.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("FAIL EMAIL : ",response.toString());

                Toast.makeText(EditProfileActivity.this, "This contact does not exist.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
