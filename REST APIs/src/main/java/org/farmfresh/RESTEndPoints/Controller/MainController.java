package org.farmfresh.RESTEndPoints.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.farmfresh.RESTEndPoints.Domain.AddToCart;
import org.farmfresh.RESTEndPoints.Domain.BlockedQty;
import org.farmfresh.RESTEndPoints.Domain.CartCustItemCount;
import org.farmfresh.RESTEndPoints.Domain.CartsSummary;
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
/*
Instead of allowing cross origin across any domain by specifying @CrossOrigin,
specify the required domains explicitly. 
@CrossOrigin(origins = "http://localhost:80xXX")
 */
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
    CustomerRepo customerRepo;

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
        List<Menu> menuList = menuRepo.findByMenuAvailabilityInd(menuAvailabilityInd);
        return menuList;
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

    @GetMapping(path = {"/pricing/{menuItemId}","/pricing/{menuItemId}/{includeAll}"})
    public List<Pricing> getPricing(@PathVariable int menuItemId, @PathVariable(required = false)String includeAll) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String currencyFormat = "Rs ##,##,##0.00";
        DecimalFormat ft = new DecimalFormat(currencyFormat);
        Optional<Menu> menu = menuRepo.findById(menuItemId);
        List<Pricing> pricingList;
        if (includeAll != null)
            pricingList = pricingRepo.findAllByMenuItemId(menuItemId);
        else
            pricingList = pricingRepo.findByMenuItemId(menuItemId);
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
                cart.setMenuItemName(menu.getMenuItemName() + getMenuItemNameFormatted(menu,cart.getPackSize()));
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

    @GetMapping(path = "/orderdetail/{orderId}")
    public List<Order> getOrderItems(@PathVariable String orderId) throws IOException {
        List<Order> orderList = orderRepo.findByDisplayOrderId(Integer.valueOf(orderId));
        int orderTotal = 0;
        for(Order order: orderList){
            Optional<Menu> menuItem = menuRepo.findById(order.getMenuItemId());
            log.info("Menu Id : " + order.getMenuItemId());
            if (menuItem.isPresent()) {
                Menu menu = menuItem.get();
                order.setOrderItemName(menu.getMenuItemName() + getMenuItemNameFormatted(menu,order.getPackSize()));
                order.setOrderItemCategory(menu.getMenuItemCategory());
                order.setOrderItemSubCategory(menu.getMenuItemSubCategory());
                order.setOrderItemDescription(menu.getMenuItemDescription());
                order.setOrderImageFileName(menu.getMenuImageFileName());
                String currencyFormat = "Rs ##,##,##0.00";
                DecimalFormat ft = new DecimalFormat(currencyFormat);
                order.setOrderItemTotalPriceFmtd(rf.formattedRupee(ft.format(order.getMenuItemCount()*order.getMenuItemPackPrice())));
                order.setOrderItemPriceFmtd(rf.formattedRupee(ft.format(order.getMenuItemPackPrice())));
                order.setOrderImageFileName("../" + menu.getMenuImageFileName());
                orderTotal = orderTotal + order.getMenuItemCount()*order.getMenuItemPackPrice();
                order.setOrderTotal(orderTotal);
                log.info("Cart Total " + order.getOrderTotal() + " and " + order.getOrderImageFileName());
                order.setOrderTotalFmtd(rf.formattedRupee(ft.format(order.getOrderTotal())));
            }
        }
        return orderList;
    }

    private String getMenuItemNameFormatted(Menu menu, String packSize){

        String packSizeUnit = null;
        switch (menu.getUnitOfMeasure()){
            case "Volume":
                if (Integer.valueOf(packSize) > 10)
                    packSizeUnit = " - " + packSize + " ml";
                else {
                    if (Integer.valueOf(packSize) > 1)
                        packSizeUnit = " - " + packSize + " liters";
                    else
                        packSizeUnit = " - " + packSize + " liter";
                }
                break;
            case "Weight":
                if (Integer.valueOf(packSize) > 10)
                    packSizeUnit = " - " + packSize + " grams";
                else {
                    if (Integer.valueOf(packSize) > 1)
                        packSizeUnit = " - " + packSize + " kilos";
                    else
                        packSizeUnit = " - " + packSize + " kilo";
                }
                break;
            case "Count":
                packSizeUnit = " - " + packSize + " No.s";
                break;
            default:
                packSizeUnit = "Unit Error";
        }
        return packSizeUnit;
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
        int cartCount = cartList.size();
        Customer customer = customerRepo.findByCustomerUserId(customerId);
        for (Cart cart: cartList){

            order = new Order();
            double packSize = 0.0;
            Menu menu = menuRepo.findById(cart.getMenuItemId()).get();
            BlockedQty blockedQty = getBlockedQty(order.getMenuItemId());
            menu.setBlockedQty(blockedQty.getBlockedQuantity());
            Double remainingQty = menu.getAvailableQty() - blockedQty.getBlockedQuantity() - getStandardizedQty(cart.getPricingId(),cart.getMenuItemCount());
            for (Pricing pricing: pricingRepo.findAllByMenuItemId(cart.getMenuItemId())){
                if (pricing.getStandardizedUnit().equals("ml") || pricing.getStandardizedUnit().equals("grams")){
                    packSize = Double.valueOf(pricing.getPackSize())/1000;
                } else {
                    packSize = Double.valueOf(pricing.getPackSize());
                }
                if (packSize > remainingQty) {
                    pricing.setMenuItemPackPrice(0);
                    menu.setMenuAvailabilityInd("N");
                }
                pricingRepo.save(pricing);
            }
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
            order.setStandardizedQuantity(getStandardizedQty(cart.getPricingId(),cart.getMenuItemCount()));

            if ((orderTotalValue + cart.getMenuItemTotalPrice()) < customer.getCustomerBalance()) {
                orderTotalValue = cart.getMenuItemTotalPrice() + orderTotalValue;
                menuRepo.save(menu);
                orderRepo.save(order);
                cartRepo.deleteById(cart.getCartId());
                orderItemCount++;
            }

            if (orderId == 0) {
                orderId = order.getOrderId();
                order.setDisplayOrderId(orderId);
                orderRepo.save(order);
                log.info("Display Order ID: " + order.getOrderId());
            }

            //Update Inventory

        }
        customer.setCustomerBalance(customer.getCustomerBalance()-orderTotalValue);
        customerRepo.save(customer);
        createOrderSummary(order,orderItemCount,orderTotalValue,savings);
        /*for(Cart cart: cartList){

        }*/
        if (orderItemCount == cartCount)
            return "Dear " + customerId + ", Your order (ID: " + orderId + ") is successfully placed!";
        else
            return "Dear " + customerId + ", Your order (ID: " + orderId + ") is not fully placed due to insufficient balance. Please add money to wallet and re-order the items in the cart";
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
    @GetMapping(path = "/item/blockedqty/{menuItemId}")
    public BlockedQty getBlockedQty(@PathVariable int menuItemId){
        BlockedQty blockedQty = orderRepo.findSumByMenuId(menuItemId);
        if (blockedQty == null){
            blockedQty = new BlockedQty(0,0.0);
        }
        System.out.println(blockedQty.getBlockedQuantity());
        return blockedQty;
    }

    private double getStandardizedQty(int pricingId, int menuItemCount){
        Optional<Pricing> pricingDetails = pricingRepo.findById(pricingId);
        Pricing pricing = pricingDetails.get();
        switch (pricing.getStandardizedUnit()){
            case "liters":
            case "kilos":
            case "No.s":
                return Math.floor(Double.valueOf(pricing.getPackSize())*menuItemCount*100)/100;
            case "grams":
            case "ml":
                return Math.floor(Double.valueOf(pricing.getPackSize())/1000*menuItemCount*100)/100;
            default:
                return 0.0;
        }
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
            log.info("Retrieving as admin : " + customerId + USER_ROLE);
            return orderSummaryRepo.findAll();
        }
        else {
            log.info("Retrieving as shopper : " + customerId);
            return orderSummaryRepo.findByCustomerId(customerId);
        }
    }

    @GetMapping(path = "/cart/summary/{customerRole}")
    public List<CartsSummary> getCartsSummary(@PathVariable String customerRole){
        Authentication currentUserName = getUser();
        if (customerRole.equals("Admin")) {
            log.info("Retrieving as admin ");
            return cartRepo.findCountByMenuIdAndPackSize();
        }
        else {
            return null;
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
