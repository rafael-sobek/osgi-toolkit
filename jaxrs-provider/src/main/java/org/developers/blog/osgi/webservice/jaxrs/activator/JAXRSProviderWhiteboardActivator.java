/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.developers.blog.osgi.webservice.jaxrs.activator;

import org.developers.blog.osgi.webservice.jaxrs.impl.HttpServiceTrackerCustomizer;
import org.developers.blog.osgi.webservice.jaxrs.impl.RestProviderServiceTrackerCustomizer;
import org.developers.blog.osgi.webservice.jaxrs.impl.ServiceStateListener;
import org.developers.blog.osgi.webservice.jaxrs.api.JAXRSProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

/**
 *
 * @author rafsob
 */
public class JAXRSProviderWhiteboardActivator implements BundleActivator {

    private ServiceTracker restServiceProviderTracker;
    private ServiceTracker httpServiceTracker;

    @Override
    public void start(BundleContext context) throws Exception {
        RestProviderServiceTrackerCustomizer restProviderServiceTrackerCustomizer =
                new RestProviderServiceTrackerCustomizer(context);
        HttpServiceTrackerCustomizer httpServiceTrackerCustomizer = 
                new HttpServiceTrackerCustomizer(context);

        ServiceStateListener serviceStateListener = new ServiceStateListener();
        restProviderServiceTrackerCustomizer.addServiceEventListener(serviceStateListener);
        httpServiceTrackerCustomizer.addServiceEventListener(serviceStateListener);

        restServiceProviderTracker =
                new ServiceTracker(context, JAXRSProvider.class.getName(), restProviderServiceTrackerCustomizer);
        httpServiceTracker =
                new ServiceTracker(context, HttpService.class.getName(), httpServiceTrackerCustomizer);
        httpServiceTracker.open();
        restServiceProviderTracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (restServiceProviderTracker != null) {
            restServiceProviderTracker.close();
        }
        if (httpServiceTracker != null) {
            httpServiceTracker.close();
        }
    }
}
