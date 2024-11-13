package org.indoles.autionserviceserver.core.infra;

import org.indoles.autionserviceserver.core.auction.domain.Auction;
import org.indoles.autionserviceserver.core.auction.domain.ConstantPricePolicy;
import org.indoles.autionserviceserver.core.auction.dto.AuctionSearchCondition;
import org.indoles.autionserviceserver.core.auction.dto.SellerAuctionSearchCondition;
import org.indoles.autionserviceserver.core.auction.entity.AuctionEntity;
import org.indoles.autionserviceserver.core.auction.infra.AuctionRepository;
import org.indoles.autionserviceserver.core.context.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AuctionCoreRepository extends RepositoryTest {

    @Autowired
    private AuctionRepository auctionRepository;

    @Nested
    class findByAuctionId_Method {

        @Test
        @DisplayName("경매의_id로_경매를_조회한다")
        void findByAuctionId_Success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Auction auction = Auction.builder()
                    .id(1L)
                    .sellerId(2L)
                    .productName("상품 이름")
                    .originPrice(1000L)
                    .currentPrice(1000L)
                    .originStock(100L)
                    .currentStock(100L)
                    .maximumPurchaseLimitCount(10L)
                    .pricePolicy(new ConstantPricePolicy(10L))
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now)
                    .finishedAt(now.plusHours(1))
                    .isShowStock(true)
                    .build();
            Auction saved = auctionRepository.save(auction);

            // when
            Auction foundAuction = auctionRepository.findById(saved.getId()).get();

            // then
            assertAll(
                    () -> assertThat(foundAuction.getId()).isEqualTo(saved.getId()),
                    () -> assertThat(foundAuction.getSellerId()).isEqualTo(saved.getSellerId()),
                    () -> assertThat(foundAuction.getProductName()).isEqualTo(saved.getProductName()),
                    () -> assertThat(foundAuction.getOriginPrice()).isEqualTo(saved.getOriginPrice()),
                    () -> assertThat(foundAuction.getCurrentPrice()).isEqualTo(saved.getCurrentPrice()),
                    () -> assertThat(foundAuction.getOriginStock()).isEqualTo(saved.getOriginStock()),
                    () -> assertThat(foundAuction.getCurrentStock()).isEqualTo(saved.getCurrentStock()),
                    () -> assertThat(foundAuction.getMaximumPurchaseLimitCount()).isEqualTo(
                            saved.getMaximumPurchaseLimitCount()),
                    () -> assertThat(foundAuction.getPricePolicy()).isEqualTo(saved.getPricePolicy()),
                    () -> assertThat(foundAuction.getVariationDuration()).isEqualTo(saved.getVariationDuration()),
                    () -> assertThat(foundAuction.getStartedAt()).isEqualTo(saved.getStartedAt()),
                    () -> assertThat(foundAuction.getFinishedAt()).isEqualTo(saved.getFinishedAt()),
                    () -> assertThat(foundAuction.isShowStock()).isEqualTo(saved.isShowStock())
            );
        }

        @Test
        @DisplayName("경매의_id에_해당하는_경매가_없으면_empty를_반환한다")
        void findByAuctionId_Fail() {
            // given
            Long notExistId = 1L;

            // when
            boolean isExist = auctionRepository.findById(notExistId).isPresent();

            // then
            assertThat(isExist).isFalse();
        }
    }

    @Nested
    class save_Auction_Method {

        @Test
        @DisplayName("경매_도메인_엔티티를_받으면_정상적으로_수행한다")
        void Transactional_save_Auction_Success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Auction auction = Auction.builder()
                    .sellerId(2L)
                    .productName("상품 이름")
                    .originPrice(1000L)
                    .currentPrice(1000L)
                    .originStock(100L)
                    .currentStock(100L)
                    .maximumPurchaseLimitCount(10L)
                    .pricePolicy(new ConstantPricePolicy(10L))
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now)
                    .finishedAt(now.plusHours(1))
                    .isShowStock(true)
                    .build();

            // when
            Auction save = auctionRepository.save(auction);

            // then
            assertAll(
                    () -> assertThat(save).isNotNull(),
                    () -> assertThat(save.getId()).isNotNull(),
                    () -> assertThat(save.getSellerId()).isEqualTo(2L),
                    () -> assertThat(save.getProductName()).isEqualTo("상품 이름"),
                    () -> assertThat(save.getOriginPrice()).isEqualTo(1000L),
                    () -> assertThat(save.getCurrentPrice()).isEqualTo(1000L),
                    () -> assertThat(save.getOriginStock()).isEqualTo(100L),
                    () -> assertThat(save.getCurrentStock()).isEqualTo(100L),
                    () -> assertThat(save.getMaximumPurchaseLimitCount()).isEqualTo(10L),
                    () -> assertThat(save.getPricePolicy()).isEqualTo(new ConstantPricePolicy(10L)),
                    () -> assertThat(save.getVariationDuration()).isEqualTo(Duration.ofMinutes(10L)),
                    () -> assertThat(save.getStartedAt()).isEqualTo(now),
                    () -> assertThat(save.getFinishedAt()).isEqualTo(now.plusHours(1)),
                    () -> assertThat(save.isShowStock()).isTrue()
            );

        }

        @Test
        @DisplayName("이미_등록된_경매 상품인_경우_정상적으로_수정한다")
        void Transactional_save_Auction_Update_Success() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Auction auction = Auction.builder()
                    .sellerId(2L)
                    .productName("상품 이름")
                    .originPrice(1000L)
                    .currentPrice(1000L)
                    .originStock(100L)
                    .currentStock(100L)
                    .maximumPurchaseLimitCount(10L)
                    .pricePolicy(new ConstantPricePolicy(10L))
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now)
                    .finishedAt(now.plusHours(1))
                    .isShowStock(true)
                    .build();
            Auction save = auctionRepository.save(auction);

            // when
            Auction newAuction = Auction.builder()
                    .id(save.getId())
                    .sellerId(2L)
                    .productName("멋지게 바뀐 이름")
                    .originPrice(5000L)
                    .currentPrice(5000L)
                    .originStock(500L)
                    .currentStock(500L)
                    .maximumPurchaseLimitCount(10L)
                    .pricePolicy(new ConstantPricePolicy(10L))
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now)
                    .finishedAt(now.plusHours(1))
                    .isShowStock(true)
                    .build();
            Auction newSave = auctionRepository.save(newAuction);

            // then
            assertAll(
                    () -> assertThat(newSave).isNotNull(),
                    () -> assertThat(newSave.getId()).isEqualTo(save.getId()), // ID should be the same
                    () -> assertThat(newSave.getSellerId()).isEqualTo(2L), // ID should be the same
                    () -> assertThat(newSave.getProductName()).isEqualTo("멋지게 바뀐 이름"),
                    () -> assertThat(newSave.getOriginPrice()).isEqualTo(5000L),
                    () -> assertThat(newSave.getCurrentPrice()).isEqualTo(5000L),
                    () -> assertThat(newSave.getOriginStock()).isEqualTo(500L),
                    () -> assertThat(newSave.getCurrentStock()).isEqualTo(500L),
                    () -> assertThat(newSave.getMaximumPurchaseLimitCount()).isEqualTo(10L),
                    () -> assertThat(newSave.getPricePolicy()).isEqualTo(new ConstantPricePolicy(10L)),
                    () -> assertThat(newSave.getVariationDuration()).isEqualTo(Duration.ofMinutes(10L)),
                    () -> assertThat(newSave.getStartedAt()).isEqualTo(now),
                    () -> assertThat(newSave.getFinishedAt()).isEqualTo(now.plusHours(1)),
                    () -> assertThat(newSave.isShowStock()).isTrue()
            );
        }

    }

    @Nested
    class findAllBy_BuyerAuctionSearchCondition_Method {

        @ParameterizedTest
        @CsvSource({
                "0, 2, 2",  // 첫 번째 페이지, 크기 2
                "1, 3, 3",  // 두 번째 페이지, 크기 3
                "2, 4, 4",  // 세 번째 페이지, 크기 4
                "0, 10, 10" // 첫 번째 페이지, 크기 10
        })
        @DisplayName("검색조건을_받으면_정상적으로_처리한다")
        void findAllBy_BuyerAuctionSearchCondition_Success(int offset, int size, int expectedSize) {
            // given
            createAuctions(10);
            AuctionSearchCondition condition = new AuctionSearchCondition(offset, size);

            // when
            List<Auction> auctions = auctionRepository.findAllBy(condition);

            // then
            assertThat(auctions).hasSize(expectedSize);

            Auction firstAuction = auctions.get(0);
            long expectedSellerId = 10 - offset;
            assertAll(
                    () -> assertThat(firstAuction.getSellerId()).isEqualTo(expectedSellerId),
                    () -> assertThat(firstAuction.getProductName()).isEqualTo("productName" + expectedSellerId),
                    () -> assertThat(firstAuction.getOriginPrice()).isEqualTo(10000L * expectedSellerId),
                    () -> assertThat(firstAuction.getCurrentPrice()).isEqualTo(10000L * expectedSellerId),
                    () -> assertThat(firstAuction.getOriginStock()).isEqualTo(100L * expectedSellerId),
                    () -> assertThat(firstAuction.getCurrentStock()).isEqualTo(100L * expectedSellerId),
                    () -> assertThat(firstAuction.getMaximumPurchaseLimitCount()).isEqualTo(expectedSellerId),
                    () -> assertThat(firstAuction.getPricePolicy()).isInstanceOf(ConstantPricePolicy.class),
                    () -> assertThat(firstAuction.getVariationDuration()).isEqualTo(Duration.ofMinutes(10L)),
                    () -> assertThat(firstAuction.isShowStock()).isTrue()
            );
        }

        @Test
        @DisplayName("조건에_해당하는_경매가_없으면_정상_반환한다")
        void findAllBy_BuyerAuctionSearchCondition_NoResult() {
            // given
            AuctionSearchCondition condition = new AuctionSearchCondition(0, 10);

            // when
            List<Auction> auctions = auctionRepository.findAllBy(condition);

            // then
            assertThat(auctions).isEmpty();
        }

    }

    private void createAuctions(int count) {
        List<AuctionEntity> auctions = new ArrayList<>();

        for (long i = 1; i <= count; i++) {
            LocalDateTime now = LocalDateTime.now().plusHours(i);
            AuctionEntity auction = AuctionEntity.builder()
                    .sellerId(i)
                    .productName("productName" + i)
                    .originPrice(10000L * i)
                    .currentPrice(10000L * i)
                    .originStock(100L * i)
                    .currentStock(100L * i)
                    .maximumPurchaseLimitCount(i)
                    .pricePolicy(new ConstantPricePolicy(1000L))
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now.minusMinutes(30))
                    .finishedAt(now.plusMinutes(30))
                    .isShowStock(true)
                    .build();
            auctions.add(auction);
        }

        auctionJpaRepository.saveAll(auctions);
    }

    @Nested
    class findAllBy_SellerAuctionSearchCondition_Method {

        @ParameterizedTest
        @CsvSource({
                "0, 2, 1",  // 첫 번째 페이지, 크기 2
                "0, 10, 1"  // 첫 번째 페이지, 크기 10
        })

        @DisplayName("검색조건을_받으면_정상적으로_처리한다")
        void findAllBy_SellerAuctionSearchCondition_Success(int offset, int size, int expectedSize) {
            // given
            long sellerId = 1L;
            createAuctions(10);
            SellerAuctionSearchCondition condition = new SellerAuctionSearchCondition(sellerId, offset, size);

            // when
            List<Auction> auctions = auctionRepository.findAllBy(condition);

            // then
            assertThat(auctions).hasSize(expectedSize);

            Auction firstAuction = auctions.get(0);
            long expectedSellerId = offset + 1;
            assertAll(
                    () -> assertThat(firstAuction.getSellerId()).isEqualTo(expectedSellerId),
                    () -> assertThat(firstAuction.getProductName()).isEqualTo("productName" + expectedSellerId),
                    () -> assertThat(firstAuction.getOriginPrice()).isEqualTo(10000L * expectedSellerId),
                    () -> assertThat(firstAuction.getCurrentPrice()).isEqualTo(10000L * expectedSellerId),
                    () -> assertThat(firstAuction.getOriginStock()).isEqualTo(100L * expectedSellerId),
                    () -> assertThat(firstAuction.getCurrentStock()).isEqualTo(100L * expectedSellerId),
                    () -> assertThat(firstAuction.getMaximumPurchaseLimitCount()).isEqualTo(expectedSellerId),
                    () -> assertThat(firstAuction.getPricePolicy()).isInstanceOf(ConstantPricePolicy.class),
                    () -> assertThat(firstAuction.getVariationDuration()).isEqualTo(Duration.ofMinutes(10L)),
                    () -> assertThat(firstAuction.isShowStock()).isTrue()
            );
        }

        @Test
        @DisplayName("조건에 해당하는 경매가 없으면 정상적으로 반환한다.")
        void findAllBy_SellerAuctionSearchCondition_NoResult() {
            // given
            long sellerId = 1L;
            SellerAuctionSearchCondition condition = new SellerAuctionSearchCondition(sellerId, 0, 10);

            // when
            List<Auction> auctions = auctionRepository.findAllBy(condition);

            // then
            assertThat(auctions).isEmpty();
        }

        @Nested
        class AuctionDeleteById_Method {

            @Test
            @DisplayName("경매_식별번호가_전달되면_정상적으로_삭제된다")
            void auctionDeleteById_Success() {
                // given
                AuctionEntity entity = AuctionEntity.builder().build();
                AuctionEntity savedAuction = auctionJpaRepository.save(entity);

                // when
                auctionRepository.deleteById(savedAuction.getId());

                // then
                List<AuctionEntity> all = auctionJpaRepository.findAll();
                assertThat(all).isEmpty();
            }

            @Test
            @DisplayName("존재하지 않는 경매 식별번호가 전달되면 정상적으로 무시된다")
            void auctionDeleteById_NonExistentId() {
                // given
                long nonExistentId = 1L;

                // when
                auctionRepository.deleteById(nonExistentId);

                // then
                List<AuctionEntity> all = auctionJpaRepository.findAll();
                assertThat(all).isEmpty();
            }
        }
    }
}
