package org.farmfresh.RESTEndPoints.Configuration;


import lombok.extern.slf4j.Slf4j;
import org.farmfresh.RESTEndPoints.Entity.HomeData;
import org.farmfresh.RESTEndPoints.Repo.HomeDataRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("h2")
@Slf4j
public class HomeDataLoader implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    HomeData homeData;

    @Autowired
    HomeDataRepo homeDataRepo;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        homeData.setTitleText("Dhariya  Farms");
        homeData.setTelephoneNumber("9915081947");
        homeData.setTelephoneNumberText("Call us for home delivery");
        homeData.setCenterCarousalHeader("Farming for families since 2016");
        homeData.setCenterCarousalLine1("Prompt Door Delivery");
        homeData.setCenterCarousalLine2("Freshly Grown Vegetables");
        homeData.setCenterCarousalLine3("A Hygienic Indulgence");
        homeData.setCenterCarousalLine4("Online Order Booking");
        homeData.setTileHeader1("Dairy and Eggs");
        homeData.setTileHeader2("Rice and Related");
        homeData.setTileHeader3("Oil & Honey");
        homeData.setTileHeader4("Vegetables");
        homeData.setTileHeader5("Others");
        homeData.setTileHeader6("Pickup Point");
        homeData.setCategoryMessage("Availability of fruits and vegetables are subject to seasonal variations");
        homeData.setLoggedInUser("Guest");
        homeDataRepo.save(homeData);
    }
}
