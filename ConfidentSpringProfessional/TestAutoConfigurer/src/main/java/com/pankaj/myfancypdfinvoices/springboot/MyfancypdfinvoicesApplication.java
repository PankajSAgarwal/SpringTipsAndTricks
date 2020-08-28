package com.pankaj.myfancypdfinvoices.springboot;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class MyfancypdfinvoicesApplication {
    @Bean
    public InitializingBean runner(DataSource dataSource){
        return ()->{
            System.out.println("This is the datasource this Spring boot project is using: " + dataSource);
        };
    }

    public static void main(String[] args) {

        SpringApplication.run(MyfancypdfinvoicesApplication.class, args);
    }


}
