/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.developers.blog.osgi.webservice.jaxrs.impl;

import java.util.ArrayList;
import java.util.List;
import org.developers.blog.osgi.webservice.jaxrs.api.JAXRSProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rafsob
 */
public class JAXRSProviderListener {

    private Logger log = LoggerFactory.getLogger(JAXRSProviderListener.class);
    private ServiceStateCoordinator coordinator;

    public ServiceStateCoordinator getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(ServiceStateCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    public void bind(JAXRSProvider jaxrsProvider) {
        List<Object> values = new ArrayList<Object>();
        values.add(jaxrsProvider);
        coordinator.stateChanged(
                ServiceStateCoordinator.ADD_REST_PROVIDER,
                values);
    }

    public void unbind(JAXRSProvider jaxrsProvider) {
        if (jaxrsProvider != null) {
            List<Object> values = new ArrayList<Object>();
            values.add(jaxrsProvider);
            coordinator.stateChanged(ServiceStateCoordinator.REMOVE_REST_PROVIDER,
                    values);
        }
    }
}
