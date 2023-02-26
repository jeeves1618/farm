package org.farmfresh.RESTEndPoints.Domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@ToString
public class PackPrice {
    private Integer menuItemPackPrice;

    public PackPrice(Integer menuItemPackPrice) {
        this.menuItemPackPrice = menuItemPackPrice;
    }
}
