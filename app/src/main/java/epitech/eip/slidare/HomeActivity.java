package epitech.eip.slidare;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import epitech.eip.slidare.request.Config;
import epitech.eip.slidare.request.Share;
import epitech.eip.slidare.util.TcpClient;
import epitech.eip.slidare.util.encryptUtils;

/**
 * Created by ferrei_e on 13/02/2017.
 */

public class HomeActivity extends AppCompatActivity {

    static final String TAG = "HomeActivity";

    private Context mContext;

    private String mToken;
    private String mUrlPicture;
    private String mUserEmail;
    private ByteArrayOutputStream fileData = new ByteArrayOutputStream();
    private FileOutputStream fos;

    private HomeListAdapter mAdapter;
    private ListView mListView;
    private List<String> mList;

    private ImageView mAgendaView;
    private ImageView mProfilView;
    private ImageView mShare;
    private String sender_id;
    private String mFileName;
    private String encFilePath;
    private String filePath;
    private String sha1;
    private String key;
    private String mEmail;
    private byte[] salt;
    private byte[] iv;
    private String transferId;

    static public Socket mSocket;
    {
        try {
            mSocket = IO.socket(Config.URL_SOCKET);
        } catch (URISyntaxException e) {}
    }
    TcpClient mTcpClient;
    StorageReference mStorage;

    private class ConnectTask extends AsyncTask<byte[], String, TcpClient> {
        private int toto = 0;
        @Override
        protected TcpClient doInBackground(byte[]... message) {

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
                                String body = "{ \"file_url\": \"" + taskSnapshot.getDownloadUrl().toString() + "\", \"sender\": \"" + sender_id + "\"  }";
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mList.add(sender_id + ";" + taskSnapshot.getDownloadUrl().toString());
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                                Handler<String> handler = new Handler<String>() {
                                    @Override
                                    public void success(@NotNull Request request, @NotNull Response response, String s) {
                                        Log.d("addFile " + Config.ONSUCCESS,response.toString());
                                    }

                                    @Override
                                    public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                                        Log.d("addFile " + Config.FAILURE,response.toString());
                                    }
                                };
                                Share.addFile(body, mToken, handler);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

//                            DownloadManager.Request request = new DownloadManager.Request(taskSnapshot.getDownloadUrl());
//                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mFileName);
//                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
//                            request.allowScanningByMediaScanner();// if you want to be available from media players
//                            DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                            manager.enqueue(request);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Log.d(TAG, Config.ONCREATE);

        mContext = getApplicationContext();
        Intent intent = getIntent();
        mToken = intent.getStringExtra("token");
        mUrlPicture = intent.getStringExtra("fbUrl");
        //mUserEmail = intent.getStringExtra("email");
        SharedPreferences settings = getSharedPreferences("USERDATA", 0);
        mEmail = settings.getString("email", null);
        Log.d(TAG, mEmail);
        mAdapter = new HomeListAdapter(mList, mContext, mToken);

        mAgendaView = (ImageView) findViewById(R.id.ico_agenda);
        mProfilView = (ImageView) findViewById(R.id.ico_profil);
        mShare = (ImageView) findViewById(R.id.ico_send);
        mListView = (ListView) findViewById(R.id.history_list);

        if (mSocket.connected() == false) {

            mSocket.on(mEmail, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    transferId = (String) args[2];
                    sha1 = (String) args[5];
                    key = (String) args[8];
                    sender_id = (String) args[10];
                    sendNotification(getApplicationContext(),sender_id + " sent you a file");
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
                    TcpClient.SERVER_IP = Config.IP;
                    TcpClient.SERVER_PORT = Integer.parseInt(args[1].toString());
                    mFileName = args[4].toString();
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
                                Log.d("icicicicicicicici", "camarche pas pas pas pas pas pas");
                                java.net.Socket sock;
                                sock = new java.net.Socket(Config.IP, (int) args[0]);
                                OutputStream is = sock.getOutputStream();

                                File arg = new File(getCacheDir(), (String) args[1]);

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
        }



        /*if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},4242);
            }
        }*/

        View.OnClickListener mAgendaViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, AgendaActivity.class);
            intent.putExtra("token", mToken);
            startActivity(intent);
            finish();
            }
        };

        View.OnClickListener mProfilViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            intent.putExtra("token", mToken);
            intent.putExtra("fbUrl", mUrlPicture);
            startActivity(intent);
            finish();
            }
        };

        View.OnClickListener mSendListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, ShareActivity.class);
            intent.putExtra("token", mToken);
            startActivity(intent);
            }
        };

        mAgendaView.setOnClickListener(mAgendaViewListener);
        mProfilView.setOnClickListener(mProfilViewListener);
        mShare.setOnClickListener(mSendListener);
        mListView.setAdapter(mAdapter);

        try {
            Handler<String> handler = new Handler<String>() {
                @Override
                public void success(@NotNull Request request, @NotNull Response response, String s) {
                    Log.d("getFiles " + Config.ONSUCCESS,response.toString());
                    try {
                        JSONObject data = new JSONObject(new String(response.getData()));
                        JSONArray files = data.getJSONArray("file_urls");
                        JSONArray senders = data.getJSONArray("senders");
//                        String fileUrls = data.getString("file_urls").toString().replace("[\"", "").replace("\"]", "").replaceAll("\"","").replaceAll("\\\\", "");
//                        String senders = data.getString("senders").toString().replace("[\"", "").replace("\"]", "").replaceAll("\"","");
//                        String[] tab = fileUrls.split(",");
//                        String[] str = senders.split(",");
                        System.out.println(data);

                        mList = new ArrayList<String>();
                        for (int i = 0; i < files.length(); i++){
                            mList.add((i >= senders.length() ? "Sender Unknown" : senders.getString(i)) + ";" + files.getString(i));
                        }
                        mAdapter = new HomeListAdapter(mList, mContext, mToken);
                        mListView.setAdapter(mAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                    Log.d("getFiles " + Config.FAILURE,response.toString());
                }
            };
            Share.getFiles(mToken, handler);
        }
        catch (Exception e) {
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
}