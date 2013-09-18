package de.qcore.muc2ircproxy;

import de.qcore.muc2ircproxy.irc.IrcBot;
import de.qcore.muc2ircproxy.xmpp.MucBot;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App {
  private static final Logger LOG = Logger.getLogger(App.class.getName());

  public static void main(String[] args) throws Exception {
    LOG.log(Level.INFO, "Starting MUC/IRC Proxy");
    final App app = new App();
  }

  public App() throws Exception {
    final Bot ircBot = new IrcBot();
    final Bot mucBot = new MucBot();
    
    ircBot.addEndpoint(mucBot);
    mucBot.addEndpoint(ircBot);
    
    ircBot.doConnect();
    mucBot.doConnect();
  }
}
