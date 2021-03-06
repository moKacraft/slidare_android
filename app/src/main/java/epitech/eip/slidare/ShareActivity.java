package epitech.eip.slidare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mvc.imagepicker.ImagePicker;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import epitech.eip.slidare.request.Config;
import epitech.eip.slidare.util.encryptUtils;
import id.zelory.compressor.Compressor;

public class ShareActivity extends AppCompatActivity implements ToContactFragment.OnItemSelectedListener, ToGroupFragment.OnItemSelectedListener {

    static final String TAG = "ShareActivity";

    private Context mContext;

    public static JSONArray mEmails = new JSONArray();

    private TextView mToContact;
    private TextView mToGroup;
    private TextView mDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Log.d(TAG, Config.ONCREATE);

        mContext = getApplicationContext();
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
    public void onResume() {
        super.onResume();
        mEmails = new JSONArray();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            InputStream is = ImagePicker.getInputStreamFromResult(mContext, requestCode, resultCode, data);
            if (is != null) {
                File file = new File(getCacheDir(), UUID.randomUUID().toString() + ".png");
                OutputStream out = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int len;
                while((len=is.read(buf))>0){
                    out.write(buf,0,len);
                }

                file = new Compressor(this).compressToFile(file);
                File encrypted = new File(getCacheDir(), "encrypted");
                FileOutputStream outFile = new FileOutputStream(encrypted);
                encryptUtils _crypt = new  encryptUtils();
                String key =  encryptUtils.SHA256("my secret key", 32);
                _crypt.encryptFile(file.toString(), "encrypted", outFile, key);

                JSONArray users = new JSONArray();
                users.put(mEmails);

                SharedPreferences settings = getSharedPreferences("USERDATA", 0);
                String myEmail = settings.getString("email", null);
                HomeActivity.mSocket.emit("request file transfer", file.getName(),
                    encrypted, mEmails, _crypt.get_fileEncryptedName(), file.getName(),
                    _crypt.get_fileSHA1(), Base64.encode(_crypt.get_fileSalt(), Base64.DEFAULT),
                    Base64.encode(_crypt.get_fileIV(), Base64.DEFAULT), _crypt.get_fileKey(), file.length(), myEmail);

                System.out.println(_crypt.get_fileSalt());
                System.out.println( Base64.encode(_crypt.get_fileSalt(), Base64.DEFAULT));

                out.flush();
                out.close();
                outFile.flush();
                outFile.close();
                is.close();
            }
            else {
                //failed to load file
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onContactItemSelected(String link) {
    }

    @Override
    public void onGroupItemSelected(String link) {
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
