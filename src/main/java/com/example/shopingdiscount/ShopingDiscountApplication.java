package com.example.shopingdiscount;

import com.example.shopingdiscount.model.DiscountRequest;
import com.example.shopingdiscount.model.DiscountResponse;
import com.example.shopingdiscount.service.DiscountService;
import tools.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

@SpringBootApplication
public class ShopingDiscountApplication {

    /**
     * Name of the request JSON file (under src/main/resources/requests) to run on startup.
     * Change this constant to try a different scenario, then re-run the application.
     * Available files: 1-fixed-amount-coupon.json, 2-percentage-coupon.json,
     * 3-category-on-top.json, 4-points-on-top.json, 5-seasonal.json,
     * 6-combined-coupon-ontop-seasonal.json, 7-error-duplicate-category.json
     */
    private static final String REQUEST_FILE = "requests/1-fixed-amount-coupon.json";

    public static void main(String[] args) {
        SpringApplication.run(ShopingDiscountApplication.class, args);
    }

    @Bean
    public CommandLineRunner runDiscountRequestFile(DiscountService discountService, ObjectMapper objectMapper) {
        return args -> {
            System.out.println("=== Reading request file: " + REQUEST_FILE + " ===");
            try (InputStream in = new ClassPathResource(REQUEST_FILE).getInputStream()) {
                DiscountRequest request = objectMapper.readValue(in, DiscountRequest.class);
                DiscountResponse response = discountService.calculate(request);
                System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
            } catch (Exception e) {
                System.out.println("=== Error while processing " + REQUEST_FILE + " ===");
                System.out.println(e.getMessage());
            }
        };
    }

}
