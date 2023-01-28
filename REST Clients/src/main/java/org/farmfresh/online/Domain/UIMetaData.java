package org.farmfresh.online.Domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class UIMetaData {

    private String titleText;
    private String enableButtonIndicator;
    private String typeOfStatement;
    private Integer journalsKey;
}
