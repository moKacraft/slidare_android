package epitech.eip.slidare;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by 42350 on 25/09/2017.
 */

public class ShareActivity extends AppCompatActivity implements ToContactFragment.OnItemSelectedListener, ToGroupFragment.OnItemSelectedListener {

    static final String TAG = "ShareActivity";

    private Context mContext;

    private String mToken;

    private TextView mToContact;
    private TextView mToGroup;
    private TextView mDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Log.d(TAG, "----------> onCreate");

        mContext = getApplicationContext();

        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");

        mToContact = (TextView) findViewById(R.id.tocontact);
        mToGroup = (TextView) findViewById(R.id.togroup);
        mDone = (TextView) findViewById(R.id.done_share);

        View.OnClickListener mToContactListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.tocontact_fragment, new ToContactFragment())
                        .addToBackStack(null)
                        .commit();

                mToContact.setBackgroundResource(R.drawable.contact_left);
                mToContact.setTextColor(ContextCompat.getColor(mContext, R.color.blue_back));
                mToGroup.setBackgroundResource(R.drawable.contact_right);
                mToGroup.setTextColor(ContextCompat.getColor(mContext, R.color.whiteColor));
            }
        };

        View.OnClickListener mToGroupListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.tocontact_fragment, new ToGroupFragment())
                        .addToBackStack(null)
                        .commit();
                mToContact.setBackgroundResource(R.drawable.group_left);
                mToContact.setTextColor(ContextCompat.getColor(mContext, R.color.whiteColor));
                mToGroup.setBackgroundResource(R.drawable.group_right);
                mToGroup.setTextColor(ContextCompat.getColor(mContext, R.color.blue_back));
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

        Log.d(TAG, "--------> onContactItemSelected");

        /*ToContactFragment fragment = (ToContactFragment) getFragmentManager().findFragmentById(R.id.tocontact_fragment);

        if (fragment != null && fragment.isInLayout()) {
            //fragment.setText(link);
        }*/
    }

    @Override
    public void onGroupItemSelected(String link) {

        Log.d(TAG, "--------> onGroupItemSelected");

        /*ToGroupFragment fragment = (ToGroupFragment) getFragmentManager().findFragmentById(R.id.tocontact_fragment);

        if (fragment != null && fragment.isInLayout()) {
            //fragment.setText(link);
        }*/
    }
}
