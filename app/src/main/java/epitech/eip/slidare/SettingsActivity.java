package epitech.eip.slidare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.mvc.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ferrei_e on 13/02/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    static final String TAG = "SettingsActivity";

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST = 1888;

    private Context mContext;

    private String mToken;
    private String mUrlPicture;
    private String mUsername;
    private String mEmail;
    private String mPassword;
    private String mBodyUpdatePicture;
    private String mBodyUpdateUsername;
    private String mBodyUpdateEmail;
    private String mBodyUpdatePassword;

    private TextView mLibrary;
    private TextView mUserEmail;
    private TextView mName;
    private TextView mPseudo;
    private TextView mEmailAddress;
    private TextView mPasswordLabel;
    private TextView mPasswordNewLabel;
    private TextView mLogout;

    private ImageView mHomeView;
    private ImageView mGroupView;
    private ImageView mProfilView;
    private ImageView mUserImage;
    private ImageButton mPhotoButton;

    private EditText mNewUsername;
    private EditText mNewEmail;
    private EditText mNewPassword;
    private EditText mCurrentPassword;

    private Button mSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Log.d(TAG, "----------> onCreate");

        mContext = getApplicationContext();
        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");
        mUrlPicture = intent.getStringExtra("fbUrl");

        mName = (TextView) findViewById(R.id.profil_name);
        mUserEmail = (TextView) findViewById(R.id.profil_email);
        mHomeView = (ImageView) findViewById(R.id.ico_home);
        mGroupView = (ImageView) findViewById(R.id.ico_group);
        mProfilView = (ImageView) findViewById(R.id.ico_profil);
        mLibrary = (TextView) findViewById(R.id.library);
        mPhotoButton = (ImageButton) findViewById(R.id.camera);
        Picasso.with(getApplicationContext()).load(R.drawable.camera).fit().into(mPhotoButton);
        mUserImage = (ImageView) findViewById(R.id.user_image);
        //Picasso.with(getApplicationContext()).load("https://pbs.twimg.com/profile_images/2234415184/Fane_Din_Babane.jpg").fit().into(mUserImage);
        mSave = (Button) findViewById(R.id.save);
        mLogout = (TextView) findViewById(R.id.logout);

        View.OnClickListener mPhotoButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "----------> PHOTO BUTTON");

                /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }*/

                try{
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } catch(Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR : " + error);
                }
            }
        };

        /*View.OnClickListener mModifyListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Log.d(TAG, "----------> MODIFY");

                //mUrlPicture = "http://variancemagazine.com/images/chet-faker.png";
                mUrlPicture = "http://img2-ak.lst.fm/i/u/300x300/7026dd25d5b04f36cf771f6e360acd62.png";
                mBodyUpdatePicture = "{ \"profile_picture_url\": \"" + mUrlPicture + "\" }";

                //Log.d("TEST = ", mBodyUpdatePicture);

                try {
                    updateUserPicture(mBodyUpdatePicture, mToken);
                } catch (Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR = " + error);
                }
            }
        };*/

        View.OnClickListener mHomeViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "----------> HOME");

                Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                intent.putExtra("token", mToken);
                startActivity(intent);
                finish();
            }
        };

        View.OnClickListener mGroupViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "----------> CONTACT");

                Intent intent = new Intent(SettingsActivity.this, ContactActivity.class);
                intent.putExtra("token", mToken);
                startActivity(intent);
                finish();
            }
        };

        View.OnClickListener mProfilViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "----------> PROFIL");

            }
        };

        View.OnClickListener mSaveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Log.d(TAG, "----------> SAVE");

            mNewUsername = (EditText) findViewById(R.id.username_setting);
            //mNewEmail = (EditText) findViewById(R.id.email_setting);
            mNewPassword = (EditText) findViewById(R.id.password_new);
            mCurrentPassword = (EditText) findViewById(R.id.password_current);

            //Log.d("Username : ", "value = " + mNewUsername.getText().toString());
            //Log.d("Real Username :", "value = " + mUsername);
            //Log.d("Email : ", "value = " + mNewEmail.getText().toString());
            //Log.d("Real Email :", "value = " + mEmail);
            //Log.d("CurrentPassword : ", "value = " + mCurrentPassword.getText().toString());
            //Log.d("NewPassword : ", "value = " + mNewPassword.getText().toString());
            //Log.d("Real Password :", "value = " + mPassword);

            String username = mNewUsername.getText().toString();
            //String email = mNewEmail.getText().toString();
            String currentPassword = mCurrentPassword.getText().toString();
            String newPassword = mNewPassword.getText().toString();

            if (mUsername.compareTo(username) != 0){

                mBodyUpdateUsername = "{ \"username\": \"" + username + "\" }";
                //Log.d("TEST = ", mBodyUpdateUsername);
                try {
                    updateUserName(mBodyUpdateUsername, mToken);
                } catch (Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR : " + error);
                }
            }

            /*if (mEmail.compareTo(email) != 0) {

                mBodyUpdateEmail = "{ \"email\": \"" + email + "\" }";
                //Log.d("TEST = ", mBodyUpdateEmail);
                try {
                    updateUserEmail(mBodyUpdateEmail, mToken);
                } catch (Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR : " + error);
                }
            }*/

            if (mPassword.compareTo(currentPassword) == 0) {

                if (newPassword.compareTo("") != 0 && newPassword.compareTo(currentPassword) != 0) {

                    mBodyUpdatePassword = "{ \"old_password\": \"" + currentPassword + "\",\"new_password\": \"" + newPassword + "\" }";
                    //Log.d("TEST = ", mBodyUpdatePassword);
                    try {
                        updateUserPassword(mBodyUpdatePassword, mToken);
                    } catch (Exception error) {
                        Log.d(TAG, "EXCEPTION ERROR : " + error);
                    }
                }
                else {
                    //Toast.makeText(SettingsActivity.this, "Passwords are identicals.", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(SettingsActivity.this, "Current Password incorrect.", Toast.LENGTH_SHORT).show();
            }
            }
        };

        View.OnClickListener mLogoutListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences settings = getSharedPreferences("USERDATA", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.remove("userToken");
                editor.remove("userId");
                editor.remove("fbUrlImage");
                editor.commit();

                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        View.OnClickListener mSearchLibraryListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.pickImage(SettingsActivity.this, "Select your image:");
            }
        };

        mHomeView.setOnClickListener(mHomeViewListener);
        mGroupView.setOnClickListener(mGroupViewListener);
        mProfilView.setOnClickListener(mProfilViewListener);
        mPhotoButton.setOnClickListener(mPhotoButtonListener);
        mLibrary.setOnClickListener(mSearchLibraryListener);
        //mModify.setOnClickListener(mModifyListener);
        mSave.setOnClickListener(mSaveListener);
        mLogout.setOnClickListener(mLogoutListener);

        try {
            fetchUser(mToken);
        }
        catch (Exception error){
            Log.d(TAG, "EXCEPTION ERROR : " + error);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mUserImage.setImageBitmap(photo);
        }
        else {
            try {
                String url = ImagePicker.getImagePathFromResult(this, requestCode, resultCode, data);

                Log.d(TAG, "TEST = " + url);

                Picasso.with(getApplicationContext()).load(new File(url)).fit().into(mUserImage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void fetchUser(String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://34.238.153.180:50000/fetchUser").header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("fetchUser SUCCESS : ",response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));

                    mUsername = data.getString("username");
                    mName.setText(data.getString("username"));
                    //mEmail = data.getString("email");
                    mUserEmail.setText(data.getString("email"));
                    try {
                        mPassword = data.getString("password");
                        mUrlPicture = data.getString("profile_picture_url");
                        Picasso.with(getApplicationContext()).load(mUrlPicture).fit().into(mUserImage);
                    }
                    catch (Exception error){
                        Log.d("NO PICTURE OR PWD : ", error.toString());
                    }
                    mToken = data.getString("token");

                    mPseudo = (TextView) findViewById(R.id.username_setting);
                    //mEmailAddress = (TextView) findViewById(R.id.email_setting);
                    mPasswordLabel = (TextView) findViewById(R.id.password_current);
                    mPasswordNewLabel = (TextView) findViewById(R.id.password_new);
                    mPseudo.setText(mUsername);
                    //mEmailAddress.setText(mEmail);
                    if (mPassword == null) {
                        mPasswordLabel.setVisibility(View.INVISIBLE);
                        mPasswordNewLabel.setVisibility(View.INVISIBLE);
                    } else
                        mPasswordLabel.setText(mPassword);
                    mPasswordNewLabel.setText("");
                } catch (Throwable tx) {
                    tx.printStackTrace();
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("fetchUser FAILURE : ",response.toString());
            }
        });
    }

    public void updateUserPicture(String body, String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.post("http://34.238.153.180:50000/updateUserPicture").header(header).body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {

                Log.d("upUserPic SUCCESS : ",response.toString());
                Toast.makeText(SettingsActivity.this, "Picture successfully updated.", Toast.LENGTH_SHORT).show();

                try {
                    fetchUser(mToken);
                }
                catch (Exception error){
                    Log.d(TAG, "EXCEPTION ERROR : " + error);
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {

                Log.d("upUserPic FAILURE : ",response.toString());
                Toast.makeText(SettingsActivity.this, "Picture update failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUserName(String body, String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.post("http://34.238.153.180:50000/updateUserName").header(header).body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {

                Log.d("upUserName SUCCESS : ",response.toString());
                Toast.makeText(SettingsActivity.this, "Username successfully updated.", Toast.LENGTH_SHORT).show();

                try {
                    fetchUser(mToken);
                }
                catch (Exception error){
                    Log.d(TAG, "EXCEPTION ERROR : " + error);
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {

                Log.d("upUserName FAILURE : ",response.toString());
                Toast.makeText(SettingsActivity.this, "Username updated failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUserEmail(String body, String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.post("http://34.238.153.180:50000/updateUserEmail").header(header).body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {

                Log.d("upUserEmail SUCCESS : ",response.toString());
                Toast.makeText(SettingsActivity.this, "Email successfully updated.", Toast.LENGTH_SHORT).show();
                try {
                    fetchUser(mToken);
                }
                catch (Exception error){
                    Log.d(TAG, "EXCEPTION ERROR : " + error);
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {

                Log.d("upUserEmail FAILURE : ",response.toString());
                Toast.makeText(SettingsActivity.this, "This email already exist.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUserPassword(String body, String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.post("http://34.238.153.180:50000/updateUserPassword").header(header).body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {

                Log.d("upUserPass SUCCESS : ",response.toString());
                Toast.makeText(SettingsActivity.this, "Password successfully updated.", Toast.LENGTH_SHORT).show();
                try {
                    fetchUser(mToken);
                }
                catch (Exception error){
                    Log.d(TAG, "EXCEPTION ERROR : " + error);
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {

                Log.d("upUserPass FAILURE : ",response.toString());
                Toast.makeText(SettingsActivity.this, "Wrong password provided.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
