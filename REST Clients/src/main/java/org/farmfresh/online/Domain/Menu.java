package org.farmfresh.online.Domain;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.List;

@Data
@Component
public class Menu {

    private int menuItemId;
    private String menuItemName;
    private String menuItemCategory;
    private String menuItemSubCategory;
    private String menuItemDescription;
    private String menuTodaySpecialInd;
    private String menuBestSellerInd;
    private String menuImageFileName;
    private String menuAvailabilityInd;
    private String primaryInCategory;
    private Date dateCreated;
    private Date dateUpdated;
    private String userCreated;
    private String userUpdated;
    private Double availableQty;
    private String unitOfMeasure;
    private List<Pricing> pricingList;

    /*
    Available Quantity
     */
}
