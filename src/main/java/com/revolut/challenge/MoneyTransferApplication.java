package com.revolut.challenge;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.revolut.challenge.api.AdminApi;
import com.revolut.challenge.api.MoneyTransferApi;
import com.revolut.challenge.service.AccountService;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class MoneyTransferApplication {

    private static final Logger LOG = LoggerFactory.getLogger(MoneyTransferApplication.class);

    public static void main(String[] args) throws IOException {
        configureProperties();
        createAndStartServer();
        LOG.info("********MoneyTransferService started********");
    }

    private static void configureProperties() throws IOException {
        LOG.info("Configuring properties");
        Properties props = new Properties();
        props.load(MoneyTransferApplication.class.getClass().getResourceAsStream("/application.properties"));
        PropertyConfigurator.configure(props);
    }

    private static void createAndStartServer() {
        LOG.info("Starting server");
        AccountService accountService = new AccountService();
        AdminApi adminApi = new AdminApi(accountService);
        MoneyTransferApi transferApi = new MoneyTransferApi(accountService);

        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(MoneyTransferApi.class, AdminApi.class);
        sf.setAddress("http://localhost:9000");
        sf.setServiceBean(transferApi);
        sf.setServiceBean(adminApi);
        sf.setProvider(new JacksonJaxbJsonProvider());
        sf.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        sf.create();
    }
}
