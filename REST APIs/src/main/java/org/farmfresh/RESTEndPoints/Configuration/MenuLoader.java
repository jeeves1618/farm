package org.farmfresh.RESTEndPoints.Configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.farmfresh.RESTEndPoints.Domain.CatSummary;
import org.farmfresh.RESTEndPoints.Entity.Category;
import org.farmfresh.RESTEndPoints.Entity.Menu;
import org.farmfresh.RESTEndPoints.Entity.Pricing;
import org.farmfresh.RESTEndPoints.Repo.CartRepo;
import org.farmfresh.RESTEndPoints.Repo.CategoryRepo;
import org.farmfresh.RESTEndPoints.Repo.MenuRepo;
import org.farmfresh.RESTEndPoints.Repo.PricingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("h2")
@Slf4j
public class MenuLoader implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    Menu menu;

    @Autowired
    MenuRepo menuRepo;

    @Autowired
    Pricing pricing;

    @Autowired
    PricingRepo pricingRepo;

    @Autowired
    Category category;

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    CartRepo cartRepo;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ObjectMapper objectMapper = new ObjectMapper();
        Integer intId = 0;
        ArrayList<Menu> menuList = new ArrayList<>();
        List<Menu> menuItems = new ArrayList<>();
        List<Pricing> pricingList = new ArrayList<>();
        long millis=System.currentTimeMillis();
        java.sql.Date date=new java.sql.Date(millis);
        try {
            menuList = objectMapper.readValue(new URL("https://raw.githubusercontent.com/jeeves1618/HTML-CSS-JS/main/static/item.json"), ArrayList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(menuList.size());
        for (int i = 0; i < menuList.size(); i++){
            menu = new Menu();
            System.out.println(menuList.get(i));
            String[] keyValuePairs = String.valueOf(menuList.get(i)).split(",");
            for(int j = 0; j < keyValuePairs.length; j++){
                String[] tokens= keyValuePairs[j].split("=");
                pricing.setDateCreated(date);
                pricing.setDateUpdated(date);
                pricing.setUserCreated("Farm Fresh");
                pricing.setUserUpdated("Organic");
                pricing.setMenuPackAvailabilityInd("Y");
                if (tokens.length == 1)
                    System.out.println("One" + tokens[0]);
                if (tokens.length == 3) {
                    System.out.println(keyValuePairs[j] + " and " + menuList.get(i));
                    System.out.println("Three" + tokens[0] + "," + tokens[1] + "," + tokens[2]);
                    pricing.setPackSize(tokens[1].substring(1));
                    pricing.setMenuItemPackPrice(Integer.valueOf(tokens[2]));
                    pricingList.add(pricing);
                    pricing = new Pricing();
                }

                if (tokens.length == 2) {
                    System.out.println("Two" + tokens[0] + "," + tokens[1]);
                    String token = tokens[0].replace(" ","");
                    if (checkIfNumeric(token)){
                        pricing.setPackSize(token);
                        pricing.setMenuItemPackPrice(Integer.valueOf(tokens[1].replace("}", "")));
                        System.out.println("setting up numeric value for token " + token + " and " + pricing.getMenuItemPackPrice());
                        pricingList.add(pricing);
                        pricing = new Pricing();
                    }
                    else {
                        switch (tokens[0]) {
                            case " name":
                                menu.setMenuItemName(tokens[1]);
                                break;
                            case " description":
                                menu.setMenuItemDescription(tokens[1]);
                                break;
                            case " category":
                                menu.setMenuItemSubCategory(tokens[1]);
                                menu.setMenuItemCategory(tokens[1]);
                                break;
                            case "{id":
                                menu.setMenuItemCategory(tokens[1]);
                                break;
                            case " image":
                                menu.setMenuImageFileName(tokens[1]);
                                break;
                            case " available":
                                menu.setAvailableQty(Double.valueOf(tokens[1]));
                                break;
                            case " unitOfMeasure":
                                menu.setUnitOfMeasure(tokens[1].substring(0, tokens[1].length() - 1));
                                break;
                            default:
                                menu.setMenuAvailabilityInd("Y");
                                menu.setMenuTodaySpecialInd("Y");
                                menu.setMenuBestSellerInd("Y");
                                menu.setDateCreated(date);
                                menu.setDateUpdated(date);
                                menu.setUserCreated("Farm Fresh");
                                menu.setUserUpdated("Organic");
                                menu.setPrimaryInCategory("N");
                        }
                    }
                }
            }
            menu.setMenuAvailabilityInd("Y");
            menu.setMenuTodaySpecialInd("Y");
            menu.setMenuBestSellerInd("Y");
            menu.setDateCreated(date);
            menu.setDateUpdated(date);
            menu.setUserCreated("Farm Fresh");
            menu.setUserUpdated("Organic");
            menu.setPrimaryInCategory("N");
            menuItems.add(menu);
            menuRepo.save(menu);
            System.out.println("ID After: " + menu.getMenuItemId());
            for (int prices = 0; prices < pricingList.size(); prices++){
                pricingList.get(prices).setMenuItemId(menu.getMenuItemId());
            }
            pricingRepo.saveAll(pricingList);
            pricingList.clear();
        }

        List<CatSummary> catSummaryList = menuRepo.findCountByCategories();
        try {
            Class catSummaryClass = Class.forName("org.farmfresh.RESTEndPoints.Domain.CatSummary");
            for(CatSummary catSummary:catSummaryList){
                System.out.println((catSummary));
                category.setMenuItemCategory(catSummary.getMenuItemCategory());
                category.setMenuItemSubCategory(catSummary.getMenuItemSubCategory()+" (" + catSummary.getMenuItemCount() + ")");
                category.setMenuItemCount((int) catSummary.getMenuItemCount());
                category.setDateCreated(date);
                category.setDateUpdated(date);
                category.setUserCreated("Farm Fresh");
                category.setUserUpdated("Organic");
                category.setMenuImageFileName("../" + menuRepo.findOneByMenuItemSubCategory(catSummary.getMenuItemSubCategory()).getMenuImageFileName());
                categoryRepo.save(category);
                category = new Category();

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(cartRepo.findAll());
        //menuRepo.saveAll(menuItems);
    }

    boolean checkIfNumeric(String input) {
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
}
