/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.developers.blog.osgi.webservice.jaxrs.impl;

import java.util.Hashtable;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rafsob
 */
public class HttpServiceTrackerCustomizer extends AddServiceEventListener implements ServiceTrackerCustomizer {
    private Logger log = LoggerFactory.getLogger(HttpServiceTrackerCustomizer.class);

    private final static String rootPath = "/resources";

    private BundleContext bundleContext;

    CXFNonSpringServlet cxfServlet = null;

    public HttpServiceTrackerCustomizer(BundleContext context) {
        this.bundleContext = context;
    }

    @Override
    public Object addingService(ServiceReference reference) {
        HttpService httpService = null;
        try {
            httpService = (HttpService) bundleContext.getService(reference);
            if (cxfServlet == null) cxfServlet = new CXFNonSpringServlet();
            httpService.registerServlet(rootPath, cxfServlet, new Hashtable<String, String>(), null);
            serviceEventListener.stateChanged(ServiceStateListener.ADD_CXF_BUS, cxfServlet.getBus());
            
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return httpService;
    }

    @Override
    public void modifiedService(ServiceReference reference, Object service) {
        //noop
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        serviceEventListener.stateChanged(ServiceStateListener.REMOVE_CXF_BUS, null);
        cxfServlet.destroy();
        cxfServlet = null;
    }

}
