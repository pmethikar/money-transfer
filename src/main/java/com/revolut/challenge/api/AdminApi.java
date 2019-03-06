package com.revolut.challenge.api;


import com.revolut.challenge.exception.InvalidAccountException;
import com.revolut.challenge.model.Account;
import com.revolut.challenge.service.AccountService;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/*Admin Api is just for creating accounts for transfer purposes.
* Thus only basic account addition handled.*/
@Path("/v1/admin/account")
@Produces(MediaType.APPLICATION_JSON)
public class AdminApi {
    private static final Logger LOG = LoggerFactory.getLogger(AdminApi.class);

    private AccountService service;

    public AdminApi() {
    }

    public AdminApi(AccountService service) {
        this.service = service;
    }

    @POST
    public Response addAccount(Account account){
        try {
            service.addAccount(account);
            return Response.status(HttpStatus.CREATED_201).build();
        } catch (InvalidAccountException e){
            LOG.error("Error while creating account ", e);
            return Response.status(HttpStatus.BAD_REQUEST_400).entity(e.getMessage()).build();
        }
    }

    @Path("/{accountNo}")
    @GET
    public Response getAccount(@PathParam("accountNo") Long accountNo){
        try {
            return Response.ok(service.getAccount(accountNo)).build();
        } catch (InvalidAccountException e){
            LOG.error("Error while getting account ", e);
            return Response.status(HttpStatus.NOT_FOUND_404).entity(e.getMessage()).build();
        }
    }
}
