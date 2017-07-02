/**
 * ServerResoucesHandler.java
 *
 * @brief
 *    An interface that allows detection of connect and disconnect of the network.
 *
 * Created on 12/01/2014
 *
 * Copyright(c) 2014 Nagravision S.A, All Rights Reserved.
 * This software is the proprietary information of Nagravision S.A.
 */
package project.eip.epitech.slidare.download.listener;


/**
 * An interface that allows detection of connect and disconnect of the network.
 *
 */
public interface NetworkListener {

  /**
   * Called when the network has lost connection.
   */
  public void unconnect();


  /**
   * Called when the network has re-established a connection.
   */
  public void recoverConnection();
}
