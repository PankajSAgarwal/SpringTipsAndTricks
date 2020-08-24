package com.pankaj.service;

import com.pankaj.model.Invoice;
import com.pankaj.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
@Component
public class InvoiceService {
    private final UserService userService;
    private final String cdnUrl;
    List<Invoice> invoices = new CopyOnWriteArrayList<>();

    // @Autowired // optional in newer spring evrsion as Component scan can detect
    public InvoiceService(UserService userService,@Value("${cdn.url}") String cdnUrl) {

        this.userService = userService;
        this.cdnUrl = cdnUrl;
    }

    @PostConstruct
    public void init(){
        System.out.println("Fetching PDF templates from S3...");
        //TODO download from S3 and save locally
    }
    public List<Invoice> findAll(){
        return invoices;
    }

    public Invoice create(String userId, Integer amount) {
        User user = userService.findById(userId);
        if(user==null)
            throw new IllegalStateException();
        // TODO real pdf creation and storing it on network server
        Invoice invoice = new Invoice(userId, amount, cdnUrl + "/images/default/sample.pdf");
        invoices.add(invoice);
        return invoice;
    }

    @PreDestroy
    public void shutDown(){
        System.out.println("Deleting downloaded templates ...");
        //TODO : Actual deletion of pdfs
    }
}
