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
import epitech.eip.slidare.request.Group;

/**
 * Created by ferrei_e on 13/02/2017.
 */

public class AddGroupActivity extends AppCompatActivity {

    static final String TAG = "AddGroupActivity";

    private String mToken;
    private String mBody;

    private TextView mNewGroup;
    private TextView mDone;
    private TextView mSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_add);

        Log.d(TAG, Config.ONCREATE);

        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");

        mNewGroup = (EditText) findViewById(R.id.new_group);
        mSave = (TextView) findViewById(R.id.save);
        mDone = (TextView) findViewById(R.id.done_add);

        View.OnClickListener mSaveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            String name = mNewGroup.getText().toString();
            if (!name.isEmpty()) {
                mBody = "{ \"name\": \"" + name + "\" }";
                try {
                    Handler<String> handler = new Handler<String>() {
                        @Override
                        public void success(@NotNull Request request, @NotNull Response response, String s) {
                            Log.d("createGroup " + Config.ONSUCCESS,response.toString());
                            Toast.makeText(AddGroupActivity.this, Config.GRPCREATED, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                            Log.d("createGroup " + Config.FAILURE,response.toString());
                            Toast.makeText(AddGroupActivity.this, Config.GRPNOCREATED, Toast.LENGTH_SHORT).show();
                        }
                    };
                    Group.createGroup(mBody, mToken, handler);
                } catch (Exception error) {
                    Log.d(TAG, Config.EXCEPTION + error);
                }
            }
            }
        };

        View.OnClickListener mDoneListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };

        mSave.setOnClickListener(mSaveListener);
        mDone.setOnClickListener(mDoneListener);
    }
}
