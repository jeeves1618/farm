package org.farmfresh.online.Domain;

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
    private String itemName;

    public CartsSummary(int menuItemId, String packSize, long menuItemCount, String itemName) {
        this.menuItemId = menuItemId;
        this.packSize = packSize;
        this.menuItemCount = menuItemCount;
        this.itemName = itemName;
    }
}
