package org.farmfresh.RESTEndPoints.Domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class HomeMetaData {

    private String titleText;
    private String telephoneNumber;
    private String telephoneNumberText;
    private String centerCarousalHeader;
    private String centerCarousalLine1;
    private String centerCarousalLine2;
    private String centerCarousalLine3;
    private String centerCarousalLine4;
    private String tileHeader1;
    private String tileHeader2;
    private String tileHeader3;
}
