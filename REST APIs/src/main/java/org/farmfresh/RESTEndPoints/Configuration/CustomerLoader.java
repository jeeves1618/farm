package org.farmfresh.RESTEndPoints.Configuration;


import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.farmfresh.RESTEndPoints.Entity.Customer;
import org.farmfresh.RESTEndPoints.Repo.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("h2")
@Slf4j
public class CustomerLoader implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    Customer customer;

    @Autowired
    CustomerRepo customerRepo;

    private final Faker faker = new Faker();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        long millis=System.currentTimeMillis();
        java.sql.Date date=new java.sql.Date(millis);
        List<String> userList = new ArrayList<>();
        userList.add("Priya");
        userList.add("Guest");
        userList.add("Sathya");
        userList.add("Nithya");
        userList.add("Lavanya");
        userList.add("Swetha");
        userList.add("Dharshana");
        userList.add("Naga");

        List<Customer> customerList = new ArrayList<>();
        for(String user: userList) {
            customer.setCustomerBalance(Double.valueOf(faker.number().numberBetween(500,5000)));
            if (user.equals("Swetha"))
                customer.setCustomerBalance(300.00);
            if (customer.getCustomerBalance() < 2500)
                customer.setCustomerCredential("Please leave the groceries on top of the shoe rack outside the front door. Customer takes resposibility for missing items");
            else
                customer.setCustomerCredential("Please hand it over to the resident. If there is no response to calling bell, try calling the mobile number ");
            customer.setCustomerMobile(faker.phoneNumber().cellPhone());
            customer.setCustomerEmail(faker.internet().emailAddress());
            customer.setCustomerUserId(user);
            customer.setCustomerBillingAddress(faker.address().fullAddress());
            if (user.equals("Naga"))
                customer.setCustomerDiscountRate(0.1);
            else
                customer.setCustomerDiscountRate(0.0);
            customer.setUserCreated(user);
            customer.setUserUpdated(user);
            customer.setDateCreated(date);
            customer.setDateUpdated(date);
            customerList.add(customer);
            customer = new Customer();
        }

        customerRepo.saveAll(customerList);
    }
}
