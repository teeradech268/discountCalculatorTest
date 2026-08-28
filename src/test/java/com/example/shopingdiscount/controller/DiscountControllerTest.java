package com.example.shopingdiscount.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@AutoConfigureMockMvc
class DiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void calculate_fixedAmountCoupon_matchesPdfExample() throws Exception {
        String requestBody = """
                {
                  "items": [
                    {"name": "T-Shirt", "category": "CLOTHING", "unitPrice": 350},
                    {"name": "Hat", "category": "ACCESSORIES", "unitPrice": 250}
                  ],
                  "campaigns": [
                    {"type": "FIXED_AMOUNT_COUPON", "amount": 50}
                  ]
                }
                """;

        mockMvc.perform(post("/api/discount/calculate")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(550.0))
                .andExpect(jsonPath("$.items[0].name").value("T-Shirt"))
                .andExpect(jsonPath("$.campaigns[0].type").value("FIXED_AMOUNT_COUPON"));
    }

    @Test
    void calculate_multipleCampaignsInOrder_matchesExpectedTotal() throws Exception {
        String requestBody = """
                {
                  "items": [
                    {"name": "T-Shirt", "category": "CLOTHING", "unitPrice": 350},
                    {"name": "Hat", "category": "ACCESSORIES", "unitPrice": 250},
                    {"name": "Belt", "category": "ACCESSORIES", "unitPrice": 230}
                  ],
                  "campaigns": [
                    {"type": "SEASONAL", "every": 300, "discount": 40},
                    {"type": "POINTS_ON_TOP", "points": 68},
                    {"type": "FIXED_AMOUNT_COUPON", "amount": 50}
                  ]
                }
                """;

        // total 830 -> coupon 830-50=780 -> points 780-68=712 -> seasonal floor(712/300)*40=80 -> 632
        mockMvc.perform(post("/api/discount/calculate")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(632.0));
    }

    @Test
    void calculate_emptyItems_returns400() throws Exception {
        String requestBody = """
                {"items": [], "campaigns": []}
                """;

        mockMvc.perform(post("/api/discount/calculate")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculate_duplicateCampaignCategory_returns400() throws Exception {
        String requestBody = """
                {
                  "items": [{"name": "T-Shirt", "category": "CLOTHING", "unitPrice": 350}],
                  "campaigns": [
                    {"type": "FIXED_AMOUNT_COUPON", "amount": 50},
                    {"type": "PERCENTAGE_COUPON", "percentage": 10}
                  ]
                }
                """;

        mockMvc.perform(post("/api/discount/calculate")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculate_missingRequiredCampaignField_returns400() throws Exception {
        String requestBody = """
                {
                  "items": [{"name": "T-Shirt", "category": "CLOTHING", "unitPrice": 350}],
                  "campaigns": [
                    {"type": "SEASONAL", "every": 300}
                  ]
                }
                """;

        mockMvc.perform(post("/api/discount/calculate")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculate_malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/discount/calculate")
                        .contentType(APPLICATION_JSON)
                        .content("{not-json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculate_invalidCategoryEnum_returns400() throws Exception {
        String requestBody = """
                {
                  "items": [{"name": "T-Shirt", "category": "NOT_A_CATEGORY", "unitPrice": 350}],
                  "campaigns": []
                }
                """;

        mockMvc.perform(post("/api/discount/calculate")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
