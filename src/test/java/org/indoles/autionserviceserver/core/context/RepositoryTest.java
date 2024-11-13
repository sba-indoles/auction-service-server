package org.indoles.autionserviceserver.core.context;


import org.indoles.autionserviceserver.core.auction.infra.AuctionCoreRepository;
import org.indoles.autionserviceserver.core.auction.infra.AuctionJpaRepository;
import org.indoles.autionserviceserver.global.config.JpaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import({JpaConfig.class, AuctionCoreRepository.class})
@DataJpaTest
public abstract class RepositoryTest {

    @Autowired
    public AuctionJpaRepository auctionJpaRepository;

}
