package org.farmfresh.RESTEndPoints.Domain;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class AddToCart {
    private String customerId;
    private int menuItemId;
    private Integer menuItemCount;
    private String packSize;
    private int pricingId;
}