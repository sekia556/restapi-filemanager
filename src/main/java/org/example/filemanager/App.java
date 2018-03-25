package org.example.filemanager;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Start point.
 * We use mime-multipart for file uploading, so this class
 * exntends ResourceConfig(from jersey) instead of Application.
 * @author sekia
 */
@ApplicationPath("rest")
//public class App extends Application {
public class App extends ResourceConfig {

  /**
   * Constructor.
   */
  public App() {
    packages("org.example.filemanager");
    //property(ServerProperties.PROVIDER_SCANNING_RECURSIVE, false);

    register(MultiPartFeature.class);
  }
}
