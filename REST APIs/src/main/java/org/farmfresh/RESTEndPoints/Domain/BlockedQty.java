package org.farmfresh.RESTEndPoints.Domain;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@ToString
public class BlockedQty {
    private int menuItemId;
    private double blockedQuantity;

    public BlockedQty(int menuItemId, double blockedQuantity) {
        this.menuItemId = menuItemId;
        this.blockedQuantity = blockedQuantity;
    }
}