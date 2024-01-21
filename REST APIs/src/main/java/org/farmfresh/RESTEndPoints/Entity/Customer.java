package org.farmfresh.RESTEndPoints.Entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
@Component
@Table(name = "customer_table")
@EntityListeners(AuditingEntityListener.class)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "customer_id")
    private int customerId;
    @Column(name = "customer_mobile")
    private String customerMobile;
    @Column(name = "customer_billing_address")
    private String customerBillingAddress;
    @Column(name = "customer_email")
    private String customerEmail;
    @Column(name = "customer_discount_rate")
    private Double customerDiscountRate;
    @Column(name = "customer_user_id")
    private String customerUserId;
    @Column(name = "customer_credential")
    private String customerCredential;
    @Column(name = "customer_balance")
    private Double customerBalance;
    @Column(name = "date_created")
    private Date dateCreated;
    @Column(name = "date_updated")
    private Date dateUpdated;
    @CreatedBy
    @Column(name = "user_created")
    private String userCreated;
    @LastModifiedBy
    @Column(name = "user_updated")
    private String userUpdated;
}
