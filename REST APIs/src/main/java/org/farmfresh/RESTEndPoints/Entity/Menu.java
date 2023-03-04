package org.farmfresh.RESTEndPoints.Entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Data
@Component
@Table(name = "menu_item_table")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "menu_item_id")
    private int menuItemId;
    @Column(name = "menu_item_name")
    private String menuItemName;
    @Column(name = "menu_item_category")
    private String menuItemCategory;
    @Column(name = "menu_item_sub_category")
    private String menuItemSubCategory;
    @Column(name = "menu_item_description")
    private String menuItemDescription;
    @Column(name = "menu_today_special_ind")
    private String menuTodaySpecialInd;
    @Column(name = "menu_best_seller_ind")
    private String menuBestSellerInd;
    @Column(name = "menu_image_file_name")
    private String menuImageFileName;
    @Column(name = "menu_availability_ind")
    private String menuAvailabilityInd;
    @Column(name = "primary_in_categoty")
    private String primaryInCategory;
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
    @Column(name = "available_qty")
    private Double availableQty;
    @Column(name = "unit_of_measure")
    private String unitOfMeasure;
    @Transient
    private String unitToUse;
    @Transient
    private Double blockedQty;
    @Transient
    private Double freeQty;
    @Transient
    private List<Pricing> pricingList;
    @Transient
    private Long menuItemInCartCount;
}
