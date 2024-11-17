package org.indoles.autionserviceserver.core.infra;

import org.indoles.autionserviceserver.core.auction.dto.Request.AuctionSearchConditionRequest;
import org.indoles.autionserviceserver.core.auction.dto.Request.SellerAuctionSearchConditionRequest;
import org.indoles.autionserviceserver.core.auction.entity.AuctionEntity;
import org.indoles.autionserviceserver.core.context.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AuctionQueryDslRepositoryTest extends RepositoryTest {

    @Nested
    class AuctionSearch {

        @Test
        @DisplayName("사용자는 size 갯수 만큼 거래 내역을 조회할 수 있다.")
        void auctionSearch_Success() {
            // given
            int offset = 0;
            int size = 3;
            var condition = new AuctionSearchConditionRequest(offset, size);

            for (int i = 0; i < size + 3; i++) {
                auctionJpaRepository.save(AuctionEntity.builder()
                        .build());
            }

            // when
            List<AuctionEntity> result = auctionJpaRepository.findAllBy(condition);

            // then
            assertThat(result.size()).isEqualTo(size);
        }


        @Test
        @DisplayName("사용자는 offset을 적용하여 데이터를 조회할 수 있다.")
        void auctionSearch_WithOffset_Success() {
            // given
            int offset = 0;
            int size = 3;
            var condition = new AuctionSearchConditionRequest(offset, size);

            for (int i = 0; i < size; i++) {
                auctionJpaRepository.save(AuctionEntity.builder()
                        .build());
            }

            // when
            List<AuctionEntity> result = auctionJpaRepository.findAllBy(condition);
            Long baseId = result.get(2).getId();

            // then
            assertThat(result)
                    .map(AuctionEntity::getId)
                    .containsExactly(baseId + 2L, baseId + 1L, baseId);
        }
    }

    @Nested
    class SellerSearch {

        @Test
        @DisplayName("판매자는 size 갯수 만큼 거래 내역을 조회할 수 있다.")
        void sellerSearch_Success() {
            // given
            long sellerId = 1L;
            int offset = 0;
            int size = 3;
            var condition = new SellerAuctionSearchConditionRequest(sellerId, offset, size);

            for (int i = 0; i < size + 3; i++) {
                auctionJpaRepository.save(AuctionEntity.builder()
                        .sellerId(sellerId)
                        .build());
            }

            // when
            List<AuctionEntity> result = auctionJpaRepository.findAllBy(condition);

            // then
            assertThat(result.size()).isEqualTo(size);
        }

        @Test
        @DisplayName("판매자는 offset을 적용하여 데이터를 조회할 수 있다.")
        void sellerSearch_WithOffset_Success() {
            long sellerId = 1L;
            int offset = 0;
            int size = 3;
            var condition = new SellerAuctionSearchConditionRequest(sellerId, offset, size);

            for (int i = 0; i < size + 5; i++) {
                auctionJpaRepository.save(AuctionEntity.builder()
                        .sellerId(sellerId)
                        .build());
            }

            // when
            List<AuctionEntity> result = auctionJpaRepository.findAllBy(condition);

            // then
            List<Long> expectedIds = result.stream()
                    .map(AuctionEntity::getId)
                    .sorted()
                    .toList();

            assertThat(expectedIds)
                    .containsExactly(expectedIds.get(0), expectedIds.get(0) + 1, expectedIds.get(0) + 2);
        }
    }
}
