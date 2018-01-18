package epitech.eip.slidare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import epitech.eip.slidare.request.Contact;

/**
 * Created by ferrei_e on 13/02/2017.
 */

public class AddContactActivity extends AppCompatActivity {

    static final String TAG = "AddContactActivity";

    private String mToken;
    private String mBody;

    private TextView mNewEmail;
    private TextView mCancel;
    private TextView mDone;
    private TextView mSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_add);

        Log.d(TAG, Config.ONCREATE);

        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");

        mNewEmail = (EditText) findViewById(R.id.new_email);
        mSave = (TextView) findViewById(R.id.save);
        mCancel = (TextView) findViewById(R.id.cancel_add);
        mDone = (TextView) findViewById(R.id.done_add);

        View.OnClickListener mSaveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            String email = mNewEmail.getText().toString();
            if (!email.isEmpty()) {
                mBody = "{ \"email\": \"" + email + "\" }";
                try {
                    Handler<String> handler = new Handler<String>() {
                        @Override
                        public void success(@NotNull Request request, @NotNull Response response, String s) {
                            Log.d("addContact " + Config.ONSUCCESS,response.toString());
                            Toast.makeText(AddContactActivity.this, Config.CONTACTADDED, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                            Log.d("addContact " + Config.FAILURE,response.toString());
                            Toast.makeText(AddContactActivity.this, Config.CTCTNOTEXIST, Toast.LENGTH_SHORT).show();
                        }
                    };
                    Contact.addContact(mBody, mToken, handler);
                } catch (Exception error) {
                    Log.d(TAG, Config.EXCEPTION + error);
                }
            }
            }
        };

        View.OnClickListener mCancelListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };

        View.OnClickListener mDoneListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };

        mSave.setOnClickListener(mSaveListener);
        mCancel.setOnClickListener(mCancelListener);
        mDone.setOnClickListener(mDoneListener);
    }
}
