/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.developers.blog.osgi.webservice.jaxrs.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.transport.common.gzip.GZIPInInterceptor;
import org.apache.cxf.transport.common.gzip.GZIPOutInterceptor;
import org.developers.blog.osgi.webservice.jaxrs.api.JAXRSProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rafsob
 */
public class ServiceStateCoordinator {
    private Logger log = LoggerFactory.getLogger(ServiceStateCoordinator.class);
    
    public final static String ADD_CXF_BUS = "ADD_CXF_BUS";
    public final static String REMOVE_CXF_BUS = "REMOVE_CXF_BUS";
    public final static String ADD_REST_PROVIDER = "ADD_REST_PROVIDER";
    public final static String REMOVE_REST_PROVIDER = "REMOVE_REST_PROVIDER";

    public Map<JAXRSProvider,String> providerValues =
            new HashMap<JAXRSProvider, String>();

    public Map<JAXRSProvider,Server> servers =
            new HashMap<JAXRSProvider, Server>();

    private Bus bus;

    private Server registerRestService(JAXRSProvider restProvider) {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBus(bus);
        factory.setAddress(restProvider.getAlias());
        factory.setResourceClasses(restProvider.getService().getClass());
        factory.setResourceProvider(restProvider.getService().getClass(), new SingletonResourceProvider(restProvider.getService()));
        factory.getInInterceptors().add(new GZIPInInterceptor());
        factory.getOutInterceptors().add(new GZIPOutInterceptor());
        ClassLoader bundleClassLoader =
                Thread.currentThread().getContextClassLoader();
        ClassLoader delegateClassLoader =
                JAXRSServerFactoryBean.class.getClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(delegateClassLoader);
            return factory.create();
        } finally {
            Thread.currentThread().setContextClassLoader(bundleClassLoader);
        }
    }

    public synchronized void stateChanged(String event, Object resultObject) {
        if (event.equals(ADD_CXF_BUS)) {
            bus = (Bus) resultObject;
            log.info(ADD_CXF_BUS + ":" + bus.getId());
            for (JAXRSProvider restProvider:providerValues.keySet()) {
                log.info("Registration RestProvider:" + restProvider.getAlias() + " | " + restProvider.getService().getClass() + " | " + providerValues.get(restProvider));
                Server server = registerRestService(restProvider);
                servers.put(restProvider,server);
            }
        } else if (event.equals(REMOVE_CXF_BUS)) {
            for (Server server:servers.values()) {
                server.destroy();
            }
            servers.clear();
            log.info(REMOVE_CXF_BUS + ":" + bus.getId());
            bus = null;
        } else if (event.equals(ADD_REST_PROVIDER)) {
            List<Object> values = (List<Object>)resultObject;
            JAXRSProvider restProvider = (JAXRSProvider)values.get(0);
            providerValues.put(restProvider, null);
            if (bus != null) {
                log.info("Registration RestProvider:" + restProvider.getAlias() + " | " + restProvider.getService().getClass() + " | " + providerValues.get(restProvider));
                Server server = registerRestService(restProvider);
                servers.put(restProvider, server);
            }
        } else if (event.equals(REMOVE_REST_PROVIDER)) {
            List<Object> values = (List<Object>)resultObject;
            JAXRSProvider restProvider = (JAXRSProvider)values.get(0);
            log.info("Deregistration RestProvider:" + restProvider.getAlias() + " | " + restProvider.getService().getClass());
            if (servers.containsKey(restProvider)) {
                Server server = servers.get(restProvider);
                server.destroy();
            } else {
                System.out.println("No Server Destroyed!!!");
            }
            providerValues.remove(restProvider);
        }
    }
}
