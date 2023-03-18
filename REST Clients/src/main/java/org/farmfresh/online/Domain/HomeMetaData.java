package org.farmfresh.online.Domain;

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
    private String tileHeader4;
    private String tileHeader5;
    private String tileHeader6;
    private String categoryHeader;
    private String categoryMessage;
    private String cartHeader;
    private String cartSubHeader;
    private String loggedInUser;
    private String cartSummary;
    private String cartSubSummary;
    private String navRoleSensitiveLabel;
    private String routingUrl;
}
