package com.pankaj.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pankaj.model.Invoice;
import com.pankaj.myfancypdfinvoices.context.MyFancyPdfInvoicesApplicationConfiguration;
import com.pankaj.service.InvoiceService;
import com.pankaj.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MyFancyPdfInvoicesServlet extends HttpServlet {
    private UserService userService;
    private ObjectMapper objectMapper;
    private InvoiceService invoiceService;

    @Override
    public void init() throws ServletException {
        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(MyFancyPdfInvoicesApplicationConfiguration.class);
        ctx.registerShutdownHook();
        this.userService = ctx.getBean(UserService.class);
        this.objectMapper = ctx.getBean(ObjectMapper.class);
        this.invoiceService = ctx.getBean(InvoiceService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getRequestURI().equalsIgnoreCase("/")) {

            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().print("<html>\n" +
                    "<body>\n" +
                    "<h1>Hello World</h1>\n" +
                    "<p>This is my very first, embedded Tomcat, HTML Page!</p>\n" +
                    "</body>\n" +
                    "</html>");
        }else if(req.getRequestURI().equalsIgnoreCase("/invoices")){
            resp.setContentType("application/json;charset=UTF-8");
            List<Invoice> invoices = invoiceService.findAll();
            resp.getWriter().print(objectMapper.writeValueAsString(invoices));
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getRequestURI().equalsIgnoreCase("/invoices")){
            String userId = req.getParameter("user_id");
            Integer amount = Integer.valueOf(req.getParameter("amount"));

            Invoice invoice = invoiceService.create(userId,amount);
            resp.setContentType("application/json;charset=UTF-8");
            String json = objectMapper.writeValueAsString(invoice);
            resp.getWriter().print(json);
        }else{
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

    }
}
