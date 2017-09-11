package epitech.eip.slidare;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mvc.imagepicker.ImagePicker;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import epitech.eip.slidare.util.MyWebViewClient;
import epitech.eip.slidare.util.TcpClient;
import epitech.eip.slidare.util.encryptUtils;
import kotlin.text.Charsets;

/**
 * Created by ferrei_e on 13/02/2017.
 */

public class HomeActivity extends AppCompatActivity {

    static final String TAG = "HomeActivity";

    private String mToken;
    private String mUrlPicture;
    private ByteArrayOutputStream fileData = new ByteArrayOutputStream();
    private FileOutputStream fos;

    private ImageView mHomeView;
    private ImageView mGroupView;
    private ImageView mProfilView;
    private WebView mMyWebview;
    private String mFileName;
    private String encFilePath;
    private String filePath;
    private String sha1;
    private String key;
    private byte[] salt;
    private byte[] iv;
    private String transferId;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://34.227.142.101:8090");
        } catch (URISyntaxException e) {}
    }
    TcpClient mTcpClient;
    StorageReference mStorage;

    private class ConnectTask extends AsyncTask<byte[], String, TcpClient> {
        private int toto = 0;
        @Override
        protected TcpClient doInBackground(byte[]... message) {

            //we create a TCPClient object
            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(byte[] message, int messageSize) {
                    if (messageSize <= 0) {
                        encryptUtils _crypt = null;
                        try {
                            _crypt = new encryptUtils();
                            _crypt.decryptFile(encFilePath, filePath, sha1, salt, iv, key);

                            InputStream stream = new FileInputStream(new File(filePath));
                            UploadTask uploadTask = mStorage.putStream(stream);
//                            UploadTask uploadTask = mStorage.putBytes(fileData.toByteArray());
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    System.out.println(exception);
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                    System.out.println("SUCCESS");
                                    sendNotification(getApplicationContext(), "File transfer finished");
                                    mSocket.emit("transfer finished", transferId);
                                    try {
                                        addFile(taskSnapshot.getDownloadUrl().toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    mMyWebview.loadUrl(taskSnapshot.getDownloadUrl().toString());
                                    DownloadManager.Request request = new DownloadManager.Request(taskSnapshot.getDownloadUrl());
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mFileName);
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
                                    request.allowScanningByMediaScanner();// if you want to be available from media players
                                    DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                    manager.enqueue(request);
                                }
                            });
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
                        } catch (InvalidAlgorithmParameterException e) {
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            fos.write(message, 0, messageSize);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        fileData.write(message, 0, messageSize);
                        toto += messageSize;
                    }
                }
            });
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            InputStream is = ImagePicker.getInputStreamFromResult(this, requestCode, resultCode, data);
            if (is != null) {
                File file = new File(getCacheDir(), "toEncryt.png");
                OutputStream out = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int len;
                while((len=is.read(buf))>0){
                    out.write(buf,0,len);
                }

                File encrypted = new File(getCacheDir(), "encrypted");
                FileOutputStream outFile = new FileOutputStream(encrypted);

                encryptUtils _crypt = new  encryptUtils();
                String key =  encryptUtils.SHA256("my secret key", 32);

                _crypt.encryptFile(file.toString(), "encrypted", outFile, key);

//                String users[] = new String[1];
//                users[0] = "soso@gmail.com";

//                String[] users = {"soso@gmail.com"};

                JSONArray users = new JSONArray();
                users.put("soso@gmail.com");

                mSocket.emit("request file transfer", file.getName(),
                        encrypted, users, _crypt.get_fileEncryptedName(), file.getName(),
                        _crypt.get_fileSHA1(), Base64.encode(_crypt.get_fileSalt(), Base64.DEFAULT),
                        Base64.encode(_crypt.get_fileIV(), Base64.DEFAULT), _crypt.get_fileKey(), file.length());

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

    public void onPickImage(View view) {
        // Click on image button
        ImagePicker.pickImage(this, "Select your image:");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Button button = (Button) findViewById(R.id.button_id);
        ImagePicker.setMinQuality(600, 600);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onPickImage(v);
            }
        });


        mSocket.on("soso@gmail.com", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                sendNotification(getApplicationContext(), "xxx Wants to send you a file");
                transferId = (String) args[2];
                sha1 = (String) args[5];
                key = (String) args[8];
                salt = Base64.decode((String) args[6], Base64.DEFAULT);
                iv = Base64.decode((String) args[7], Base64.DEFAULT);
                fileData = new ByteArrayOutputStream();
                try {
                    File yourEncFile = new File(getApplicationContext().getFilesDir(), (String)args[3]);
                    File yourFile = new File(getApplicationContext().getFilesDir(), (String)args[4]);
                    encFilePath = yourEncFile.getPath();
                    filePath = yourFile.getPath();
                    yourFile.createNewFile();
                    yourEncFile.createNewFile();
                    fos = new FileOutputStream(encFilePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                TcpClient.SERVER_IP = "34.227.142.101";
                TcpClient.SERVER_PORT = Integer.parseInt(args[1].toString());
                mFileName = args[4].toString();
//                FirebaseStorage.getInstance();
                mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://slidare-c93d1.appspot.com/" + args[4].toString());

                new ConnectTask().execute("".getBytes());
            }
        });

        mSocket.on("server ready", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            java.net.Socket sock;
                            sock = new java.net.Socket("34.227.142.101", (int)args[0]);
                            OutputStream is = sock.getOutputStream();

                            File arg = new File(getCacheDir(), (String)args[1]);

                            FileInputStream fis = new FileInputStream(arg);
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
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        mSocket.connect();
        Log.d(TAG, "----------> onCreate");

        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");
        mUrlPicture = intent.getStringExtra("fbUrl");

        mHomeView = (ImageView) findViewById(R.id.ico_home);
        mGroupView = (ImageView) findViewById(R.id.ico_group);
        mProfilView = (ImageView) findViewById(R.id.ico_profil);
        mMyWebview = (WebView) findViewById(R.id.my_webview);

        mMyWebview.setWebViewClient(new MyWebViewClient());
        mMyWebview.getSettings().setJavaScriptEnabled(true);

        mMyWebview.getSettings().setAllowContentAccess(true);
        mMyWebview.getSettings().setAllowFileAccessFromFileURLs(true);
        mMyWebview.getSettings().setAllowFileAccess(true);
        mMyWebview.getSettings().setLoadWithOverviewMode(true);
        mMyWebview.getSettings().setUseWideViewPort(true);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        4242);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        //
//        mMyWebview.setDownloadListener(new DownloadListener() {
//            @Override
//            public void onDownloadStart(String url, String userAgent,
//                                        String contentDisposition, String mimetype,
//                                        long contentLength) {
//
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
//            }
//        });
//        mMyWebview.set
//        Map<String, String> extraHeaders = new HashMap<>();
//        extraHeaders.put("Content-Disposition", "inline;filename=\"5.jpg\"");
//        mMyWebview.loadUrl("https://firebasestorage.googleapis.com/v0/b/slidare-c93d1.appspot.com/o/5.jpg?alt=media&token=49ab4571-6565-47d2-89fd-5fecbf57c86d", extraHeaders);

//        mMyWebview.loadUrl("https://firebasestorage.googleapis.com/v0/b/slidare-c93d1.appspot.com/o/5.jpg?alt=media&token=49ab4571-6565-47d2-89fd-5fecbf57c86d");
//        mMyWebview.setDownloadListener(new DownloadListener() {
//            @Override
//            public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
//
//            }
//        });

        View.OnClickListener mHomeViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "----------> HOME");
            }
        };

        View.OnClickListener mGroupViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "----------> CONTACT");

                Intent intent = new Intent(HomeActivity.this, ContactActivity.class);
                intent.putExtra("token", mToken);
                startActivity(intent);
                finish();
            }
        };

        View.OnClickListener mProfilViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "----------> PROFIL");

                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                intent.putExtra("token", mToken);
                intent.putExtra("fbUrl", mUrlPicture);
                startActivity(intent);
                finish();
            }
        };

        mHomeView.setOnClickListener(mHomeViewListener);
        mGroupView.setOnClickListener(mGroupViewListener);
        mProfilView.setOnClickListener(mProfilViewListener);
        try {
            getFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void sendNotification(Context ctx, String content) {
        Intent intent = new Intent(ctx, HomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.slidarelogo)
                .setContentTitle("File Transfer")
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo(content);


        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());
    }

    public void addFile(String file_url) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+ mToken);

        String body = "{ \"file_url\": \"" + file_url + "\" }";


        Fuel.post("http://34.227.142.101:50000/addFileToList").header(header).body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("addContact SUCCESS : ",response.toString());
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("addContact FAILURE : ",response.toString());
            }
        });
    }

    public void getFiles() throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+ mToken);

        Fuel.get("http://34.227.142.101:50000/getUserFiles").header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("getFiles SUCCESS : ",response.toString());
                try {
                    JSONObject data = new JSONObject(new String(response.getData()));
                    JSONArray fileUrls = data.getJSONArray("file_urls");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("getFiles FAILURE : ",response.toString());
            }
        });
    }
}
