package org.farmfresh.online.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.farmfresh.online.Domain.Customer;
import org.farmfresh.online.Domain.HomeMetaData;
import org.farmfresh.online.Service.RupeeFormatter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(path = "/admin")
public class AdminController {
    private String USER_NAME = "Guest";
    private String USER_ROLE = "Guest";

    private Authentication getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Authentication currentUserName = null;
        log.info("Still a guest");
        if (!(authentication instanceof AnonymousAuthenticationToken)) {

            currentUserName = authentication;
            USER_NAME = authentication.getName();
            log.info("Logged in as " + USER_NAME);
            USER_ROLE = authentication.getAuthorities().stream().toArray()[0].toString();
        }
        return currentUserName;
    }

    private HomeMetaData getHomeMetaData(RestTemplate restTemplate){
        log.info("Now a " + USER_ROLE);
        String routingUrl = null;
        if (USER_ROLE.equals("admin")) {
            routingUrl = "redirect:/farmfoods/shopper/landing";
        } else if (USER_ROLE.equals("read")) {
            routingUrl = "redirect:/farmfoods/shopper/landing";
        } else {
            routingUrl = "redirect:/farmfoods/home";
        }
        log.info("Will be routed to " + routingUrl);
        Authentication currentUser = getUser();
        HttpEntity<String> request = authenticateUser();
        ResponseEntity<HomeMetaData> response = restTemplate.exchange("http://localhost:8081/farmfoods/home", HttpMethod.GET, request, HomeMetaData.class);
        HomeMetaData homeMetaData = response.getBody();
        if (USER_ROLE.equals("admin")) {
            homeMetaData.setNavRoleSensitiveLabel("Admin");
        } else if (USER_ROLE.equals("read")) {
            homeMetaData.setNavRoleSensitiveLabel("Hi " + USER_NAME);
        } else {
            homeMetaData.setNavRoleSensitiveLabel("Login");
        }
        homeMetaData.setLoggedInUser(USER_NAME);
        homeMetaData.setRoutingUrl(routingUrl);
        return homeMetaData;
    }


    @GetMapping(path = "/login")
    public String getHomePage(Model model, RestTemplate restTemplate){

        log.info("Retrieving data for home page for " + USER_NAME);
        log.info("Authorities : " + USER_ROLE);
        HomeMetaData homeMetaData = getHomeMetaData(restTemplate);
        model.addAttribute("metahome",homeMetaData);
        return "login";
    }

    private HttpEntity<String> authenticateUser(){
        String plainCreds = USER_NAME + ":farm";
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        HttpEntity<String> request = new HttpEntity<String>(headers);

        return request;
    }

    @GetMapping(path = "/customer")
    public String getCustomers(Model model, RestTemplate restTemplate) {
        RupeeFormatter rupeeFormatter = new RupeeFormatter();
        HomeMetaData homeMetaData = getHomeMetaData(restTemplate);

        List<Customer> customerList = null;
        try {
            //menuItemSubCategory = URLEncoder.encode(menuItemSubCategory,"UTF-8").replace("+", "%20");
            String customerId = homeMetaData.getLoggedInUser();
            URL url = new URL("http://localhost:8081/admin/customers");
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
                customerList = mapper.readValue(output, new TypeReference<List<Customer>>(){});
            }
            httpURLConnection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (customerList.size() > 0) {
            homeMetaData.setCartHeader("Your Customers");
            homeMetaData.setCartSubHeader("Back to Admin Screen");
        }
        else {
            if (USER_ROLE.equals("admin")) {
                homeMetaData.setCartHeader("Customers do not exist!");
                homeMetaData.setCartSubHeader("Please add the customers");
            }
            else {
                homeMetaData.setCartHeader("You do not have access to this screen");
                homeMetaData.setCartSubHeader("Back to Landing Page");
            }
        }
        for (Customer customer: customerList){
            String currencyFormat = "Rs ##,##,##0.00";
            DecimalFormat ft = new DecimalFormat(currencyFormat);
            customer.setCustomerBalanceFmtd(rupeeFormatter.formattedRupee(ft.format(customer.getCustomerBalance())));
        }
        model.addAttribute("metahome",homeMetaData);
        model.addAttribute("customers",customerList);
        return "customers";
    }
}
