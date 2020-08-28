package com.pankaj.myfancyppdfinvoices.springboot.web;

import com.pankaj.myfancyppdfinvoices.springboot.dto.InvoiceDto;
import com.pankaj.myfancyppdfinvoices.springboot.model.Invoice;
import com.pankaj.myfancyppdfinvoices.springboot.service.InvoiceService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class InvoicesController {
    private final InvoiceService invoiceService;

    public InvoicesController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/invoices")
    public List<Invoice> invoices(){
        return invoiceService.findAll();
    }

    @PostMapping("/invoices")
    public Invoice createInvoice(@Valid @RequestBody InvoiceDto invoiceDto){
        return invoiceService.create(invoiceDto.getUserId(),invoiceDto.getAmount());
    }
}
