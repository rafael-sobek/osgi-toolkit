/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.developers.blog.osgi.webservice.rest.test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import java.net.URI;
import java.util.Enumeration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.developers.blog.osgi.webservice.test.provider.Activator;
import org.developers.blog.osgi.webservice.test.provider.Customer;
import org.developers.blog.osgi.webservice.test.provider.RestCustomerService;
import org.developers.blog.osgi.webservice.test.provider.RestProvider;
import org.junit.Ignore;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import static org.ops4j.pax.exam.CoreOptions.*;

import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.swissbox.tinybundles.dp.Constants;
import static org.ops4j.pax.swissbox.tinybundles.core.TinyBundles.*;

/**
 *
 * @author rafsob
 */
@RunWith(JUnit4TestRunner.class)
public class RestServiceProviderTest {

    @Inject
    BundleContext bundleContext = null;
    // you get that because you "installed" a log profile in configuration.
    public Log logger = LogFactory.getLog(RestServiceProviderTest.class);

    @Configuration
    public static Option[] configure() {
//        String tmpPaxDir = System.getProperty("java.io.tmpdir") + "/paxexam_runner_" + System.getProperty("user.name");
//        boolean deleted = (new File(tmpPaxDir)).delete();
//        if (!deleted) {
//            throw new RuntimeException("Pax Exam Temp couldn't deleted -> " + tmpPaxDir);
//        }
        return options(
                waitForFrameworkStartup(),
                frameworks(//equinox()),
                felix().version("3.0.2")),
                bootDelegationPackages("java.*", "sun.*", "org.xml.*", "org.w3c.*", "com.sun.*", "javax.*"),
                debugClassLoading(),
                //systemProperty("felix.log.level").value("4"),
                //systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("INFO"),
                systemProperty("org.apache.felix.http.jettyEnabled").value("true"),
                systemProperty("org.apache.felix.http.whiteboardEnabled").value("true"),
                wrappedBundle(mavenBundle().groupId("com.sun.jersey").artifactId("jersey-core").version("1.5")),
                wrappedBundle(mavenBundle().groupId("com.sun.jersey").artifactId("jersey-client").version("1.5")),
                mavenBundle().groupId("org.osgi").artifactId("org.osgi.compendium").version("4.2.0"),
                mavenBundle().groupId("org.ops4j.pax.logging").artifactId("pax-logging-api").version("1.5.3"),
                mavenBundle().groupId("org.ops4j.pax.logging").artifactId("pax-logging-service").version("1.5.3"),
                mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.configadmin").version("1.2.4"),
                //mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.http.bundle").version("2.0.4"),
                mavenBundle().groupId("org.ops4j.pax.web").artifactId("pax-web-jetty-bundle").version("1.0.3"),
                mavenBundle().groupId("org.developers.blog.osgi.webservice").artifactId("jaxrs-provider").version("1.0-SNAPSHOT"),
                provision(newBundle().add(Activator.class).add(Customer.class).add(RestCustomerService.class).add(RestProvider.class).set(Constants.BUNDLE_SYMBOLICNAME, "RestProviderTestBundle").set(Constants.EXPORT_PACKAGE, "org.developers.blog.osgi.webservice.test.provider").set(Constants.IMPORT_PACKAGE, "org.osgi.framework,org.developers.blog.osgi.webservice.jaxrs.api,org.developers.blog.osgi.webservice.test.provider,javax.ws.rs.core,javax.ws.rs").set(Constants.REQUIRE_BUNDLE, "org.developers.blog.osgi.webservice.jaxrs-provider").set(Constants.BUNDLE_ACTIVATOR, "org.developers.blog.osgi.webservice.test.provider.Activator").build(withBnd())));
    }

    @Test
    public void allBundlesAreRunning() throws Exception {
        logger.info("This is running inside Felix. With all configuration set up like you specified. ");
        int failedCount = 0;
        String failMessages = "\n";
        for (Bundle b : bundleContext.getBundles()) {
            logger.info("Bundle " + b.getBundleId() + " : " + b.getSymbolicName() + " : " + b.getState());
            logger.info("MANIFEST:");
            Enumeration enums = b.getHeaders().keys();
            while (enums.hasMoreElements()) {
                String key = (String) enums.nextElement();
                logger.info(key + "|" + b.getHeaders().get(key));
            }
            if (b.getState() != 32) {
                failedCount++;
                failMessages += "Starting bundle failed: " + b.getSymbolicName() + "|" + b.getVersion() + "\n";

            }

        }
        if (failedCount > 0) {
            fail(failMessages);
        }
    }

    @Test
    public void resourceReadTest() throws Exception {

        Client client = Client.create();
        WebResource webResource = client.resource(getBaseURI());
        Customer customer1 = webResource.path("customers").path("1").accept(MediaType.APPLICATION_XML_TYPE).get(Customer.class);
        assertEquals("Rafael", customer1.getFirstName());
        assertEquals("Sobek", customer1.getSecondName());
        assertEquals("Karlsruhe", customer1.getCity());

        Customer customer2 = webResource.path("customers").path("2").accept(MediaType.APPLICATION_XML_TYPE).get(Customer.class);
        assertEquals("Peter", customer2.getFirstName());




        //DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        //Document document = documentBuilder.parse(customerData);
    }

    @Ignore
    @Test
    public void massiveDynamicReadLoadTest() throws Exception {
        for (int i = 0; i < 10; i++) {
            resourceReadTest();
            Bundle bundle = getBundleForSymbolicName("RestProviderTestBundle");
            assertEquals(Bundle.ACTIVE, waitForBundle(bundle));
            bundle.stop();
            assertFalse(checkRestServiceRunning(false));
            bundle.start();
            logger.info("Test " + i + ": finished");
        }
    }

    private boolean waitForBundle(Bundle bundle) throws Exception {
        int timeout = 10000;
        int time = 0;
        boolean started = false;
        while (bundle.getState() != Bundle.ACTIVE) {
            Thread.sleep(100);
            time += 100;
            if (time == timeout) break;
        }
        if (time == timeout)
            return false;
        else
            return true;
    }

    private boolean checkRestServiceRunning(boolean debugException) throws InterruptedException {
        boolean result = true;
        try {
            Client client = Client.create();
            WebResource webResource = client.resource(getBaseURI());
            Customer customer1 = webResource.path("customers").path("1").accept(MediaType.APPLICATION_XML_TYPE).get(Customer.class);
        } catch (Exception e) {
            result = false;
            if (debugException) {
                logger.debug(e.getMessage(), e);
            }
        }
        return result;
    }

    @Test
    public void testDependentBundleLifecyclePermutation1Test() throws Exception {
        Bundle paxWebBundle =
                getBundleForSymbolicName("org.ops4j.pax.web.pax-web-jetty-bundle");
        Bundle restServiceProviderBundle =
                getBundleForSymbolicName("org.developers.blog.osgi.webservice.jaxrs-provider");
        paxWebBundle.stop();
        restServiceProviderBundle.stop();
        assertFalse(checkRestServiceRunning(false));
        paxWebBundle.start();
        assertTrue(waitForBundle(paxWebBundle));
        restServiceProviderBundle.start();
        assertTrue(waitForBundle(restServiceProviderBundle));
        assertTrue(checkRestServiceRunning(true));
    }

    @Test
    public void testDependentBundleLifecyclePermutation2Test() throws Exception {
        Bundle paxWebBundle =
                getBundleForSymbolicName("org.ops4j.pax.web.pax-web-jetty-bundle");
        paxWebBundle.stop();
        assertFalse(checkRestServiceRunning(false));
        paxWebBundle.start();
        assertTrue(waitForBundle(paxWebBundle));
        assertTrue(checkRestServiceRunning(true));
    }

    @Test
    public void testDependentBundleLifecyclePermutation3Test() throws Exception {
        Bundle restServiceProviderBundle =
                getBundleForSymbolicName("org.developers.blog.osgi.webservice.jaxrs-provider");
        restServiceProviderBundle.stop();
        assertFalse(checkRestServiceRunning(false));
        restServiceProviderBundle.start();
        assertTrue(checkRestServiceRunning(true));
    }

    @Test
    public void testDependentBundleLifecyclePermutation4Test() throws Exception {
        Bundle paxWebBundle =
                getBundleForSymbolicName("org.ops4j.pax.web.pax-web-jetty-bundle");
        Bundle restServiceProviderBundle =
                getBundleForSymbolicName("org.developers.blog.osgi.webservice.rest-service-provider");
        Bundle restTestProviderBundle =
                getBundleForSymbolicName("RestProviderTestBundle");
        restTestProviderBundle.stop();
        assertFalse(checkRestServiceRunning(false));
        restTestProviderBundle.start();
        assertTrue(checkRestServiceRunning(true));
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri(
                "http://localhost:8080/webservices/rest/").build();
    }

    @Test
    public void simpleRestProviderWhitboardTest() throws Exception {
        Bundle bundle = getBundleForSymbolicName("RestProviderTestBundle");
        assertEquals(Bundle.ACTIVE, bundle.getState());
        bundle.stop();
        bundle.start();
    }

    private Bundle getBundleForSymbolicName(String symbolicName) {
        for (Bundle b : bundleContext.getBundles()) {
            if (b.getSymbolicName().equals(symbolicName)) {
                return b;
            }
        }
        return null;
    }
}
