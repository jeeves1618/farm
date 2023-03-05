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
@Table(name = "cart_table")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cart_id")
    private int cartId;
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
    @Column(name = "menu_item_total_price")
    private Integer menuItemTotalPrice;
    @Column(name = "cart_status")
    private String cartStatus;
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
    @Transient
    private String menuItemName;
    @Transient
    private String menuItemCategory;
    @Transient
    private String menuItemSubCategory;
    @Transient
    private String menuItemDescription;
    @Transient
    private String menuItemPriceFmtd;
    @Transient
    private String menuItemTotalPriceFmtd;
    @Transient
    private String menuImageFileName;
    @Transient
    private int cartTotal;
    @Transient
    private String cartTotalFmtd;
}
