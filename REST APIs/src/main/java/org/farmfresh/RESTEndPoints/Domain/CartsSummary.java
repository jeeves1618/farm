package org.farmfresh.RESTEndPoints.Domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@ToString
public class CartsSummary {

    private int menuItemId;
    private String packSize;
    private long menuItemCount;

    public CartsSummary(int menuItemId, String packSize, long menuItemCount) {
        this.menuItemId = menuItemId;
        this.packSize = packSize;
        this.menuItemCount = menuItemCount;
    }
}
