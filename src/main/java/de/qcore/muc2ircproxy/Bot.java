/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.qcore.muc2ircproxy;

/**
 *
 * @author britter
 */
public interface Bot {
  
  void doConnect() throws Exception;
  void doDisconnect() throws Exception;
  void doReconnect() throws Exception;
  boolean hasConnection();
  
  void addEndpoint(Bot bot);
  
  void sendProxyMessage(String user, String message) throws Exception;
  
  String getIdent();
  
}
