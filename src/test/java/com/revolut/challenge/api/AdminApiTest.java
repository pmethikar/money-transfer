package com.revolut.challenge.api;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.revolut.challenge.model.Account;
import com.revolut.challenge.service.AccountService;
import org.apache.cxf.common.util.ProxyClassLoader;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.print.Book;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class AdminApiTest {

    private AccountService accountService;

    private AdminApi api;

    private Server server;
    private WebClient client;

    private static final String ENDPOINT_ADDRESS = "http://localhost:9001";


    @Before
    public void setUp() {
        accountService = new AccountService();
        api = new AdminApi(accountService);

        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(AdminApi.class);
        sf.setAddress(ENDPOINT_ADDRESS);
        sf.setServiceBean(api);
        sf.setProvider(new JacksonJaxbJsonProvider());
        sf.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        server = sf.create();

        client = WebClient.create(ENDPOINT_ADDRESS, Arrays.asList(new JacksonJaxbJsonProvider()));
        client.path("/v1/admin/account");
        client.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testAddAccount() {
        Account a1 = createAccount(1L, new BigDecimal(100));
        Response resp = client.post(a1);
        assertEquals(Response.Status.CREATED.getStatusCode(), resp.getStatus());
    }

    @Test
    public void testAddAccountDuplicateAccount() {
        Account a1 = createAccount(1L, new BigDecimal(100));
        Response resp = client.post(a1);
        Account a2 = createAccount(1L, new BigDecimal(1000));
        resp = client.post(a1);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), resp.getStatus());
    }

    @Test
    public void testGetAccount() {
        Account expected = createAccount(1L, new BigDecimal(100));
        accountService.addAccount(expected);

        client.path("/" + expected.getAccountNo());
        Response resp = client.get();

        assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());

        Account actual = resp.readEntity(Account.class);
        assertEquals(expected.getAccountNo(), actual.getAccountNo());
        assertEquals(expected.getBalance(), actual.getBalance());
    }

    @Test
    public void testGetAccountInvalidAccount() {
        client.path("/1");
        Response resp = client.get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
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
