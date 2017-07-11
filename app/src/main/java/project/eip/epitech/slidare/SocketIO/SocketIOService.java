package project.eip.epitech.slidare.SocketIO;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import project.eip.epitech.slidare.util.encryptUtils;

/**
 * Created by Madeline on 11/07/2017.
 */

public class SocketIOService extends Service{
    static final String TAG = "SocketIOService";

    private final LocalBinder mBinder = new LocalBinder();
    private Socket mSocket;

    public class LocalBinder extends Binder {
        public SocketIOService getService(){
            return SocketIOService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupConnections();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

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
