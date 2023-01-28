package org.farmfresh.RESTEndPoints.Domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@ToString
public class CatSummary {

    private String menuItemCategory;
    private String menuItemSubCategory;
    private long menuItemCount;

    public CatSummary(String menuItemCategory, String menuItemSubCategory, long menuItemCount) {
        this.menuItemCategory = menuItemCategory;
        this.menuItemSubCategory = menuItemSubCategory;
        this.menuItemCount = menuItemCount;
    }
}
