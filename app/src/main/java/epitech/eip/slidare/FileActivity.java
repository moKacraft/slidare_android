package epitech.eip.slidare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class FileActivity extends AppCompatActivity {

    static final String TAG = "FileActivity";

    private Context mContext;

    private String mToken;
    private String mImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        mContext = getApplicationContext();

        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");
        mImageUrl = intent.getStringExtra("image_url");

        ImageView imageView = (ImageView) findViewById(R.id.file_image);
        Picasso.with(mContext).load(mImageUrl).into(imageView);

        TextView textView = (TextView) findViewById(R.id.back);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(mContext, HomeActivity.class);
            intent.putExtra("token", mToken);
            startActivity(intent);
            finish();
            }
        });
    }
}
