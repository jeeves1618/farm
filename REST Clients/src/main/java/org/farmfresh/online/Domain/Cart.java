package org.farmfresh.online.Domain;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Data
@Component
public class Cart {
    private int cartId;
    private String customerId;
    private int menuItemId;
    private int pricingId;
    private Integer menuItemCount;
    private String packSize;
    private Integer menuItemPackPrice;
    private String customerDiscountRate;
    private Integer cartStatus;
    private Date dateCreated;
    private Date dateUpdated;
    private String userCreated;
    private String userUpdated;
}
