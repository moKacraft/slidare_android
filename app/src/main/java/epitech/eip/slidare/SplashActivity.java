package epitech.eip.slidare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by ferrei_e on 13/02/2017.
 */

public class SplashActivity extends AppCompatActivity {

    static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.d(TAG,"----------> onCreate");

        SharedPreferences settings = getSharedPreferences("USERDATA", 0);
        String userToken = settings.getString("userToken", null);
        String urlPicture = settings.getString("fbUrlImage", null);


        if (userToken != null) {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            intent.putExtra("token", userToken);
            intent.putExtra("fbUrlImage", urlPicture);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
