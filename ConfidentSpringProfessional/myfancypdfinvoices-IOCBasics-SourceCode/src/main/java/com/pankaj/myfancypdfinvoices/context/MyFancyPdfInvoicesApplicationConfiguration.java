package com.pankaj.myfancypdfinvoices.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pankaj.ApplicationLauncher;
import com.pankaj.service.InvoiceService;
import com.pankaj.service.UserService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackageClasses = ApplicationLauncher.class)
@PropertySource("classpath:/application.properties")
@PropertySource(value = "classpath:/application-${spring.profiles.active}.properties",
        ignoreResourceNotFound = true)
//@PropertySource("classpath:/someOtherFile.properties")
public class MyFancyPdfInvoicesApplicationConfiguration {

    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public UserService userService() {
        return new UserService();
    }


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
