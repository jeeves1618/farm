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
@Table(name = "order_summary_table")
public class OrderSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_summary_id")
    private int orderSummaryId;
    @Column(name = "display_order_id")
    private int displayOrderId;
    @Column(name = "customer_id")
    private String customerId;
    @Column(name = "order_item_count")
    private Integer orderItemCount;
    @Column(name = "total_order_value")
    private Integer totalOrderValue;
    @Column(name = "you_saved")
    private Integer youSaved;
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
    @Transient
    private String customUrl;
}
