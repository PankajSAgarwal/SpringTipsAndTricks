package com.pankaj.myfancypdfinvoices.springboot.repository;

import com.pankaj.myfancyppdfinvoices.springboot.model.Invoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends CrudRepository<Invoice,String> {

}
