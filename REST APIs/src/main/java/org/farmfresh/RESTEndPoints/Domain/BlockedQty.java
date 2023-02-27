package org.farmfresh.RESTEndPoints.Domain;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class BlockedQty {
    private Double blockedQuantity;
}