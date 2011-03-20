/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
        
package org.developers.blog.osgi.webservice.jaxrs.api;

/**
 *
 * @author rafsob
 */
public interface JAXRSProvider {
    public Object getService();
    public String getAlias();
}
