package org.farmfresh.online.Controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.farmfresh.online.Domain.HomeMetaData;
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
}
