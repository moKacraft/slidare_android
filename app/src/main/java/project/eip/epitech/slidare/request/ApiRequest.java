package project.eip.epitech.slidare.request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import project.eip.epitech.slidare.HomeActivity;
import project.eip.epitech.slidare.MainActivity;
import project.eip.epitech.slidare.R;
import project.eip.epitech.slidare.SessionActivity;

public class ApiRequest extends AppCompatActivity {

    static final String TAG = "ApiRequest";
    private String mBody;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "----------> onCreate");
        mContext = getApplicationContext();
    }

    public void createUser(String body) throws Exception {

        mBody = body;
        Fuel.post("http://54.224.110.79:50000/createUser").body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("SUCCESS create User : ",response.toString());
                try {
                    loginUser(mBody);
                }
                catch (Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR = " + error);
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("FAILURE create User : ",response.toString());
            }
        });
    }

    /*public void loginUser(String body) throws Exception {

        Log.d(TAG, "----------> LOGIN USER");

        Fuel.post("http://54.152.163.253:50000/loginUser").body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("SUCCESS login User : ",response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));

                    Intent intent = new Intent(ApiRequest.this, SessionActivity.class);
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
    }*/

    public void loginUser(String body) throws Exception {

        Log.d(TAG, "----------> loginUser API");

        Fuel.post("http://54.224.110.79:50000/loginUser").body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("SUCCESS login User : ",response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));

                    Intent intent = new Intent(mContext, HomeActivity.class);
                    intent.putExtra("token", data.getString("token"));
                    //intent.putExtra("fbUrlImage", mUrlPicture);
                    startActivity(intent);
                    finish();
                }
                catch (Throwable tx) {
                    tx.printStackTrace();
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("FAILURE login User ",response.toString());

                Toast.makeText(ApiRequest.this, "Login or password incorrect.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
