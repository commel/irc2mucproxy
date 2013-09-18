/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.qcore.muc2ircproxy.xmpp;

import de.qcore.muc2ircproxy.Bot;
import de.qcore.muc2ircproxy.config.ProxyConfiguration;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;
/**
 *
 * @author britter
 */
public class MucBot implements Bot {
  
  private static final Logger LOG = Logger.getLogger(MucBot.class.getName());
  
  private XMPPConnection connection;
  private MultiUserChat muc;
  private Bot endpointBot;
  
  private String getHostname() {
    return ProxyConfiguration.getConfig().getString("xmpp.server.hostname");
  }
  
  private Integer getPort() {
    return ProxyConfiguration.getConfig().getInteger("xmpp.server.port", 5222);
  }
  
  private String getUsername() {
    return ProxyConfiguration.getConfig().getString("xmpp.bot.username");
  }
  
  private String getPassword() {
    return ProxyConfiguration.getConfig().getString("xmpp.bot.password");
  }  
  
  private String getResource() {
    return ProxyConfiguration.getConfig().getString("xmpp.bot.resource", "Bot");
  }  
  
  private String getChatroom() {
    return ProxyConfiguration.getConfig().getString("xmpp.server.muc.room");
  }
  
  private Boolean getSASLAuthenticationEnabled() {
    return ProxyConfiguration.getConfig().getBoolean("xmpp.server.sasl", false);
  }
  
  public void doConnect() throws Exception {
    LOG.log(Level.INFO, "Connecting to MUC...");        
    
    final ConnectionConfiguration config = new ConnectionConfiguration(getHostname(), getPort(), getResource());    
    config.setSASLAuthenticationEnabled(getSASLAuthenticationEnabled());
    connection = new XMPPConnection(config);
    connection.connect();
    connection.login(getUsername(), getPassword());
    
    muc = new MultiUserChat(connection, getChatroom());
    muc.join(getUsername());    
    muc.addMessageListener(new PacketListener() {
      @Override
      public void processPacket(Packet packet) {
        final Message msg = (Message) packet;
        LOG.log(Level.INFO, "{0} says: {1}", new Object[]{packet.getFrom(), msg.getBody()});        
        final String sayingUser = packet.getFrom().substring(packet.getFrom().lastIndexOf("/") + 1);
        if (!sayingUser.equalsIgnoreCase(getUsername())) {
          try {
            endpointBot.sendProxyMessage(sayingUser, msg.getBody());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Could not send message", e);
          }
        }
      }
    });
    LOG.log(Level.INFO, "Connected to MUC {0}", new Object[] {getChatroom()});    
  }
  
  public void sendProxyMessage(final String user, final String message) throws Exception {
    final StringWriter buffer = new StringWriter();
    buffer.append(user);
    buffer.append(endpointBot.getIdent());
    buffer.append(": ");
    buffer.append(message);
    muc.sendMessage(buffer.toString());
  }
  
  @Override
  public void doDisconnect() throws Exception {
    connection.disconnect();
  }
  
  @Override
  public void doReconnect() throws Exception {
    doDisconnect();
    doConnect();
  }
  
  public boolean hasConnection() {
    return connection != null && connection.isConnected();
  }

  public void addEndpoint(final Bot bot) {
    this.endpointBot = bot;
  }
  
  public String getIdent() {
    return "@jabber";
  }  
  
}
