package com.pankaj.springrestclient;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringRestClientApplication {

    private final RandomQuoteClient randomQuoteClient;

    public SpringRestClientApplication(RandomQuoteClient randomQuoteClient) {
        this.randomQuoteClient = randomQuoteClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringRestClientApplication.class, args);
    }

    // Uncomment this when u want to run the main application
    /*@Bean
    CommandLineRunner commandLineRunner(){
        return args -> System.out.println(randomQuoteClient.getRandomQuote());
    }*/

}
