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
@Table(name = "pricing_table")
public class Pricing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pricing_id")
    private int pricingId;
    @Column(name = "menu_item_id")
    private int menuItemId;
    @Column(name = "pack_size")
    private String packSize;
    @Column(name = "menu_item_pack_price")
    private Integer menuItemPackPrice;
    @Column(name = "menu_item_pack_price_fmtd")
    private String menuItemPackPriceFmtd;
    @Column(name = "menu_pack_availability_ind")
    private String menuPackAvailabilityInd;
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
