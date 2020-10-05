package com.example.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@EnableBinding(Source.class)
public class ProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }
}

@RestController
@RequiredArgsConstructor
class GreetingRestController{
    private final Source source;
    @GetMapping("/send/{name}")
    boolean send(){
        MessageChannel output = this.source.output();
        Message<String> build = MessageBuilder.withPayload("Hello World!").build();
        return output.send(build);

    }
}
