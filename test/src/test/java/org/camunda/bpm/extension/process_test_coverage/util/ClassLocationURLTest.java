package org.camunda.bpm.extension.process_test_coverage.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ClassLocationURLTest {

  @Test
  public void test_urlFromCodeSource_JavaLangStringClass() {
    URL url = ClassLocationURL.urlFromCodeSource(String.class);
    Assert.assertNull(url);
  }

  @Test
  public void test_urlFromCodeSource_OrgJunitTestClass() {
    URL url = ClassLocationURL.urlFromCodeSource(Test.class);
    Assert.assertNotNull(url);
    Assert.assertTrue(url.toExternalForm().startsWith("file:"));
    Assert.assertTrue(url.toExternalForm().endsWith(".jar"));
  }

  @Test
  public void test_urlFromCodeSource_ThisClass() {
    URL url = ClassLocationURL.urlFromCodeSource(ClassLocationURLTest.class);
    Assert.assertNotNull(url);
    Assert.assertTrue(url.toExternalForm().startsWith("file:"));
    Assert.assertFalse(url.toExternalForm().endsWith(".jar"));
    Assert.assertTrue(url.toExternalForm().endsWith("/"));
  }

  @Test
  public void test_urlFromResource_JavaLangStringClass() {
    URL url = ClassLocationURL.urlFromResource(String.class);
    Assert.assertNotNull(url);
    Assert.assertTrue(url.toExternalForm().startsWith("file:"));
    Assert.assertTrue(url.toExternalForm().endsWith(".jar"));
  }

  @Test
  public void test_urlFromResource_OrgJunitTestClass() {
    URL url = ClassLocationURL.urlFromResource(Test.class);
    Assert.assertNotNull(url);
    Assert.assertTrue(url.toExternalForm().startsWith("file:"));
    Assert.assertTrue(url.toExternalForm().endsWith(".jar"));
  }

  @Test
  public void test_urlFromResource_ThisClass() {
    URL url = ClassLocationURL.urlFromResource(ClassLocationURLTest.class);
    Assert.assertNotNull(url);
    Assert.assertTrue(url.toExternalForm().startsWith("file:"));
    Assert.assertFalse(url.toExternalForm().endsWith(".jar"));
    Assert.assertTrue(url.toExternalForm().endsWith("/"));
  }

  @Test
  public void test_locationFor_JavaLangStringClass() {
    URL url = ClassLocationURL.locationFor(String.class);
    Assert.assertNotNull(url);
    Assert.assertTrue(url.toExternalForm().startsWith("file:"));
    Assert.assertTrue(url.toExternalForm().endsWith(".jar"));
  }

  @Test
  public void test_locationFor_OrgJunitTestClass() {
    URL url = ClassLocationURL.locationFor(Test.class);
    Assert.assertNotNull(url);
    Assert.assertTrue(url.toExternalForm().startsWith("file:"));
    Assert.assertTrue(url.toExternalForm().endsWith(".jar"));
  }

  @Test
  public void test_locationFor_ThisClass() {
    URL url = ClassLocationURL.locationFor(ClassLocationURLTest.class);
    Assert.assertNotNull(url);
    Assert.assertTrue(url.toExternalForm().startsWith("file:"));
    Assert.assertFalse(url.toExternalForm().endsWith(".jar"));
    Assert.assertTrue(url.toExternalForm().endsWith("/"));
  }

  @Test
  public void test_fileFor_JavaLangStringClass() {
    File file  = ClassLocationURL.fileFor(String.class);
    Assert.assertNotNull(file);
    Assert.assertTrue(file.getAbsolutePath().endsWith(".jar"));
    Assert.assertTrue(file.isFile());
  }

  @Test
  public void test_fileFor_OrgJunitTestClass() {
    File file = ClassLocationURL.fileFor(Test.class);
    Assert.assertNotNull(file);
    Assert.assertTrue(file.getAbsolutePath().endsWith(".jar"));
    Assert.assertTrue(file.isFile());
  }

  @Test
  public void test_fileFor_ThisClass() {
    File file = ClassLocationURL.fileFor(ClassLocationURLTest.class);
    Assert.assertNotNull(file);
    Assert.assertFalse(file.getAbsolutePath().endsWith(".jar"));
    Assert.assertTrue(file.isDirectory());
  }

}
