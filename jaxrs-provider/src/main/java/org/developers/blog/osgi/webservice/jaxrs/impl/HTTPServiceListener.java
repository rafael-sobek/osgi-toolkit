/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.developers.blog.osgi.webservice.jaxrs.impl;

import java.util.Dictionary;
import java.util.Hashtable;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rafsob
 */
public class HTTPServiceListener {
    private Logger log = LoggerFactory.getLogger(HTTPServiceListener.class);

    private final static String rootPath = "/rest";

    private CXFNonSpringServlet cxfServlet = null;
    
    private ServiceStateCoordinator coordinator;

    public void bind(HttpService httpService) throws Exception  {
        if (cxfServlet == null) cxfServlet = new CXFNonSpringServlet();
        Dictionary props = new Hashtable();
        props.put("servlet-name", "jaxrs-provider");
        httpService.registerServlet(rootPath, cxfServlet, props, null);
        coordinator.stateChanged(ServiceStateCoordinator.ADD_CXF_BUS, cxfServlet.getBus());
    }
    
    public void unbind(HttpService httpService) {
        coordinator.stateChanged(ServiceStateCoordinator.REMOVE_CXF_BUS, null);
        if (cxfServlet != null) {
            cxfServlet.destroy();
            cxfServlet = null;
        }
    }

    public ServiceStateCoordinator getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(ServiceStateCoordinator coordinator) {
        this.coordinator = coordinator;
    }

}
