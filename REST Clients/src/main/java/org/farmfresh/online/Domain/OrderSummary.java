package org.farmfresh.online.Domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
@Data
@NoArgsConstructor
@ToString
public class OrderSummary {

    private int orderSummaryId;
    private int displayOrderId;
    private String customerId;
    private Integer orderItemCount;
    private Integer totalOrderValue;
    private Integer youSaved;
    private String orderStatus;
    private String customerShippingAddress;
    private Date dateCreated;
    private Date dateUpdated;
    private String userCreated;
    private String userUpdated;
    private String customUrl;
}
