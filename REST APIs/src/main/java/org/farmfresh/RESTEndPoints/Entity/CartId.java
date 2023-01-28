package org.farmfresh.RESTEndPoints.Entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
public class CartId implements Serializable {
    private int cartId;
    private String customerId;
    private int menuItemId;
    private int pricingId;
}
