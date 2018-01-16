package epitech.eip.slidare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import epitech.eip.slidare.request.Config;

public class AgendaActivity extends AppCompatActivity implements ContactFragment.OnItemSelectedListener, GroupFragment.OnItemSelectedListener {

    static final String TAG = "AgendaActivity";

    private Context mContext;
    private String mToken;

    private TextView mContact;
    private TextView mGroup;

    private ImageView mHomeView;
    private ImageView mProfilView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        Log.d(TAG, Config.ONCREATE);

        mContext = getApplicationContext();
        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");

        mContact = (TextView) findViewById(R.id.contact_switch);
        mGroup = (TextView) findViewById(R.id.group_switch);

        mHomeView = (ImageView) findViewById(R.id.ico_home);
        mProfilView = (ImageView) findViewById(R.id.ico_profil);

        View.OnClickListener mContactListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            getFragmentManager()
                .beginTransaction()
                .replace(R.id.contact_fragment, new ContactFragment())
                .addToBackStack(null)
                .commit();
            mContact.setBackgroundResource(R.drawable.contact_left);
            mContact.setTextColor(ContextCompat.getColor(mContext, R.color.blue_back));
            mGroup.setBackgroundResource(R.drawable.contact_right);
            mGroup.setTextColor(ContextCompat.getColor(mContext, R.color.whiteColor));
            }
        };

        View.OnClickListener mGroupListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            getFragmentManager()
                .beginTransaction()
                .replace(R.id.contact_fragment, new GroupFragment())
                .addToBackStack(null)
                .commit();
            mContact.setBackgroundResource(R.drawable.group_left);
            mContact.setTextColor(ContextCompat.getColor(mContext, R.color.whiteColor));
            mGroup.setBackgroundResource(R.drawable.group_right);
            mGroup.setTextColor(ContextCompat.getColor(mContext, R.color.blue_back));
            }
        };

        View.OnClickListener mHomeViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AgendaActivity.this, HomeActivity.class);
                intent.putExtra("token", mToken);
                startActivity(intent);
                finish();
            }
        };

        View.OnClickListener mProfilViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AgendaActivity.this, SettingsActivity.class);
                intent.putExtra("token", mToken);
                startActivity(intent);
                finish();
            }
        };

        mContact.setOnClickListener(mContactListener);
        mGroup.setOnClickListener(mGroupListener);
        mHomeView.setOnClickListener(mHomeViewListener);
        mProfilView.setOnClickListener(mProfilViewListener);
    }

    @Override
    public void onContactItemSelected(String link) {
    }

    @Override
    public void onGroupItemSelected(String link) {
    }
}