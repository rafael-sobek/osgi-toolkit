/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.developers.blog.osgi.webservice.jaxrs.impl;

/**
 *
 * @author rafsob
 */
public interface ServiceEventListener {
    public void stateChanged(String event, Object resultObject);

}
