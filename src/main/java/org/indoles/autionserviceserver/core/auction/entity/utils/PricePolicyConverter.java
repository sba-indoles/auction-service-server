package org.indoles.autionserviceserver.core.auction.entity.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.indoles.autionserviceserver.core.auction.domain.PricePolicy;
import org.indoles.autionserviceserver.core.auction.entity.enums.PricePolicyType;
import org.indoles.autionserviceserver.core.auction.entity.exception.AuctionException;

import java.io.IOException;


import org.indoles.autionserviceserver.core.auction.entity.exception.AuctionExceptionCode;

@Converter
public class PricePolicyConverter implements AttributeConverter<PricePolicy, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(PricePolicy pricePolicy) {
        if (pricePolicy == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(pricePolicy);
        } catch (IOException e) {
            throw new AuctionException(AuctionExceptionCode.CONVERT_TO_STRING_ERROR, e);
        }
    }

    @Override
    public PricePolicy convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            JsonNode jsonNode = objectMapper.readTree(dbData);
            PricePolicyType type = PricePolicyType.valueOf(jsonNode.get("type").asText());

            return switch (type) {
                case PERCENTAGE -> objectMapper.treeToValue(jsonNode, PercentagePricePolicy.class);
                case CONSTANT -> objectMapper.treeToValue(jsonNode, ConstantPricePolicy.class);
                default -> throw new AuctionException(AuctionExceptionCode.CONVERT_TO_INVALID_TYPE);
            };

        } catch (IOException e) {
            throw new AuctionException(AuctionExceptionCode.CONVERT_TO_FAILED_PRICE_POLICY_TYPE, e);
        } catch (IllegalArgumentException e) {
            throw new AuctionException(AuctionExceptionCode.CONVERT_TO_INVALID_PRICE_POLICY_TYPE);
        }
        return null;
    }
}
