package org.indoles.autionserviceserver.core.context;

import org.indoles.autionserviceserver.core.auction.infra.AuctionCoreRepository;
import org.indoles.autionserviceserver.core.auction.service.BuyerService;
import org.indoles.autionserviceserver.core.auction.service.SellerService;
import org.indoles.autionserviceserver.core.auction.utils.MemberFeignClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class ServiceTest {

    @Autowired
    protected DatabaseCleaner databaseCleaner;

    @Autowired
    protected AuctionCoreRepository auctionCoreRepository;

    @Autowired
    protected BuyerService buyerService;

    @Autowired
    protected SellerService sellerService;

    @Mock
    protected MemberFeignClient memberFeignClient;

    protected LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.clear();
    }
}
