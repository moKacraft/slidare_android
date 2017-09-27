package epitech.eip.slidare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by 42350 on 25/09/2017.
 */

public class ShareActivity extends AppCompatActivity implements ToContactFragment.OnItemSelectedListener {

    static final String TAG = "ShareActivity";

    private String mToken;

    private TextView mToContact;
    private TextView mToGroup;
    private TextView mDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Log.d(TAG, "----------> onCreate");

        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");

        Log.d(TAG, "TOKEN initial = " + mToken);

        mToContact = (TextView) findViewById(R.id.tocontact);
        mToGroup = (TextView) findViewById(R.id.togroup);
        mDone = (TextView) findViewById(R.id.done_share);

        View.OnClickListener mToContactListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(ShareActivity.this, ToAContactActivity.class);
            intent.putExtra("token", mToken);
            startActivity(intent);
            }
        };

        View.OnClickListener mToGroupListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(ShareActivity.this, ToAGroupActivity.class);
            intent.putExtra("token", mToken);
            startActivity(intent);
            }
        };

        View.OnClickListener mDoneListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };

        mToContact.setOnClickListener(mToContactListener);
        mToGroup.setOnClickListener(mToGroupListener);
        mDone.setOnClickListener(mDoneListener);
    }

    @Override
    public void onContactItemSelected(String link) {

        ToContactFragment fragment = (ToContactFragment) getFragmentManager().findFragmentById(R.id.tocontact_fragment);

        if (fragment != null && fragment.isInLayout()) {
            //fragment.setText(link);
        }
    }
}
