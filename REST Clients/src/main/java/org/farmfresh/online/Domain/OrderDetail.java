package org.farmfresh.online.Domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Data
@NoArgsConstructor
@ToString
public class OrderDetail {

    private int orderId;
    private Date dateCreated;
    private Date dateUpdated;
    private Double customerDiscountRate;
    private int displayOrderId;
    private int menuItemId;
    private int pricingId;
    private Integer menuItemCount;
    private Integer menuItemPackPrice;
    private String customerId;
    private String customerShippingAddress;
    private String orderStatus;
    private String packSize;
    private String userCreated;
    private String userUpdated;
    private String orderItemName;
    private String orderItemCategory;
    private String orderItemSubCategory;
    private String orderItemDescription;
    private String orderItemPriceFmtd;
    private String orderItemTotalPriceFmtd;
    private String orderImageFileName;
    private int orderTotal;
    private String orderTotalFmtd;

}
