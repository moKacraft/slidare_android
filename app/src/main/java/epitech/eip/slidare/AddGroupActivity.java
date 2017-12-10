package epitech.eip.slidare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
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

import java.util.HashMap;
import java.util.Map;

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

        Log.d(TAG, "---------> onCreate");

        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width),(int)(height));

        mNewGroup = (EditText) findViewById(R.id.new_group);
        mSave = (TextView) findViewById(R.id.save);
        mDone = (TextView) findViewById(R.id.done_add);

        View.OnClickListener mSaveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mNewGroup.getText().toString();
                if (!name.isEmpty()) {
                    mBody = "{ \"name\": \"" + name + "\" }";
                    //Log.d("TEST = ", mBody);
                    try {
                        createGroup(mBody, mToken);
                    } catch (Exception error) {
                        Log.d(TAG, "EXCEPTION ERROR = " + error);
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

    public void createGroup(String body, String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.post("http://34.238.153.180:50000/createGroup").header(header).body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {

                Log.d("createGroup SUCCESS : ",response.toString());
                Toast.makeText(AddGroupActivity.this, "Group successfully created.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {

                Log.d("createGroup FAILURE : ",response.toString());
                Toast.makeText(AddGroupActivity.this, "This group can not be created.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
