package org.farmfresh.online.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.farmfresh.online.Domain.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(path = "/farmfoods")
public class MainController {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @GetMapping(path = "/home")
    public String getHomePage(Model model, RestTemplate restTemplate){
        log.info("Retrieving data for home page");
        HttpEntity<String> request = authenticateUser();
        //HomeMetaData homeMetaData = restTemplate.getForObject(
          //      "http://localhost:8081/farmfoods/home", HomeMetaData.class);
        ResponseEntity<HomeMetaData> response = restTemplate.exchange("http://localhost:8081/farmfoods/home", HttpMethod.GET, request, HomeMetaData.class);
        HomeMetaData homeMetaData = response.getBody();
        model.addAttribute("metahome",homeMetaData);
        return "homepage";
    }

    @GetMapping(path = "/item")
    public Menu getItemById(@RequestParam("menuItemId") String menuItemId,  Model model, RestTemplate restTemplate) {
        Menu menuItem = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/item/" + menuItemId, Menu.class);
        return menuItem;
    }

    @GetMapping(path = "/items")
    public String getItemsPage(@RequestParam("menuItemSubCategory") String menuItemSubCategory,  Model model, RestTemplate restTemplate){
        HomeMetaData homeMetaData = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/home", HomeMetaData.class);
        String displayMenuItemSubCategory = menuItemSubCategory;
        List<Menu> menuList = null;
        try {
            menuItemSubCategory = URLEncoder.encode(menuItemSubCategory,"UTF-8").replace("+", "%20");
            URL url = new URL("http://localhost:8081/farmfoods/items/" + menuItemSubCategory);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            if (httpURLConnection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + httpURLConnection.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (httpURLConnection.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                ObjectMapper mapper = new ObjectMapper();
                menuList = mapper.readValue(output, new TypeReference<List<Menu>>(){});
            }
            httpURLConnection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        homeMetaData.setCategoryHeader(displayMenuItemSubCategory);
        model.addAttribute("metahome",homeMetaData);
        for(Menu menu: menuList){

            menu.setMenuImageFileName("../"+menu.getMenuImageFileName());
            if (menu.getUnitOfMeasure().equals("Weight")) menu.setUnitToUse("Kgs");
            if (menu.getUnitOfMeasure().equals("Volume")) menu.setUnitToUse("Litres");
            if (menu.getUnitOfMeasure().equals("Count")) menu.setUnitToUse("No.s");
            System.out.println("Debug : " + menu.getMenuImageFileName() + " for " + menu.getMenuItemName());
            CartSummary cartCount = restTemplate.getForObject(
                    "http://localhost:8081/farmfoods/item/cartsummary/" + menu.getMenuItemId(), CartSummary.class);
            menu.setMenuItemInCartCount((cartCount == null)  ? 0 :cartCount.getMenuItemCount());
            BlockedQty blockedQty = restTemplate.getForObject(
                    "http://localhost:8081/farmfoods/item/blockedqty", BlockedQty.class);
            menu.setBlockedQty(blockedQty.getBlockedQuantity());
            menu.setFreeQty(menu.getAvailableQty() - blockedQty.getBlockedQuantity());
            List<Pricing> pricingList = new ArrayList<>();
            try {
                URL url = new URL("http://localhost:8081/farmfoods/pricing/" + menu.getMenuItemId());
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Accept", "application/json");

                if (httpURLConnection.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + httpURLConnection.getResponseCode());
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (httpURLConnection.getInputStream())));

                String output;
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    pricingList = mapper.readValue(output, new TypeReference<List<Pricing>>(){});
                    menu.setPricingList(pricingList);
                }
                httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("items", menuList);
        System.out.println(menuList);
        return "items";
    }

    @GetMapping(path = "/products")
    public String getProductsPage(Model model, RestTemplate restTemplate){
        HomeMetaData homeMetaData = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/home", HomeMetaData.class);

        List<Menu> menuList = null;
        try {
            URL url = new URL("http://localhost:8081/farmfoods/products/Y");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            if (httpURLConnection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + httpURLConnection.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (httpURLConnection.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                ObjectMapper mapper = new ObjectMapper();
                menuList = mapper.readValue(output, new TypeReference<List<Menu>>(){});
            }
            httpURLConnection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        homeMetaData.setCategoryHeader("All Products");
        model.addAttribute("metahome",homeMetaData);
        for(Menu menu: menuList){

            menu.setMenuImageFileName("../"+menu.getMenuImageFileName());
            if (menu.getUnitOfMeasure().equals("Weight")) menu.setUnitToUse("Kgs");
            if (menu.getUnitOfMeasure().equals("Volume")) menu.setUnitToUse("Litres");
            if (menu.getUnitOfMeasure().equals("Count")) menu.setUnitToUse("No.s");
            CartSummary cartCount = restTemplate.getForObject(
                    "http://localhost:8081/farmfoods/item/cartsummary/" + menu.getMenuItemId(), CartSummary.class);
            menu.setMenuItemInCartCount((cartCount == null)  ? 0 :cartCount.getMenuItemCount());
            BlockedQty blockedQty = restTemplate.getForObject(
                    "http://localhost:8081/farmfoods/item/blockedqty", BlockedQty.class);
            menu.setBlockedQty(blockedQty.getBlockedQuantity());
            menu.setFreeQty(menu.getAvailableQty() - blockedQty.getBlockedQuantity());
            List<Pricing> pricingList = new ArrayList<>();
            try {
                URL url = new URL("http://localhost:8081/farmfoods/pricing/" + menu.getMenuItemId());
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Accept", "application/json");

                if (httpURLConnection.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + httpURLConnection.getResponseCode());
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (httpURLConnection.getInputStream())));

                String output;
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    pricingList = mapper.readValue(output, new TypeReference<List<Pricing>>(){});
                    menu.setPricingList(pricingList);
                }
                httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Cart cart = new Cart();
        cart.setCartStatus("Pending");

        model.addAttribute("items", menuList);
        System.out.println(menuList);
        return "items";
    }

    @GetMapping(path = "/about")
    public String getAboutPage(Model model, RestTemplate restTemplate){
        HomeMetaData homeMetaData = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/home", HomeMetaData.class);
        model.addAttribute("metahome",homeMetaData);
        return "about";
    }

    @GetMapping(path = "/category")
    public String getAccountPage(Model model, RestTemplate restTemplate){
        HomeMetaData homeMetaData = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/home", HomeMetaData.class);
        List<Category> categoryList = null;
        try {
            URL url = new URL("http://localhost:8081/farmfoods/catSummary");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            if (httpURLConnection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + httpURLConnection.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (httpURLConnection.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                ObjectMapper mapper = new ObjectMapper();
                categoryList = mapper.readValue(output, new TypeReference<List<Category>>(){});
            }
            httpURLConnection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        homeMetaData.setCategoryHeader("Inventory List");
        model.addAttribute("metahome",homeMetaData);
        model.addAttribute("categories", categoryList);
        return "account";
    }

    @GetMapping(path = "/inventory")
    public String getInventory(@RequestParam("menuItemSubCategory") String menuItemSubCategory,  Model model, RestTemplate restTemplate){
        HomeMetaData homeMetaData = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/home", HomeMetaData.class);
        String displayMenuItemSubCategory = menuItemSubCategory;
        List<Menu> menuList = null;
        try {
            menuItemSubCategory = URLEncoder.encode(menuItemSubCategory,"UTF-8").replace("+", "%20");
            URL url = new URL("http://localhost:8081/farmfoods/inventorybycat/" + menuItemSubCategory);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            if (httpURLConnection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + httpURLConnection.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (httpURLConnection.getInputStream())));
            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                ObjectMapper mapper = new ObjectMapper();
                menuList = mapper.readValue(output, new TypeReference<List<Menu>>(){});
            }
            httpURLConnection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        homeMetaData.setCategoryHeader(displayMenuItemSubCategory);
        model.addAttribute("metahome",homeMetaData);
        for(Menu menu: menuList){

            menu.setMenuImageFileName("../"+menu.getMenuImageFileName());
            if (menu.getUnitOfMeasure().equals("Weight")) menu.setUnitToUse("Kgs");
            if (menu.getUnitOfMeasure().equals("Volume")) menu.setUnitToUse("Litres");
            if (menu.getUnitOfMeasure().equals("Count")) menu.setUnitToUse("No.s");
            CartSummary cartCount = restTemplate.getForObject(
                    "http://localhost:8081/farmfoods/item/cartsummary/" + menu.getMenuItemId(), CartSummary.class);
            menu.setMenuItemInCartCount((cartCount == null)  ? 0 :cartCount.getMenuItemCount());
            BlockedQty blockedQty = restTemplate.getForObject(
                    "http://localhost:8081/farmfoods/item/blockedqty", BlockedQty.class);
            menu.setBlockedQty(blockedQty.getBlockedQuantity());
            menu.setFreeQty(menu.getAvailableQty() - blockedQty.getBlockedQuantity());
            List<Pricing> pricingList = new ArrayList<>();
            try {
                URL url = new URL("http://localhost:8081/farmfoods/pricing/" + menu.getMenuItemId());
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Accept", "application/json");

                if (httpURLConnection.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + httpURLConnection.getResponseCode());
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (httpURLConnection.getInputStream())));

                String output;
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    pricingList = mapper.readValue(output, new TypeReference<List<Pricing>>(){});
                    menu.setPricingList(pricingList);
                }
                httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("items", menuList);
        System.out.println(menuList);
        return "inventoryitems";
    }

    @GetMapping(path = "/showFormForUpdating")
    public String ShowFormForUpdate(@RequestParam("menuItemId") int menuItemId, Model model, RestTemplate restTemplate){

        HomeMetaData homeMetaData = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/home", HomeMetaData.class);
        model.addAttribute("metahome",homeMetaData);

        Menu menuToBeUpdated = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/item/" + menuItemId, Menu.class);
        //Set the Customer as the Model Attribute to Prepopulate the Form
        menuToBeUpdated.setMenuImageFileName("../" + menuToBeUpdated.getMenuImageFileName());
        if (menuToBeUpdated.getUnitOfMeasure().equals("Weight")) menuToBeUpdated.setUnitToUse("Kgs");
        if (menuToBeUpdated.getUnitOfMeasure().equals("Volume")) menuToBeUpdated.setUnitToUse("Litres");
        if (menuToBeUpdated.getUnitOfMeasure().equals("Count")) menuToBeUpdated.setUnitToUse("No.s");
        CartSummary cartCount = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/item/cartsummary/" + menuToBeUpdated.getMenuItemId(), CartSummary.class);
        menuToBeUpdated.setMenuItemInCartCount((cartCount == null)  ? 0 :cartCount.getMenuItemCount());
        BlockedQty blockedQty = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/item/blockedqty", BlockedQty.class);
        menuToBeUpdated.setBlockedQty(blockedQty.getBlockedQuantity());
        menuToBeUpdated.setFreeQty(menuToBeUpdated.getAvailableQty() - blockedQty.getBlockedQuantity());

        System.out.println("Debug : " + menuToBeUpdated.getMenuImageFileName() + " for " + menuToBeUpdated.getMenuItemName());

        List<Pricing> pricingList = new ArrayList<>();
        try {
            URL url = new URL("http://localhost:8081/farmfoods/pricing/" + menuToBeUpdated.getMenuItemId());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            if (httpURLConnection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + httpURLConnection.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (httpURLConnection.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                ObjectMapper mapper = new ObjectMapper();
                pricingList = mapper.readValue(output, new TypeReference<List<Pricing>>(){});
                menuToBeUpdated.setPricingList(pricingList);
            }
            httpURLConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.addAttribute("item",menuToBeUpdated);
        //Send the data to the update form
        return "updateinventory";
    }

    @PostMapping(path = "/addUpdateItem")
    public String updateMenuItem(@ModelAttribute("item") Menu menu, RestTemplate restTemplate){
        log.info(menu.toString());
        menu.setMenuImageFileName(menu.getMenuImageFileName().substring(3));
        //Reference https://howtodoinjava.com/spring-boot2/resttemplate/resttemplate-post-json-example/
        try {
            URI uri = new URI("http://localhost:8081/farmfoods/menu/addUpdateItem");
            ResponseEntity<Menu> result = restTemplate.postForEntity(uri, menu, Menu.class);
            log.info(result.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        log.info(menu.getPricingList().toString());
        for(Pricing pricing: menu.getPricingList()){
            pricing.setPackSize(pricing.getPackSize().split(" ")[0]);
            try {
                URI uri = new URI("http://localhost:8081/farmfoods/menu/addUpdatePrice");
                ResponseEntity<Pricing> result = restTemplate.postForEntity(uri, pricing, Pricing.class);
                log.info(result.toString());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        log.info("Save successful");
        return "redirect:/farmfoods/inventory?menuItemSubCategory=Dairy";
    }

    @PostMapping(path = "/addToCart")
    public String addToCart(@ModelAttribute("cart") Cart cart, RestTemplate restTemplate){
        log.info(cart.toString());
        //Reference https://howtodoinjava.com/spring-boot2/resttemplate/resttemplate-post-json-example/
        try {
            URI uri = new URI("http://localhost:8081/farmfoods/menu/addToCart");
            ResponseEntity<Cart> result = restTemplate.postForEntity(uri, cart, Cart.class);
            log.info(result.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        log.info("Add to cart is successful");
        return "redirect:/farmfoods/products";
    }

    @GetMapping(path = "/cart")
    public String getContactPage(Model model, RestTemplate restTemplate){
        HomeMetaData homeMetaData = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/home", HomeMetaData.class);

        List<Cart> cartList = null;
        try {
            //menuItemSubCategory = URLEncoder.encode(menuItemSubCategory,"UTF-8").replace("+", "%20");
            String customerId = homeMetaData.getLoggedInUser();
            URL url = new URL("http://localhost:8081/farmfoods/cart/" + customerId);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            if (httpURLConnection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + httpURLConnection.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (httpURLConnection.getInputStream())));
            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                ObjectMapper mapper = new ObjectMapper();
                cartList = mapper.readValue(output, new TypeReference<List<Cart>>(){});
            }
            httpURLConnection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cartList.size() > 0) {
            homeMetaData.setCartHeader("Shopping Cart");
            homeMetaData.setCartSubHeader("Explore more");
        }
        else {
            homeMetaData.setCartHeader("Your cart is empty!");
            homeMetaData.setCartSubHeader("Continue Shopping");
        }
        if (cartList.size() > 0)
            homeMetaData.setCartSummary("Your cart total (" + cartList.size() + " items) is : " + cartList.get(Math.max(cartList.size() - 1, 0)).getCartTotalFmtd());
        else
            homeMetaData.setCartSummary("Your cart total (" + cartList.size() + " items) is : Rs. 0.00" );
        model.addAttribute("metahome",homeMetaData);
        model.addAttribute("items",cartList);
        return "cart";
    }

    @GetMapping(path = "/cart/delete")
    public String deleteCartItem(@RequestParam("cartId") int cartId, Model model, RestTemplate restTemplate) {

        HomeMetaData homeMetaData = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/home", HomeMetaData.class);

        Cart cart = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/cart/delete/"+cartId, Cart.class);

        return "redirect:/farmfoods/cart";
    }

    @GetMapping(path = "/order")
    public String getServicePage(@RequestParam("customerId") String customerId, Model model, RestTemplate restTemplate){
        HomeMetaData homeMetaData = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/home", HomeMetaData.class);
        String cart = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/place/order/"+homeMetaData.getLoggedInUser(), String.class);
        homeMetaData.setCartHeader(cart + ", Your order is succeffully placed!");
        homeMetaData.setCartSubHeader("Explore more");
        model.addAttribute("metahome",homeMetaData);
        return "order";
    }

    @GetMapping(path = "/testimonial")
    public String getTestimonialPage(Model model){
        return "testimonial";
    }

    private HttpEntity<String> authenticateUser(){
        String plainCreds = "farmpeople:dhariya";
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        HttpEntity<String> request = new HttpEntity<String>(headers);

       return request;
    }
}
