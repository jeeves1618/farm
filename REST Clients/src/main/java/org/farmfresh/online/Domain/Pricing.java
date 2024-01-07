package org.farmfresh.online.Domain;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Data
@Component
public class Pricing {
    private int pricingId;
    private int menuItemId;
    private String packSize;
    private Integer menuItemPackPrice;
    private String menuItemPackPriceFmtd;
    private String menuPackAvailabilityInd;
    private Date dateCreated;
    private Date dateUpdated;
    private String userCreated;
    private String userUpdated;
    private String standardizedUnit;
}
