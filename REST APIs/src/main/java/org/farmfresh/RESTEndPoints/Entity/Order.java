package org.farmfresh.RESTEndPoints.Entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
@Component
@Table(name = "order_table")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id")
    private int orderId;
    @Column(name = "customer_id")
    private String customerId;
    @Column(name = "menu_item_id")
    private int menuItemId;
    @Column(name = "pricing_id")
    private int pricingId;
    @Column(name = "menu_item_count")
    private Integer menuItemCount;
    @Column(name = "pack_size")
    private String packSize;
    @Column(name = "menu_item_pack_price")
    private Integer menuItemPackPrice;
    @Column(name = "customer_discount_rate")
    private Double customerDiscountRate;
    @Column(name = "order_status")
    private String orderStatus;
    @Column(name = "customer_shipping_address")
    private String customerShippingAddress;
    @CreatedDate
    @Column(name = "date_created")
    private Date dateCreated;
    @LastModifiedDate
    @Column(name = "date_updated")
    private Date dateUpdated;
    @Column(name = "user_created")
    private String userCreated;
    @Column(name = "user_updated")
    private String userUpdated;
    @Column(name = "display_order_id")
    private int displayOrderId;
    @Transient
    private String orderItemName;
    @Transient
    private String orderItemCategory;
    @Transient
    private String orderItemSubCategory;
    @Transient
    private String orderItemDescription;
    @Transient
    private String orderItemPriceFmtd;
    @Transient
    private String orderItemTotalPriceFmtd;
    @Transient
    private String orderImageFileName;
    @Transient
    private int orderTotal;
    @Transient
    private String orderTotalFmtd;
}
