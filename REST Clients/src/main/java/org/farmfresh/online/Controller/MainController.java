package org.farmfresh.online.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.farmfresh.online.Domain.HomeMetaData;
import org.farmfresh.online.Domain.Menu;
import org.farmfresh.online.Domain.Pricing;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
        HomeMetaData homeMetaData = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/home", HomeMetaData.class);
        model.addAttribute("metahome",homeMetaData);
        return "homepage";
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
            System.out.println("Debug : " + menu.getMenuImageFileName() + " for " + menu.getMenuItemName());
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
            System.out.println("Debug : " + menu.getMenuImageFileName() + " for " + menu.getMenuItemName());
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

    @GetMapping(path = "/about")
    public String getAboutPage(Model model, RestTemplate restTemplate){
        HomeMetaData homeMetaData = restTemplate.getForObject(
                "http://localhost:8081/farmfoods/home", HomeMetaData.class);
        model.addAttribute("metahome",homeMetaData);
        return "about";
    }

    @GetMapping(path = "/account")
    public String getAccountPage(Model model, RestTemplate restTemplate){
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
        homeMetaData.setCategoryHeader("Inventory List");
        model.addAttribute("metahome",homeMetaData);
        for(Menu menu: menuList){

            menu.setMenuImageFileName("../"+menu.getMenuImageFileName());
            System.out.println("Debug : " + menu.getMenuImageFileName() + " for " + menu.getMenuItemName());
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
        model.addAttribute("products", menuList);
        return "account";
    }

    @GetMapping(path = "/contact")
    public String getContactPage(Model model){
        return "contact";
    }

    @GetMapping(path = "/reservation")
    public String getReservationPage(Model model){
        return "reservation";
    }

    @GetMapping(path = "/service")
    public String getServicePage(Model model){
        return "service";
    }

    @GetMapping(path = "/testimonial")
    public String getTestimonialPage(Model model){
        return "testimonial";
    }
}
