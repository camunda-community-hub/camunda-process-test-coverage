package org.camunda.community.process_test_coverage.report;

/*-
 * #%L
 * Camunda Process Test Coverage Report Generator
 * %%
 * Copyright (C) 2019 - 2024 Camunda
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

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
//    doesn't work in Java 11: Assert.assertTrue(url.toExternalForm().startsWith("file:"));
//    doesn't work in Java 11: Assert.assertTrue(url.toExternalForm().endsWith(".jar"));
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
//    doesn't work in Java 11: Assert.assertTrue(url.toExternalForm().startsWith("file:"));
//    doesn't work in Java 11: Assert.assertTrue(url.toExternalForm().endsWith(".jar"));
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
  @Ignore // doesn't work in Java 11
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
