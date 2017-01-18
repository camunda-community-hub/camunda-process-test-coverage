package org.camunda.bpm.extension.process_test_coverage.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;

/**
 * Based on info found at http://stackoverflow.com/questions/15359702/get-location-of-jar-file
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ClassLocationURL {

  static File fileFor(Class aclass) {
    URL location = locationFor(aclass);
    if (location == null)
      throw new IllegalStateException("Could not locate " + aclass.getCanonicalName());
    try {
      return new File(location.toURI());
    } catch(URISyntaxException ex) {
      return new File(location.getPath());
    }
  }

  static URL locationFor(Class aclass) {
    URL location = urlFromCodeSource(aclass);
    return location != null ? location : urlFromResource(aclass);
  }

  static URL urlFromCodeSource(Class aclass) {
    try {
      CodeSource codeSource = aclass.getProtectionDomain().getCodeSource();
      URL url = codeSource != null ? codeSource.getLocation() : null;
      // url is in one of two forms
      // 1) ./build/classes/
      // 2) jardir/JarName.jar
      return url;
    } catch (SecurityException ex) {
      return null;
    }
  }

  static URL urlFromResource(Class aclass) {
    URL url = aclass.getResource(aclass.getSimpleName() + ".class");
    // 1) file:/U:/java/Tools/UI/build/classes/org/junit/Test.class
    // 2) jar:file:/U:/java/Tools/UI/dist/UI.jar!/org/junit/Test.class
    // We transform those forms to the forms delivered by urlFromCodeSource
    String suffix = (aclass.getName()).replace(".", "/") + ".class";
    try {
      String u = url.toExternalForm();
      return new URL(u.startsWith("jar:") ? u.replaceFirst("jar:", "").replace("!/" + suffix, "") : u.replace(suffix, ""));
    } catch (MalformedURLException e) {
      return null;
    }
  }

}
