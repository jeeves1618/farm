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
@Table(name = "category_table")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "category_id")
    private int categoryId;
    @Column(name = "menu_item_category")
    private String menuItemCategory;
    @Column(name = "menu_item_sub_category")
    private String menuItemSubCategory;
    @Column(name = "menu_image_file_name")
    private String menuImageFileName;
    @Column(name = "menu_item_count")
    private Integer menuItemCount;
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
