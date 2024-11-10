package org.indoles.autionserviceserver.core.auction.entity.exception;

import org.indoles.autionserviceserver.global.exception.BusinessException;
import org.indoles.autionserviceserver.global.exception.ExceptionCode;

public class AuctionException extends BusinessException {

    public AuctionException(ExceptionCode exceptionCode, Object... args) {
        super(exceptionCode, args);
    }
}
