package org.farmfresh.online.Domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class LandingIcon {

    private String title;
    private String subTitle;
    private String imageFileName;
    private String customUrl;

    public LandingIcon(String title, String subTitle, String imageFileName, String customUrl) {
        this.title = title;
        this.subTitle = subTitle;
        this.imageFileName = imageFileName;
        this.customUrl = customUrl;
    }
}
