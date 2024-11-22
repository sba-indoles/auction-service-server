package org.indoles.autionserviceserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AuctionServiceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionServiceServerApplication.class, args);
    }
}
