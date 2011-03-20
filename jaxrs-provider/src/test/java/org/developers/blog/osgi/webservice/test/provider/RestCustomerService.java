/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.developers.blog.osgi.webservice.test.provider;

import java.io.Serializable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 * @author rafsob
 */
@Path("/")
public class RestCustomerService implements Serializable {

    @GET
    @Path("{id}")
    @Produces("application/xml")
    public Response getCustomer(@PathParam("id") Integer customerId) {
        for (Customer customer:customers) {
            if (customerId.equals(customer.getId())) {
                return Response.ok(customer).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("")
    @Produces("application/xml")
    public Customer[] getAllCustomers() {
        return customers;
    }
    private Customer[] customers = new Customer[]{
        new Customer(1, "Rafael", "Sobek", "rafael@developersblog.org", "Karlsruhe"),
        new Customer(2, "Peter", "Grund", "peter@developersblog.org", "Karlsruhe")
    };
}
