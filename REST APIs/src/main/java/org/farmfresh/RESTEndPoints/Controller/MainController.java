package org.farmfresh.RESTEndPoints.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.farmfresh.RESTEndPoints.Domain.AddToCart;
import org.farmfresh.RESTEndPoints.Domain.BlockedQty;
import org.farmfresh.RESTEndPoints.Domain.CartCustItemCount;
import org.farmfresh.RESTEndPoints.Entity.*;
import org.farmfresh.RESTEndPoints.Repo.*;
import org.farmfresh.RESTEndPoints.Service.HomeDataService;
import org.farmfresh.RESTEndPoints.Service.RupeeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    OrderRepo orderRepo;

    @Autowired
    OrderSummaryRepo orderSummaryRepo;

    @Autowired
    RupeeFormatter rf;

    long millis=System.currentTimeMillis();
    java.sql.Date date=new java.sql.Date(millis);

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
        int cartTotal = 0;
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
                cart.setMenuItemPriceFmtd(rf.formattedRupee(ft.format(cart.getMenuItemPackPrice())));
                cart.setMenuImageFileName("../" + menu.getMenuImageFileName());
                cartTotal = cartTotal + cart.getMenuItemTotalPrice();
                cart.setCartTotal(cartTotal);
                log.info("Cart Total " + cart.getCartTotal() + " and " + cart.getMenuImageFileName());
                cart.setCartTotalFmtd(rf.formattedRupee(ft.format(cart.getCartTotal())));
            }
        }
        return cartList;
    }

    @GetMapping(path = "/cart/delete/{cartId}")
    public Cart deleteCartItem(@PathVariable int cartId) throws IOException {
        Cart cart = cartRepo.findById(cartId).get();
        if (cart != null)
            cartRepo.deleteById(cartId);
        return cart;
    }

    @GetMapping(path = "/place/order/{customerId}")
    public String placeOrder(@PathVariable String customerId) throws IOException {
        Order order = null;
        int orderItemCount = 0, orderTotalValue = 0, savings = 0;
        int orderId = 0;
        List<Cart> cartList = cartRepo.findByCustomerId(customerId);
        for (Cart cart: cartList){
            orderItemCount++;
            order = new Order();
            order.setCustomerId(customerId);
            order.setMenuItemId(cart.getMenuItemId());
            order.setPricingId(cart.getPricingId());
            order.setMenuItemCount(cart.getMenuItemCount());
            order.setPackSize(cart.getPackSize());
            order.setMenuItemPackPrice(cart.getMenuItemPackPrice());
            order.setCustomerDiscountRate(cart.getCustomerDiscountRate());
            order.setOrderStatus(cart.getCartStatus());
            order.setCustomerShippingAddress("123 Apartment Tower, Street from Customer Database, Locality, Chennai - 600 000");
            order.setDateCreated(date);
            order.setDateUpdated(date);
            order.setUserCreated(customerId);
            order.setUserUpdated(customerId);
            order.setDisplayOrderId(orderId);
            orderTotalValue = cart.getMenuItemTotalPrice() +orderTotalValue;
            orderRepo.save(order);
            if (orderId == 0) {
                orderId = order.getOrderId();
                order.setDisplayOrderId(orderId);
                orderRepo.save(order);
                log.info("Display Order ID: " + order.getOrderId());
            }
        }
        createOrderSummary(order,orderItemCount,orderTotalValue,savings);
        for(Cart cart: cartList){
            cartRepo.deleteById(cart.getCartId());
        }

        return "Dear " + customerId + ", Your order (ID: " + orderId + ") is successfully placed!";
    }

    public void createOrderSummary(Order order, int count, int totalOrderValue, int savings){
        OrderSummary orderSummary = new OrderSummary();
        orderSummary.setDisplayOrderId(order.getDisplayOrderId());
        orderSummary.setCustomerId(order.getCustomerId());
        orderSummary.setOrderItemCount(count);
        orderSummary.setTotalOrderValue(totalOrderValue);
        orderSummary.setYouSaved(savings);
        orderSummary.setOrderStatus(order.getOrderStatus());
        orderSummary.setCustomerShippingAddress(order.getCustomerShippingAddress());
        orderSummary.setDateCreated(date);
        orderSummary.setDateUpdated(date);
        orderSummary.setUserCreated(order.getUserCreated());
        orderSummary.setUserUpdated(order.getUserUpdated());

        orderSummaryRepo.save(orderSummary);
    }
    @GetMapping(path = "/item/blockedqty")
    public BlockedQty getBlockedQty(){
        BlockedQty blockedQty = new BlockedQty();
        blockedQty.setBlockedQuantity(0.0);
        return blockedQty;
    }

    @GetMapping(path = "item/cartsummary/{menuItemId}/{customerId}")
    public CartCustItemCount getCartSummary(@PathVariable int menuItemId, @PathVariable String customerId){
        return cartRepo.findCartCountByItemCust(menuItemId, customerId);
    }

    private String USER_NAME = "Guest";
    private String USER_ROLE = "Guest";

    private Authentication getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Authentication currentUserName = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {

            currentUserName = authentication;
            USER_NAME = authentication.getName();
            log.info("Logged in as " + USER_NAME);
            USER_ROLE = authentication.getAuthorities().stream().toArray()[0].toString();
        } else {
            USER_NAME = "Guest";
            USER_ROLE = "Guest";
        }
        return currentUserName;
    }

    @GetMapping(path = "order/summary/{customerId}")
    public List<OrderSummary> getOrderSummary(@PathVariable String customerId){
        Authentication currentUserName = getUser();
        if (customerId.equals("Priya")) {
            log.info("Retrieving as admin : " + customerId);
            return orderSummaryRepo.findAll();
        }
        else {
            log.info("Retrieving as guest : " + customerId);
            return orderSummaryRepo.findByCustomerId(customerId);
        }
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
