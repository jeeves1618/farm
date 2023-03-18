package org.farmfresh.RESTEndPoints.Domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@ToString
public class CartCustItemCount {

    private String customerId;
    private int menuItemId;
    private long cartCount;

    public CartCustItemCount(String customerId, int menuItemId, long cartCount) {
        this.customerId = customerId;
        this.menuItemId = menuItemId;
        this.cartCount = cartCount;
    }
}
