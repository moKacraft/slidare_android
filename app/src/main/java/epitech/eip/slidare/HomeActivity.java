package epitech.eip.slidare;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import epitech.eip.slidare.util.MyWebViewClient;
import epitech.eip.slidare.util.TcpClient;
import kotlin.text.Charsets;

/**
 * Created by ferrei_e on 13/02/2017.
 */

public class HomeActivity extends AppCompatActivity {

    static final String TAG = "HomeActivity";

    private String mToken;
    private String mUrlPicture;
    private ByteArrayOutputStream fileData = new ByteArrayOutputStream();

    private ImageView mHomeView;
    private ImageView mGroupView;
    private ImageView mProfilView;
    private WebView mMyWebview;
    private String mFileName;

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
                        UploadTask uploadTask = mStorage.putBytes(fileData.toByteArray());
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                System.out.println(exception);
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                System.out.println("SUCCESS");

                                mMyWebview.loadUrl(taskSnapshot.getDownloadUrl().toString());
                                DownloadManager.Request request = new DownloadManager.Request(taskSnapshot.getDownloadUrl());
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mFileName);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
                                request.allowScanningByMediaScanner();// if you want to be available from media players
                                DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                manager.enqueue(request);
                            }
                        });
                    } else {
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
            //response received from server
//            if (values[0].equals("FINISHED")) {
//                UploadTask uploadTask = mStorage.putBytes(fileData.toByteArray());
//                uploadTask.addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        System.out.println(exception);
//                    }
//                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
//                        System.out.println("SUCCESS");
//
//                        DownloadManager.Request request = new DownloadManager.Request(taskSnapshot.getDownloadUrl());
//                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mFileName);
//                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
//                        request.allowScanningByMediaScanner();// if you want to be available from media players
//                        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                        manager.enqueue(request);
//                    }
//                });
//            } else {
//                fileData.write(values[0].getBytes(), 0, values[0].length());
//                toto += values[0].length();
//            }
//            System.out.println(toto);
//            Log.d("test", "response " + values[0]);
            //process server response here....

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mSocket.on("soso@gmail.com", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println(args[0]);
                System.out.println(args[1]);
                System.out.println(args[2]);
                System.out.println(args[3]);
                System.out.println(args[4]);
                fileData = new ByteArrayOutputStream();
                TcpClient.SERVER_IP = "34.227.142.101";
                TcpClient.SERVER_PORT = Integer.parseInt(args[1].toString());
                mFileName = args[4].toString();
//                FirebaseStorage.getInstance();
                mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://slidare-c93d1.appspot.com/" + args[4].toString());

                new ConnectTask().execute("".getBytes());
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
    }
}
