/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.developers.blog.osgi.webservice.test.provider;

import org.developers.blog.osgi.webservice.jaxrs.api.JAXRSProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author rafsob
 */
public class Activator implements BundleActivator {
    ServiceRegistration reg;
    
    @Override
    public void start(BundleContext context) throws Exception {
        reg = context.registerService(JAXRSProvider.class.getName(), new RestProvider(), null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        reg.unregister();
    }

    public static void main(String[] args) throws Exception {

    }

}
