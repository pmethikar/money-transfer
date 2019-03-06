package com.revolut.challenge.api;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.revolut.challenge.model.Account;
import com.revolut.challenge.model.MoneyTransfer;
import com.revolut.challenge.service.AccountService;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class MoneyTransferApiTest {
    private AccountService accountService;

    private MoneyTransferApi api;

    private Server server;
    private WebClient client;

    private static final String ENDPOINT_ADDRESS = "http://localhost:9001";

    @Before
    public void setUp() {
        accountService = new AccountService();
        api = new MoneyTransferApi(accountService);

        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(MoneyTransferApi.class);
        sf.setAddress(ENDPOINT_ADDRESS);
        sf.setServiceBean(api);
        sf.setProvider(new JacksonJaxbJsonProvider());
        sf.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        server = sf.create();

        client = WebClient.create(ENDPOINT_ADDRESS, Arrays.asList(new JacksonJaxbJsonProvider()));
        client.path("/v1/transfer");
        client.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testTransfer(){
        Account from = createAccount(1L, new BigDecimal("29578"));
        accountService.addAccount(from);
        Account to = createAccount(2L, new BigDecimal("19978.25"));
        accountService.addAccount(to);

        MoneyTransfer transfer = getMoneyTransferInstruction(from.getAccountNo(), to.getAccountNo(), new BigDecimal(5000));

        Response resp = client.post(transfer);
        assertEquals(HttpStatus.ACCEPTED_202, resp.getStatus());

        BigDecimal newFromBalance = accountService.getAccount(from.getAccountNo()).getBalance();
        BigDecimal newToBalance = accountService.getAccount(to.getAccountNo()).getBalance();
        assertEquals(new BigDecimal("24578"), newFromBalance);
        assertEquals(new BigDecimal("24978.25"), newToBalance);
        assertEquals("Transfer successful. Updated balance: " + newFromBalance, resp.readEntity(String.class));
    }

    @Test
    public void testTransferInvalidFrom(){
        Account to = createAccount(1L, new BigDecimal("19978.25"));
        accountService.addAccount(to);

        MoneyTransfer transfer = getMoneyTransferInstruction(2L, to.getAccountNo(), new BigDecimal(5000));

        Response resp = client.post(transfer);
        assertEquals(HttpStatus.BAD_REQUEST_400, resp.getStatus());
    }

    @Test
    public void testTransferInvalidTo(){
        Account from = createAccount(1L, new BigDecimal("19978.25"));
        accountService.addAccount(from);

        MoneyTransfer transfer = getMoneyTransferInstruction(from.getAccountNo(), 2L, new BigDecimal(5000));

        Response resp = client.post(transfer);
        assertEquals(HttpStatus.BAD_REQUEST_400, resp.getStatus());
    }


    @Test
    public void testTransferSameFromAndTo(){
        Account from = createAccount(1L, new BigDecimal("19978.25"));
        accountService.addAccount(from);

        MoneyTransfer transfer = getMoneyTransferInstruction(from.getAccountNo(), from.getAccountNo(), new BigDecimal(5000));

        Response resp = client.post(transfer);
        assertEquals(HttpStatus.BAD_REQUEST_400, resp.getStatus());
    }


    @Test
    public void testTransferInsufficientBalance(){
        Account to = createAccount(1L, new BigDecimal("19978.25"));
        accountService.addAccount(to);
        Account from = createAccount(2L, new BigDecimal("29578"));
        accountService.addAccount(from);

        MoneyTransfer transfer = getMoneyTransferInstruction(from.getAccountNo(), from.getAccountNo(),
                new BigDecimal(30000));

        Response resp = client.post(transfer);
        assertEquals(HttpStatus.BAD_REQUEST_400, resp.getStatus());
    }


    private MoneyTransfer getMoneyTransferInstruction(Long from, Long to, BigDecimal amt) {
        MoneyTransfer mt = new MoneyTransfer();
        mt.setFromAccount(from);
        mt.setToAccount(to);
        mt.setAmount(amt);
        return mt;
    }

    private Account createAccount(long no, BigDecimal amount) {
        Account a = new Account();
        a.setAccountNo(no);
        a.setBalance(amount);
        return a;
    }

    @After
    public void cleanUp() {
        server.stop();
        server.destroy();
    }
}
