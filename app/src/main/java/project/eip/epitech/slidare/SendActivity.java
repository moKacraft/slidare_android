package project.eip.epitech.slidare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SendActivity extends AppCompatActivity {

    static final String TAG = "SendActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        Log.d(TAG, "----------> onCreate");



    }
}
