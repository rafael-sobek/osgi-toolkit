/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.developers.blog.osgi.webservice.test.provider;

import org.developers.blog.osgi.webservice.jaxrs.api.JAXRSProvider;

/**
 *
 * @author rafsob
 */
public class RestProvider implements JAXRSProvider {

    @Override
    public Object getService() {
        return new RestCustomerService();
    }

    @Override
    public String getAlias() {
        return "/customers";
    }
}
