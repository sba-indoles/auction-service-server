package org.indoles.autionserviceserver.core.auction.entity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.indoles.autionserviceserver.global.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class AuctionExceptionCode implements ExceptionCode {



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
