package org.farmfresh.RESTEndPoints.Domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@ToString
public class CartSummary {

    private int menuItemId;
    private long menuItemCount;

    public CartSummary(int menuItemId, long menuItemCount) {
        this.menuItemId = menuItemId;
        this.menuItemCount = menuItemCount;
    }
}
