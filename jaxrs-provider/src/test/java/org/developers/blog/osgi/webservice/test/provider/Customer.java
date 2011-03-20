/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.developers.blog.osgi.webservice.test.provider;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rafsob
 */
@XmlRootElement
public class Customer {

    public Customer() {
    }
    

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    private String firstName;
    private String secondName;
    private String email;
    private String city;

    public Customer(int id, String firstName, String secondName, String email, String city) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.city = city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getCity() {
        return city;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }
}