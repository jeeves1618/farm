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
@IdClass(CartId.class)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cart_id")
    private int cartId;
    @Id
    @Column(name = "customer_id")
    private String customerId;
    @Id
    @Column(name = "menu_item_id")
    private int menuItemId;
    @Id
    @Column(name = "pricing_id")
    private int pricingId;
    @Column(name = "menu_item_count")
    private Integer menuItemCount;
    @Column(name = "pack_size")
    private String packSize;
    @Column(name = "menu_item_pack_price")
    private Integer menuItemPackPrice;
    @Column(name = "customer_discount_rate")
    private String customerDiscountRate;
    @Column(name = "cart_status")
    private Integer cartStatus;
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
}
