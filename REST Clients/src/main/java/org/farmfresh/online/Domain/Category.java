package org.farmfresh.online.Domain;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.Date;


@Data
@Component
public class Category {

    private int categoryId;
    private String menuItemCategory;
    private String menuItemSubCategory;
    private String menuImageFileName;
    private Integer menuItemCount;
    private Date dateCreated;
    private Date dateUpdated;
    private String userCreated;
    private String userUpdated;
}
