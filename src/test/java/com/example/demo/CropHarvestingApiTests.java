package com.example.demo;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.User;
import com.example.demo.repository.CropRepository;
import com.example.demo.repository.HarvestingRecordRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CropHarvestingApiTests {

    private static final String CROP_BODY = """
            {
              "cropName": "Wheat",
              "cropType": "Cereal",
              "cropVariety": "Durum",
              "fieldLocation": "North field",
              "cultivatedArea": 12.5,
              "sowingDate": "2026-01-10",
              "expectedHarvestDate": "2026-06-10",
              "farmerName": "Asha Farmer",
              "notes": "Winter crop"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private HarvestingRecordRepository harvestingRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void clearDatabase() {
        harvestingRecordRepository.deleteAll();
        cropRepository.deleteAll();
        userRepository.deleteAll();
        userService.registerFarmer(new RegisterRequest(
                "Test Farmer", "test@example.com", "test-password-long"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "FARMER")
    void cropAndHarvestCrudPreservesFrontendJsonContract() throws Exception {
        String cropResponse = mockMvc.perform(withCsrf(post("/api/crops"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CROP_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cropName").value("Wheat"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        long cropId = objectMapper.readTree(cropResponse).get("id").asLong();

        mockMvc.perform(get("/api/crops/{id}", cropId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expectedHarvestDate").value("2026-06-10"));

        mockMvc.perform(withCsrf(put("/api/crops/{id}", cropId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CROP_BODY.replace("\"cropName\": \"Wheat\"", "\"cropName\": \"Barley\"")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cropName").value("Barley"));

        String harvestBody = harvestBody(cropId, "Pending");
        String harvestResponse = mockMvc.perform(withCsrf(post("/api/harvests"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(harvestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.crop.id").value(cropId))
                .andExpect(jsonPath("$.crop.cropName").value("Barley"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode harvest = objectMapper.readTree(harvestResponse);
        long harvestId = harvest.get("id").asLong();

        mockMvc.perform(get("/api/harvests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].crop.id").value(cropId));

        mockMvc.perform(get("/api/harvests/{id}", harvestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plannedHarvestDate").value("2026-06-10"));

        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCrops").value(1))
                .andExpect(jsonPath("$.totalHarvestingRecords").value(1))
                .andExpect(jsonPath("$.pendingTasks").value(1));

        mockMvc.perform(withCsrf(put("/api/harvests/{id}", harvestId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(harvestBody(cropId, "Completed")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Completed"));

        mockMvc.perform(withCsrf(delete("/api/crops/{id}", cropId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());

        mockMvc.perform(withCsrf(delete("/api/harvests/{id}", harvestId)))
                .andExpect(status().isNoContent());
        mockMvc.perform(withCsrf(delete("/api/crops/{id}", cropId)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/crops"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "FARMER")
    void invalidRequestAndMissingResourcesReturnUsefulClientErrors() throws Exception {
        mockMvc.perform(withCsrf(post("/api/crops"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.cropName").exists());

        mockMvc.perform(get("/api/crops/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Crop not found with id: 999999"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "FARMER")
    void frontendOriginCanMakeCorsPreflightRequests() throws Exception {
        mockMvc.perform(options("/api/crops")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void anonymousRequestsAreRejectedAndRegistrationCannotAssignAdminRole() throws Exception {
        mockMvc.perform(get("/api/crops"))
                .andExpect(status().isUnauthorized());

        String registration = """
                {
                  "name": "New Farmer",
                  "email": "new@example.com",
                  "password": "a-very-long-password",
                  "role": "ADMIN"
                }
                """;
        MvcResult result = mockMvc.perform(withCsrf(post("/api/auth/register"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registration))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("FARMER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.role").value("FARMER"));

        mockMvc.perform(withCsrf(post("/api/auth/logout")).session(session))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/crops").session(session))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "FARMER")
    void farmerCanOnlyReadAndChangeTheirOwnCropAndHarvestRecords() throws Exception {
        User secondFarmer = userService.registerFarmer(new RegisterRequest(
                "Second Farmer", "second@example.com", "another-long-password"));
        String cropResponse = mockMvc.perform(withCsrf(post("/api/crops"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CROP_BODY))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        long cropId = objectMapper.readTree(cropResponse).get("id").asLong();

        mockMvc.perform(get("/api/crops").with(org.springframework.security.test.web.servlet.request
                        .SecurityMockMvcRequestPostProcessors.user(secondFarmer.getEmail()).roles("FARMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        mockMvc.perform(get("/api/crops/{id}", cropId).with(org.springframework.security.test.web.servlet.request
                        .SecurityMockMvcRequestPostProcessors.user(secondFarmer.getEmail()).roles("FARMER")))
                .andExpect(status().isNotFound());
        mockMvc.perform(withCsrf(delete("/api/crops/{id}", cropId))
                        .with(org.springframework.security.test.web.servlet.request
                                .SecurityMockMvcRequestPostProcessors.user(secondFarmer.getEmail()).roles("FARMER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void csrfIsRequiredForStateChangingRequests() throws Exception {
        mockMvc.perform(post("/api/crops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CROP_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void csrfEndpointReturnsTokenThatCanBeUsedByBrowserRequests() throws Exception {
        MvcResult csrfResult = mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode csrf = objectMapper.readTree(csrfResult.getResponse().getContentAsString());
        Cookie csrfCookie = csrfResult.getResponse().getCookie("XSRF-TOKEN");

        assertNotNull(csrfCookie);
        assertEquals(csrfCookie.getValue(), csrf.get("token").asText());

        mockMvc.perform(post("/api/auth/login")
                        .cookie(csrfCookie)
                        .header(csrf.get("headerName").asText(), csrf.get("token").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"missing@example.com","password":"incorrect-password"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    private MockHttpServletRequestBuilder withCsrf(MockHttpServletRequestBuilder request) {
        String token = "test-csrf-token";
        Cookie cookie = new MockCookie("XSRF-TOKEN", token);
        return request.cookie(cookie).header("X-XSRF-TOKEN", token);
    }

    private String harvestBody(long cropId, String status) {
        return """
                {
                  "cropId": %d,
                  "farmerName": "Asha Farmer",
                  "fieldLocation": "North field",
                  "plannedHarvestDate": "2026-06-10",
                  "actualHarvestDate": null,
                  "harvestedQuantity": 900.5,
                  "status": "%s",
                  "notes": "Ready to harvest"
                }
                """.formatted(cropId, status);
    }
}
