package org.indoles.autionserviceserver.core.auction.infra;

import lombok.RequiredArgsConstructor;
import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.dto.Request.AuctionSearchConditionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Request.SellerAuctionSearchConditionRequest;
import org.indoles.autionserviceserver.core.auction.entity.AuctionEntity;
import org.indoles.autionserviceserver.global.util.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuctionCoreRepository implements AuctionRepository {

    private final AuctionJpaRepository auctionJpaRepository;

    @Override
    public Auction save(Auction auction) {
        AuctionEntity auctionEntity = Mapper.convertToAuctionEntity(auction);
        AuctionEntity saved = auctionJpaRepository.save(auctionEntity);
        return Mapper.convertToAuction(saved);
    }

    @Override
    public Optional<Auction> findById(Long id) {
        Optional<AuctionEntity> auction = auctionJpaRepository.findById(id);
        return auction.map(Mapper::convertToAuction);
    }

    public void deleteById(long id) {
        auctionJpaRepository.deleteById(id);
    }

    @Override
    public List<Auction> findAllBy(AuctionSearchConditionRequest condition) {
        List<AuctionEntity> entities = auctionJpaRepository.findAllBy(condition);
        return entities.stream()
                .map(Mapper::convertToAuction)
                .toList();
    }

    @Override
    public List<Auction> findAllBy(SellerAuctionSearchConditionRequest condition) {
        List<AuctionEntity> entities = auctionJpaRepository.findAllBy(condition);
        return entities.stream()
                .map(Mapper::convertToAuction)
                .toList();
    }

    @Override
    public Optional<Auction> findByIdForUpdate(long auctionId) {
        Optional<AuctionEntity> auction = auctionJpaRepository.findByIdForUpdate(auctionId);
        return auction.map(Mapper::convertToAuction);
    }
}
