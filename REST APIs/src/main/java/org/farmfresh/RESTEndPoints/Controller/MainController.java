package org.farmfresh.RESTEndPoints.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.farmfresh.RESTEndPoints.Entity.Category;
import org.farmfresh.RESTEndPoints.Entity.HomeData;
import org.farmfresh.RESTEndPoints.Entity.Menu;
import org.farmfresh.RESTEndPoints.Entity.Pricing;
import org.farmfresh.RESTEndPoints.Repo.CategoryRepo;
import org.farmfresh.RESTEndPoints.Repo.MenuRepo;
import org.farmfresh.RESTEndPoints.Repo.PricingRepo;
import org.farmfresh.RESTEndPoints.Service.HomeDataService;
import org.farmfresh.RESTEndPoints.Service.RupeeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping(path = "/farmfoods")
public class MainController {

    @Autowired
    HomeDataService homeDataService;

    @Autowired
    MenuRepo menuRepo;

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    PricingRepo pricingRepo;

    @Autowired
    RupeeFormatter rf;

    @GetMapping(path = "/home")
    public HomeData getHomePage(){
        return homeDataService.getHomePageData();
    }

    @GetMapping(path = "/category")
    public List<Category> getSweetCategories(@RequestParam String menuItemCategory) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        //menuItemCategory = "Dairy";
        return categoryRepo.findByMenuItemCategory(menuItemCategory);
    }

    @GetMapping(path = "/category/savouries")
    public List<Category> getSavouriesCategories(String menuItemCategory) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        menuItemCategory = "Savouries";
        return categoryRepo.findByMenuItemCategory(menuItemCategory);
    }

    @GetMapping(path = "/items/{menuItemSubCategory}")
    public List<Menu> getMenuItems(@PathVariable String menuItemSubCategory) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return menuRepo.findByMenuItemSubCategory(menuItemSubCategory);
    }

    @GetMapping(path = "/item/{menuItemId}")
    public Menu getMenuItem(@PathVariable Integer menuItemId) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return menuRepo.findById(menuItemId).get();
    }

    @GetMapping(path = "/products/{menuAvailabilityInd}")
    public List<Menu> getProducts(@PathVariable String menuAvailabilityInd) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return menuRepo.findByMenuAvailabilityInd(menuAvailabilityInd);
    }

    @GetMapping(path = "/catSummary")
    public List<Category> getCategory() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return categoryRepo.findAll();
    }

    @GetMapping(path = "/inventory")
    public List<Menu> getInventory() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return menuRepo.findAll();
    }

    @GetMapping(path = "/pricing/{menuItemId}")
    public List<Pricing> getPricing(@PathVariable int menuItemId) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String currencyFormat = "Rs ##,##,##0.00";
        DecimalFormat ft = new DecimalFormat(currencyFormat);
        Optional<Menu> menu = menuRepo.findById(menuItemId);
        List<Pricing> pricingList = pricingRepo.findByMenuItemId(menuItemId);
        for (Pricing pricing:pricingList){
            pricing.setMenuItemPackPriceFmtd(rf.formattedRupee(ft.format(pricing.getMenuItemPackPrice())));
            if (menu.get().getUnitOfMeasure().equals("Weight")){
                if (Float.parseFloat(pricing.getPackSize())  > 10.0){
                    pricing.setPackSize(pricing.getPackSize()+ " grams");
                } else {
                    pricing.setPackSize(pricing.getPackSize()+ " kilos");
                }
            } else if (menu.get().getUnitOfMeasure().equals("Volume")){
                if (Float.parseFloat(pricing.getPackSize())  > 10.0){
                    pricing.setPackSize(pricing.getPackSize()+ " ml");
                } else {
                    pricing.setPackSize(pricing.getPackSize()+ " liters");
                }
            } else {
                   pricing.setPackSize(pricing.getPackSize()+ " No.s");
            }
        }
        return pricingList;
    }

    /*
    http://localhost:8081/farmfoods/pricing/perpack?menuItemId=2&packSize=1
     */
    @GetMapping("/pricing/perpack")
    public Integer findPriceForPack(@RequestParam int menuItemId, @RequestParam String packSize, @RequestParam int quantity){
        log.info("Pricing requested for menu item %s for a pack size of %s.", menuItemId,packSize);
        return pricingRepo.findPriceForPack(menuItemId,packSize).getMenuItemPackPrice()*quantity;
    }

    @PostMapping(path = "/meta/addUpdateItem")
    public String AddBookToList(@ModelAttribute("item") Menu menu){
        menuRepo.save(menu);
        return "Updated Successfully";
    }

    @GetMapping(path = "/about")
    public String getAboutPage(){
        return "about";
    }

    @GetMapping(path = "/contact")
    public String getContactPage(){
        return "contact";
    }

    @GetMapping(path = "/reservation")
    public String getReservationPage(){
        return "reservation";
    }

    @GetMapping(path = "/service")
    public String getServicePage(){
        return "service";
    }

    @GetMapping(path = "/testimonial")
    public String getTestimonialPage(){
        return "testimonial";
    }
}
