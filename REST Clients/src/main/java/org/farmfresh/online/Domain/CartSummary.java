package org.farmfresh.online.Domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@ToString
public class CartSummary {

    private String menuItemId;
    private long menuItemCount;

    public CartSummary(String menuItemId, long menuItemCount) {
        this.menuItemId = menuItemId;
        this.menuItemCount = menuItemCount;
    }
}
