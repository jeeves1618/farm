package org.farmfresh.online.Domain;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Data
@Component
public class Customer {

    private int customerId;
    private String customerMobile;
    private String customerBillingAddress;
    private String customerEmail;
    private Double customerDiscountRate;
    private String customerUserId;
    private String customerCredential;
    private Double customerBalance;
    private String customerBalanceFmtd;
    private Date dateCreated;
    private Date dateUpdated;
    private String userCreated;
    private String userUpdated;
}
