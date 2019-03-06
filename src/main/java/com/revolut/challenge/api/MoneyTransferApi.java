package com.revolut.challenge.api;

import com.revolut.challenge.exception.MoneyTransferException;
import com.revolut.challenge.model.MoneyTransfer;
import com.revolut.challenge.service.AccountService;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;


@Path("/v1/transfer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MoneyTransferApi {
    private static final Logger LOG = LoggerFactory.getLogger(MoneyTransferApi.class);

    private AccountService service;

    public MoneyTransferApi() {
    }

    public MoneyTransferApi(AccountService service) {
        this.service = service;
    }

    @POST
    public Response transfer(MoneyTransfer transfer) {
        LOG.info("Received instruction to transfer from account {} to account {}", transfer.getFromAccount(), transfer.getToAccount());
        try {
            BigDecimal updatedBalance = service.transfer(transfer);
            LOG.info("Transfer Successful");
            return Response.accepted("Transfer successful. Updated balance: " + updatedBalance).build();
        } catch (MoneyTransferException e) {
            LOG.error("Error occurred during money transfer: ", e);
            return Response.status(HttpStatus.BAD_REQUEST_400).entity(e.getMessage()).build();
        }
    }
}