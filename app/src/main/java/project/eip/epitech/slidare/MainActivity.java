package project.eip.epitech.slidare;

import project.eip.epitech.slidare.SocketIO.SocketIOService;
import project.eip.epitech.slidare.request.ApiRequest;
import project.eip.epitech.slidare.util.encryptUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";

    private Button mBtnSignin;
    private EditText mEmailEditText = null;
    private EditText mPasswordEditText = null;
    private TextView mSignupText;
    private CallbackManager callbackManager;
    private String mBody;
    private String mBodyUpdatePicture;
    private String mUrlPicture;
    private String mToken;
    private Context mContext;
    private LoginResult mLoginResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "----------> onCreate");

        mContext = getApplicationContext();
        mBody = new String();
        mEmailEditText = (EditText) findViewById(R.id.email_field);
        mPasswordEditText = (EditText) findViewById(R.id.password_field);
        mBtnSignin = (Button) findViewById(R.id.btnsignin);
        mSignupText = (TextView) findViewById(R.id.signup_text);
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "user_friends", "public_profile"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "----------> SUCCESS");

                mLoginResult = loginResult;
                //getUserDetailsFromFB(mLoginResult);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "----------> CONCEL");
                Toast.makeText(MainActivity.this, "Login attempt canceled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "----------> ERROR");
                Toast.makeText(MainActivity.this, "Login attempt failed.", Toast.LENGTH_SHORT).show();
            }
        });

        View.OnClickListener mSigninListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Email : ", "value = " + mEmailEditText.getText().toString());
                Log.d("Password : ", "value = " + mPasswordEditText.getText().toString());

                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                mBody = "{ \"email\": \"" + email + "\",\"password\": \"" + password + "\" }";

                try {
                    loginUser(mBody);
                }
                catch (Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR = " + error);
                }
            }
        };

        View.OnClickListener mSignupTextListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        };

        mBtnSignin.setOnClickListener(mSigninListener);
        mSignupText.setOnClickListener(mSignupTextListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public void loginUser(String body) throws Exception {

        Log.d(TAG, "----------> LOGIN USER");

        Fuel.post("http://34.227.142.101:50000/loginUser").body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("SUCCESS login User : ",response.toString());

                //setupConnections();
                //startService(new Intent(this, SocketIOService.class));

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));

                    mToken = data.getString("token");

                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("token", mToken);
                    intent.putExtra("fbUrlImage", mUrlPicture);
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
                Log.d("FAILURE request ",request.toString());
                Log.d("FAILURE fuelerror ",fuelError.toString());

                Toast.makeText(MainActivity.this, "Login or password incorrect.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUserDetailsFromFB(LoginResult loginResult) {

        GraphRequest req = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try{

                    String[] strings = object.getString("name").split(" ");
                    Log.d(TAG, "----------> FIRSTNAME " + strings[0]);
                    Log.d(TAG, "----------> LASTNAME " + strings[1]);

                    mBody = "{ \"first_name\": \"" + strings[0] + "\",\"last_name\": \"" + strings[1] + "\",\"email\": \"" + object.getString("email") + "\",\"fb_token\": \"" + mLoginResult.getAccessToken().getToken() + "\",\"fb_user_id\": \"" + mLoginResult.getAccessToken().getUserId() + "\" }";
                    Log.d(TAG, "----------> BODY " + mBody);

                    mUrlPicture = "https://graph.facebook.com/" + mLoginResult.getAccessToken().getUserId() + "/picture?type=large";
                    /*String email =  object.getString("email");
                    String birthday = object.getString("birthday");
                    String gender = object.getString("gender");
                    String name = object.getString("name");
                    String id = object.getString("id");
                    String photourl =object.getJSONObject("picture").getJSONObject("data").getString("url");*/
                    try {

                        Log.d(TAG, "----------> BODY BIS :" + mBody);
                        createUser(mBody);
                    }catch (Exception e){
                        Log.d("ERROR LOGIN USER : ",e.toString());
                    }

                }catch (JSONException e)
                {
                    Log.d("ERROR : ",e.toString());
                }

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large)");
        req.setParameters(parameters);
        req.executeAsync();
    }

    public void createUser(String body) throws Exception {

        Fuel.post("http://34.227.142.101:50000/createUser").body(body.getBytes()).responseString(new Handler<String>() {
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
                try {
                    loginUser(mBody);
                }
                catch (Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR = " + error);
                }
            }
        });
    }

    public void updateUserPicture(String body, String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.post("http://34.227.142.101:50000/updateUserPicture").header(header).body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("SUCCESS PICTURE : ",response.toString());

                Toast.makeText(MainActivity.this, "Picture successfully updated.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("FAIL PICTURE : ",response.toString());

                Toast.makeText(MainActivity.this, "Picture update failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Socket mSocket;

    public void setupConnections() {
        try {
            mSocket = IO.socket("http://34.227.142.101:8090");
            System.out.println("socket init");
            Log.d(TAG, "----------> SOCKET INIT");
            mSocket.on("server ready", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {

                    //dialogSend.setVisible(true);
                    System.out.println("Server ready");
                    Log.d(TAG, "----------> Server ready");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d(TAG, "----------> in server thread");
                                java.net.Socket sock;
                                sock = new java.net.Socket("34.227.142.101", (int)args[0]);
                                OutputStream is = sock.getOutputStream();
                                FileInputStream fis = new FileInputStream((String)args[1]);
                                BufferedInputStream bis = new BufferedInputStream(fis);
                                byte[] buffer = new byte[4096];
                                int ret;
                                while ((ret = fis.read(buffer)) > 0) {
                                    is.write(buffer, 0, ret);
                                }
                                fis.close();
                                bis.close();
                                is.close();
                                sock.close();
                            } catch (IOException ex) {
                                //Logger.getLogger(EventlogController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }).start();
                }
            }).on("soso@gmail.com", new Emitter.Listener() {
                @Override
                public void call(final Object... args)
                {
                    Log.d(TAG, "----------> in server username file receive");

                    Looper.prepare();

                    AlertDialog.Builder builder;
                    //Context context = getApplicationContext();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
//                    } else {
                        builder = new AlertDialog.Builder(getApplicationContext());
                    //}
                    builder.setTitle("Delete entry")
                            .setMessage("Are you sure you want to delete this entry?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    //Platform.runLater(new Runnable() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

//                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//                            alert.setTitle("Info");
//                            alert.setHeaderText("File Received");
//                            alert.setContentText("");
//                            Optional<ButtonType> result = alert.showAndWait();
//                            System.out.println(result);
//
//                            if (result.isPresent() && result.get() != ButtonType.OK) {
//                                return;
//                            }

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
//                                    JDialog dialog = new JDialog();
//                                    JLabel label = new JLabel("Please wait...");
//                                    dialog.setLocationRelativeTo(null);
//                                    dialog.setTitle("Please Wait...");
//                                    dialog.add(label);
//                                    dialog.setPreferredSize(new Dimension(300, 100));
//                                    dialog.setAlwaysOnTop(true);
//                                    dialog.pack();

                                    System.out.println("Receving File");
                                    Log.d(TAG, "----------> receiving file");
                                    String transferId = (String) args[2];
                                    try {
                                        FileOutputStream fos = new FileOutputStream((String) args[3]);
                                        java.net.Socket sock = new java.net.Socket("34.227.142.101", (int)args[1]);
                                        InputStream is = sock.getInputStream();
                                        byte[] buffer = new byte[4096];
                                        int ret;
                                        double sumCount = 0.0;

                                        //dialog.setVisible(true);

                                        while ((ret = is.read(buffer)) > 0) {
                                            fos.write(buffer, 0, ret);

                                            sumCount += ret;
                                            System.out.println("Percentace: " + (int)((sumCount / (int) args[9] * 100.0)) + "%");
                                            Log.d(TAG, "----------> Percentace: " + (int)((sumCount / (int) args[9] * 100.0)) + "%");
                                            //Thread.sleep(1000);
                                            //label.setText("Percentace: " + (int)((sumCount / (int) args[9] * 100.0)) + "%");
                                        }
                                        //dialog.setVisible(false);
                                        Log.d(TAG, "----------> Transfer Finished");
                                        System.out.println("Transfer Finished");
                                        fos.close();
                                        is.close();
                                        sock.close();
                                        mSocket.emit("transfer finished", transferId);

                                        encryptUtils _crypt = new  encryptUtils();
                                        byte[] salt =  Base64.decode((String)args[6],Base64.DEFAULT);
                                        byte[] iv = Base64.decode((String)args[7],Base64.DEFAULT);
                                        System.out.println(salt);
                                        System.out.println(iv);
                                        System.out.println(salt.length);
                                        System.out.println(iv.length);
                                        _crypt.decryptFile((String) args[3], (String) args[4], (String) args[5], salt, iv, (String) args[8]);
                                    } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex) {

                                    }
                                }
                            }).start();
                        }
                    });
                }
            }).on("receiving data", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("args[0] " + (String)args[0] + "args[1] " + args[1]);
                    Log.d(TAG, "----------> args[0] " + (String)args[0] + "args[1] " + args[1]);
                    //labelSend.setText((String) args[0]);
//                    if ((boolean) args[1] == true) {
//                        dialogSend.setVisible(false);
//                    }
                }
            });
            mSocket.connect();
            //Looper.loop();

        } catch (URISyntaxException ex) {
            System.out.println(ex.getMessage());
            System.out.println(ex.getReason());
        }

    }
}
