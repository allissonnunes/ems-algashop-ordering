package br.dev.allissonnunes.algashop.ordering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneOffset;
import java.util.TimeZone;

@SpringBootApplication
public class OrderingApplication {

    static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
        SpringApplication.run(OrderingApplication.class, args);
    }

}
