/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.qcore.muc2ircproxy.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 *
 * @author britter
 */
public class ProxyConfiguration {
  
  private ProxyConfiguration() {}
  
  private static Configuration config;
  
  static {
    try {
      config = new PropertiesConfiguration("proxy.properties");
    } catch (ConfigurationException ce) {
      throw new RuntimeException(ce);
    }
  }
  
  public static Configuration getConfig() {
    return config;
  }
  
}
