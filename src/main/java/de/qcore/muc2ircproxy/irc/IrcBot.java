/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.qcore.muc2ircproxy.irc;

import de.qcore.muc2ircproxy.Bot;
import de.qcore.muc2ircproxy.config.ProxyConfiguration;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jibble.pircbot.PircBot;

/**
 *
 * @author britter
 */
public class IrcBot extends PircBot implements Bot {
  
  private static final Logger LOG = Logger.getLogger(IrcBot.class.getName());
  
  private Bot endpointBot;
  
  @Override
  public void onMessage(final String channel, final String sender, final String login, final String hostname, final String message) {  
    super.onMessage(channel, sender, login, hostname, message);
    if (channel.equalsIgnoreCase(getChannel()) && !sender.equalsIgnoreCase(getBotname())) {
      LOG.log(Level.INFO, "{0} says: {1}:", new Object[] {sender, message});
      try {
        endpointBot.sendProxyMessage(sender, message);
      } catch (Exception e) {
        LOG.log(Level.SEVERE, "Could not send message", e);
      }
    }
  }
  
  @Override
  public void sendProxyMessage(final String user, final String message) {
    final StringWriter buffer = new StringWriter();
    buffer.append(user);
    buffer.append(endpointBot.getIdent());
    buffer.append(": ");
    buffer.append(message);
    sendMessage(getChannel(), buffer.toString());
  }
  
  private String getBotname() {
    return ProxyConfiguration.getConfig().getString("irc.bot.name", "Harald");
  }
  
  private String getHostname() {
    return ProxyConfiguration.getConfig().getString("irc.server.hostname");
  }
  
  private Integer getHostPort() {
    return ProxyConfiguration.getConfig().getInt("irc.server.port", 6667);
  }
  
  private String getChannel() {
    return ProxyConfiguration.getConfig().getString("irc.server.channel");
  }

  public void doConnect() throws Exception {
    LOG.log(Level.INFO, "Connecting to IRC...");
    setName(getBotname());
    connect(getHostname(), getHostPort());
    joinChannel(getChannel());
    LOG.log(Level.INFO, "Connected to IRC");
  }

  public void doDisconnect() throws Exception {
    disconnect();
  }

  public void doReconnect() throws Exception {
    reconnect();
  }
  
  public boolean hasConnection() {
    return isConnected();
  }

  public void addEndpoint(final Bot bot) {
    this.endpointBot = bot;
  }
  
  public String getIdent() {
    return "@irc";
  }
  
}
