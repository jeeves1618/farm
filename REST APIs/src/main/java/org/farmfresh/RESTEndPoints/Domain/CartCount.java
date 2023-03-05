package org.farmfresh.RESTEndPoints.Domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@ToString
public class CartCount {

    private String customerId;
    private long cartCount;

    public CartCount(String customerId, long cartCount) {
        this.customerId = customerId;
        this.cartCount = cartCount;
    }
}
