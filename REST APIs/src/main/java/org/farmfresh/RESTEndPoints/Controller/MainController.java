package org.farmfresh.RESTEndPoints.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.farmfresh.RESTEndPoints.Domain.AddToCart;
import org.farmfresh.RESTEndPoints.Domain.BlockedQty;
import org.farmfresh.RESTEndPoints.Domain.CartSummary;
import org.farmfresh.RESTEndPoints.Entity.*;
import org.farmfresh.RESTEndPoints.Repo.CartRepo;
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
    CartRepo cartRepo;

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
    @GetMapping(path = "/items/{menuItemSubCategory}")
    public List<Menu> getMenuItems(@PathVariable String menuItemSubCategory) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return menuRepo.findByMenuItemSubCategoryAndMenuAvailabilityInd(menuItemSubCategory,"Y");
    }

    @GetMapping(path = "/inventorybycat/{menuItemSubCategory}")
    public List<Menu> getInventoryItems(@PathVariable String menuItemSubCategory) throws IOException {
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

    @PostMapping(path = "/menu/addUpdateItem")
    public Menu updateProducts(@RequestBody Menu menu){
        log.info(menu.toString());
        menuRepo.save(menu);
        log.info("API invoked for updating product");
        return menu;
    }

    @PostMapping(path = "/menu/addUpdatePrice")
    public Pricing updatePricing(@RequestBody Pricing pricing){
        log.info(pricing.toString());
        pricingRepo.save(pricing);
        log.info("API invoked for updating price");
        return pricing;
    }

    @PostMapping(path = "/menu/addToCart")
    public Cart addToCart(@RequestBody AddToCart addToCart){
        Cart cart = new Cart();
        cart.setMenuItemId(addToCart.getMenuItemId());
        cart.setPricingId(addToCart.getPricingId());
        cart.setMenuItemCount(addToCart.getMenuItemCount());
        cart.setPackSize(addToCart.getPackSize());
        int menuItemPackPrice = pricingRepo.findById(addToCart.getPricingId()).get().getMenuItemPackPrice();
        cart.setMenuItemPackPrice(menuItemPackPrice);
        cart.setCustomerDiscountRate(0.0);
        cart.setMenuItemTotalPrice((int) (menuItemPackPrice * addToCart.getMenuItemCount() * (1 - cart.getCustomerDiscountRate())));
        cart.setCartStatus("Pending");
        cart.setUserUpdated(addToCart.getCustomerId());
        cart.setUserCreated(addToCart.getCustomerId());
        cart.setCustomerId(addToCart.getCustomerId());
        long millis=System.currentTimeMillis();
        java.sql.Date date=new java.sql.Date(millis);
        cart.setDateCreated(date);
        cart.setDateUpdated(date);
        log.info(cart.toString());
        cartRepo.save(cart);
        log.info("API invoked for adding item to Cart");
        return cart;
    }

    @GetMapping(path = "/cart/{customerId}")
    public List<Cart> getCartItems(@PathVariable String customerId) throws IOException {
        List<Cart> cartList = cartRepo.findByCustomerId(customerId);
        for(Cart cart: cartList){
            Optional<Menu> menuItem = menuRepo.findById(cart.getMenuItemId());
            log.info("Menu Id : " + cart.getMenuItemId());
            if (menuItem.isPresent()) {
                Menu menu = menuItem.get();
                cart.setMenuItemName(menu.getMenuItemName());
                cart.setMenuItemCategory(menu.getMenuItemCategory());
                cart.setMenuItemSubCategory(menu.getMenuItemSubCategory());
                cart.setMenuItemDescription(menu.getMenuItemDescription());
                String currencyFormat = "Rs ##,##,##0.00";
                DecimalFormat ft = new DecimalFormat(currencyFormat);
                cart.setMenuItemTotalPriceFmtd(rf.formattedRupee(ft.format(cart.getMenuItemTotalPrice())));
            }
        }
        return cartList;
    }

    @GetMapping(path = "/item/blockedqty")
    public BlockedQty getBlockedQty(){
        BlockedQty blockedQty = new BlockedQty();
        blockedQty.setBlockedQuantity(0.0);
        return blockedQty;
    }

    @GetMapping(path = "item/cartsummary/{menuItemId}")
    public CartSummary getCartSummary(@PathVariable int menuItemId){
        return cartRepo.findCountByMenuId(menuItemId);
    }

    @GetMapping(path = "item/cartcount/{customerId}")
    public long getCartSummary(@PathVariable String customerId){
        long cartCount;
        if (cartRepo.findCartCount(customerId) == null)
            cartCount = 0;
        else
            cartCount = cartRepo.findCartCount(customerId).getCartCount();
        return cartCount;
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
