package com.example.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.integration.jdbc.lock.LockRepository;
import org.springframework.integration.support.leader.LockRegistryLeaderInitiator;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@SpringBootApplication
public class IntegrationApplication {

    @Bean
    DefaultLockRepository defaultLockRepository(DataSource dataSource){
        return new DefaultLockRepository(dataSource);
    }

    @Bean
    JdbcLockRegistry lockRegistry(LockRepository repository){
        return new JdbcLockRegistry(repository);
    }

    @Bean
    LockRegistryLeaderInitiator lockRegistryLeaderInitiator(LockRegistry registry){
        return new LockRegistryLeaderInitiator(registry);
    }
    public static void main(String[] args) {
        SpringApplication.run(IntegrationApplication.class, args);
    }
    @RestController
    @RequiredArgsConstructor
    class FileRestController{
        private final LockRegistry registry;
        private final FileRepository repository;
        private final String key = getClass().getName();

        @GetMapping("{name}/{wait}")
        String write(@PathVariable String name,@PathVariable Integer wait) throws Exception{
            Lock lock = this.registry.obtain(this.key);

            try {
                if (lock.tryLock(5, TimeUnit.SECONDS)) {
                    this.repository.update("Hello " + name + "@ " + Instant.now() + "!");
                    Thread.sleep(wait);

                }
            } finally {
                lock.unlock();

            }
            this.repository.read();

    }

    @Log4j2
    @Repository
    class FileRepository{
        private final File file;

        public FileRepository(@Value("$file-repository.file:file://${user.home}/Desktop/synchronized-file}") File file) {
            this.file = file;
            log.info("going to open " + this.file.getAbsolutePath() + '.');
        }

        String read() throws Exception{
            try(var r= new BufferedReader(
                    new FileReader(this.file))){
                return FileCopyUtils.copyToString(r);

            }
        }

        void update(String msg) throws Exception{
            try(var w= new BufferedWriter(new FileWriter(this.file))) {
                w.write(msg);
            }
        }

    }

}
