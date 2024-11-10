package org.indoles.autionserviceserver.core.member.domain;

import lombok.Getter;
import org.indoles.autionserviceserver.core.member.domain.validate.ValidatePoint;

@Getter
public class Point {

    private Long amount;
    private final ValidatePoint validatePoint = new ValidatePoint();

    public Point(Long amount) {
        validatePoint.validatePositiveAmount(amount);
        this.amount = amount;
    }

    public void minus(Long minusAmount) {
        validatePoint.validatePositiveAmount(minusAmount);
        validatePoint.validateMinusPoint(amount, minusAmount);
        amount -= minusAmount;
    }

    public void plus(Long price) {
        validatePoint.validatePositiveAmount(price);
        validatePoint.validatePlusPoint(amount, price);
        amount += price;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Point point = (Point) o;
        return amount != null && amount.equals(point.amount);
    }

    public Long getValue() {
        return amount;
    }
}
