/**
 * Netinfo.java
 * 
 * * @brief
 *    Implements telling the main app of network info. and connectivity.
 *
 * Created on 12/01/2014
 *
 * Copyright(c) 2013 Nagravision S.A, All Rights Reserved.
 * This software is the proprietary information of Nagravision S.A.
 */
package project.eip.epitech.slidare.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import project.eip.epitech.slidare.download.listener.NetworkListener;

/**
 * Implements telling the main app of network info. and connectivity.
 *
 */
public class NetInfo extends BroadcastReceiver{
  private NetworkListener mNetworkListener;
  private boolean mConnectflag = false;


  /**
   * Sets the {@link NetworkListener} for NetInfo.
   * 
   * @param xNetworkListener
   *          An object that wishes to be informed of data network events.
   */
  public void setNetworkListener(NetworkListener xNetworkListener) {
    mNetworkListener = xNetworkListener;
  }


  /**
   * Override method of BroadcastReceiver to deal with different network
   * condition
   * 
   * @param xContext
   *          {@link <a href="http://developer.android.com/reference/android/content/Context.html">Context</a>}
   *          used to access the systems services.
   * @param xIntent
   *          {@link <a href="http://developer.android.com/reference/android/content/Intent.html">Intent</a>}
   *          used to access the apps assets.
   */
  @Override
  public void onReceive(Context xContext, Intent xIntent) {
    NetworkInfo activeNetInfo = getActiveNetworkInfo(xContext);

    if ((activeNetInfo == null) || (!activeNetInfo.isAvailable())
        || (!activeNetInfo.isConnected())) {
      if (mConnectflag) {
        mNetworkListener.unconnect();
        mConnectflag = false;
      }
    } else if ((activeNetInfo != null) && (activeNetInfo.isAvailable())
        && (activeNetInfo.isConnected())) {
      if (!mConnectflag) {
        mNetworkListener.recoverConnection();
        mConnectflag = true;
      }
    }
  }


  /**
   * Get the details about the currently system active default data network.
   * 
   * @param xContext
   *          the application context
   * @return a
   *         {@link <a href="http://developer.android.com/reference/android/net/NetworkInfo.html">NetworkInfo</a>}
   *         object for the current default network or null if no network
   *         default network is currently active
   */
  private NetworkInfo getActiveNetworkInfo(Context xContext) {
    if (xContext == null) {
      return null;
    }
    ConnectivityManager manager = (ConnectivityManager) xContext
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    return manager.getActiveNetworkInfo();
  }

}
