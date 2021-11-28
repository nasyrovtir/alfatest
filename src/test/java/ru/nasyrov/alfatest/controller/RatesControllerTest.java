package ru.nasyrov.alfatest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.nasyrov.alfatest.service.ExchangeRateService;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RatesController.class)
class RatesControllerTest {

    @MockBean
    ExchangeRateService rateService;

    @Autowired
    MockMvc mockMvc;

    static byte[] returnedBytes = new byte[] {1, 1, 1};

    @Test
    void getResponseWithGif() throws Exception {
        when(rateService.getCorrectGif(eq("RUB"))).thenReturn(ResponseEntity.ok(returnedBytes));

        MvcResult mvcResult = mockMvc.perform(get("/api/gif")
                                     .param("base", "RUB"))
                                     .andExpect(status().isOk())
                                     .andReturn();
        byte[] bytes = mvcResult.getResponse().getContentAsByteArray();

        assertArrayEquals(bytes, returnedBytes);
        verify(rateService, times(1)).getCorrectGif(eq("RUB"));
    }

    @Test
    void getResponseWithServerError() throws Exception {
        when(rateService.getCorrectGif(eq("RUB"))).thenReturn(ResponseEntity.internalServerError().build());

        mockMvc.perform(get("/api/gif")
                                    .param("base", "RUB"))
                                    .andExpect(status().isInternalServerError())
                                    .andReturn();
        verify(rateService, times(1)).getCorrectGif(eq("RUB"));
    }

    @Test
    void getResponseWithBadRequest() throws Exception {
        when(rateService.getCorrectGif(eq("XXX"))).thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(get("/api/gif")
                                    .param("base", "XXX"))
                                    .andExpect(status().isBadRequest())
                                    .andReturn();
        verify(rateService, times(1)).getCorrectGif(eq("XXX"));
    }



}