/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.developers.blog.osgi.webservice.jaxrs.impl;

/**
 *
 * @author rafsob
 */
public class AddServiceEventListener {
    protected ServiceEventListener serviceEventListener;

    public void addServiceEventListener(ServiceEventListener serviceEventListener) {
        this.serviceEventListener = serviceEventListener;
    }
}
