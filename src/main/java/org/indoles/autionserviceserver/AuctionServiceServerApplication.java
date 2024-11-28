package org.indoles.autionserviceserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "org.indoles.autionserviceserver.core.auction.utils")
public class AuctionServiceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionServiceServerApplication.class, args);
    }
}
