package com.damaru.store;

import com.damaru.store.controller.MessagingController;
import com.damaru.store.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
//import com.damaru.store

@SpringBootApplication
@ComponentScan("com.damaru")
public class StoreApplication {

    public static final Logger log = LoggerFactory.getLogger(StoreApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(StoreApplication.class, args);
    }
}
