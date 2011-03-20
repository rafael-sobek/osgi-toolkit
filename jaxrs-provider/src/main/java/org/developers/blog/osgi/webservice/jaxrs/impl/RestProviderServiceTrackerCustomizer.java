/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.developers.blog.osgi.webservice.jaxrs.impl;

import java.util.ArrayList;
import java.util.List;
import org.developers.blog.osgi.webservice.jaxrs.api.JAXRSProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rafsob
 */
public class RestProviderServiceTrackerCustomizer extends AddServiceEventListener implements ServiceTrackerCustomizer {
    private Logger log = LoggerFactory.getLogger(RestProviderServiceTrackerCustomizer.class);
    private BundleContext bundleContext;

    public RestProviderServiceTrackerCustomizer(BundleContext context) {
        this.bundleContext = context;
    }

    @Override
    public Object addingService(ServiceReference reference) {
        final JAXRSProvider provider =
                (JAXRSProvider) bundleContext.getService(reference);
        List<Object> values = new ArrayList<Object>();
        values.add(provider);
        values.add(getVersion(reference.getBundle().getVersion()));
        serviceEventListener.stateChanged(
                ServiceStateListener.ADD_REST_PROVIDER,
                values
                );
        return provider;
    }

    @Override
    public void modifiedService(ServiceReference reference, Object service) {
        //noop
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        List<Object> values = new ArrayList<Object>();
        values.add(service);
        values.add(getVersion(reference.getBundle().getVersion()));
        serviceEventListener.stateChanged(ServiceStateListener.REMOVE_REST_PROVIDER, 
                values);
    }

    private String getVersion(Version version) {
        String versionAggregated = "" + version.getMajor() + "." +
                version.getMicro() + "." + version.getMinor();
        return versionAggregated;
    }

}
