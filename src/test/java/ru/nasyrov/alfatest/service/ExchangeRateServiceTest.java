package ru.nasyrov.alfatest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import ru.nasyrov.alfatest.client.OpenExchangeRates;
import ru.nasyrov.alfatest.dto.currencyRate.CurrencyRateResponseDto;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
class ExchangeRateServiceTest {

    @MockBean
    OpenExchangeRates openExchangeRatesClient;

    @MockBean
    GiphyService giphyServiceClient;

    @SpyBean
    ExchangeRateService service;

    @Value("${feign.giphy.param.tag.positive}")
    String positiveTag;

    @Value("${feign.giphy.param.tag.negative}")
    String negativeTag;

    @Value("${feign.openExchangeRates.param.baseCurrency}")
    String baseCurrency;

    @Value("${feign.openExchangeRates.param.applicationId}")
    String applicationId;

    static CurrencyRateResponseDto returnedDtoHighRate;

    static CurrencyRateResponseDto returnedDtoLowRate;

    static CurrencyRateResponseDto returnedIllegalArgRate;

    static byte[] returnedBytesWhenIncrease = new byte[] {1, 1, 1};

    static byte[] returnedBytesWhenDecrease = new byte[] {0, 0, 0};

    @BeforeAll
    static void createTestDto() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = ExchangeRateServiceTest.class.getClassLoader();

        File upFile = new File(Objects.requireNonNull(classLoader.getResource("CurrencyRateResponseUp.json")).getFile());
        File downFile = new File(Objects.requireNonNull(classLoader.getResource("CurrencyRateResponseDown.json")).getFile());
        File illegalArgFile = new File(Objects.requireNonNull(classLoader.getResource("CurrencyRateResponseIllegalArg.json")).getFile());

        returnedDtoHighRate = objectMapper.readValue(upFile, CurrencyRateResponseDto.class);
        returnedDtoLowRate = objectMapper.readValue(downFile, CurrencyRateResponseDto.class);
        returnedIllegalArgRate = objectMapper.readValue(illegalArgFile, CurrencyRateResponseDto.class);
    }

    @Test
    void isRateIncreasedRelativeToYesterday_whenRateIncrease() {
        when(openExchangeRatesClient.latest(eq(applicationId), eq(baseCurrency), eq("RUB"))).thenReturn(returnedDtoHighRate); //today
        when(openExchangeRatesClient.historical(anyString(), eq(applicationId), eq(baseCurrency), eq("RUB"))).thenReturn(returnedDtoLowRate); //yesterday

        Assertions.assertTrue(service.isRateIncreasedRelativeToYesterday("RUB"));
        verify(openExchangeRatesClient, times(1)).latest(eq(applicationId), eq(baseCurrency), eq("RUB"));
        verify(openExchangeRatesClient, times(1)).historical(anyString(), eq(applicationId), eq(baseCurrency), eq("RUB"));
        verify(service, times(1)).getYesterdayDate(anyLong());
    }

    @Test
    void isRateIncreasedRelativeToYesterday_whenRateDecrease() {
        when(openExchangeRatesClient.latest(eq(applicationId), eq(baseCurrency), eq("RUB"))).thenReturn(returnedDtoLowRate); //today
        when(openExchangeRatesClient.historical(anyString(), eq(applicationId), eq(baseCurrency), eq("RUB"))).thenReturn(returnedDtoHighRate); //yesterday

        Assertions.assertFalse(service.isRateIncreasedRelativeToYesterday("RUB"));
        verify(openExchangeRatesClient, times(1)).latest(eq(applicationId), eq(baseCurrency), eq("RUB"));
        verify(openExchangeRatesClient, times(1)).historical(anyString(), eq(applicationId), eq(baseCurrency), eq("RUB"));
        verify(service, times(1)).getYesterdayDate(anyLong());
    }

    @Test
    void isRateIncreasedRelativeToYesterday_expectException() {
        when(openExchangeRatesClient.latest(eq(applicationId), eq(baseCurrency), eq("XXX"))).thenReturn(returnedIllegalArgRate); //today
        when(openExchangeRatesClient.historical(anyString(), eq(applicationId), eq(baseCurrency), eq("XXX"))).thenReturn(returnedIllegalArgRate); //yesterday

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.isRateIncreasedRelativeToYesterday("XXX"));
        verify(openExchangeRatesClient, times(1)).latest(eq(applicationId), eq(baseCurrency), eq("XXX"));
        verify(openExchangeRatesClient, times(1)).historical(anyString(), eq(applicationId), eq(baseCurrency), eq("XXX"));
        verify(service, times(1)).getYesterdayDate(anyLong());
    }

    @Test
    void getCorrectGif_whenRateIncrease() throws Exception {
        Mockito.doReturn(true).when(service).isRateIncreasedRelativeToYesterday(anyString());
        when(giphyServiceClient.getGif(eq(positiveTag))).thenReturn(returnedBytesWhenIncrease);

        Assertions.assertArrayEquals(service.getCorrectGif(anyString()).getBody(), returnedBytesWhenIncrease);
        verify(giphyServiceClient, times(1)).getGif(eq(positiveTag));
        verify(service, times(1)).isRateIncreasedRelativeToYesterday(anyString());
    }


    @Test
    void getCorrectGif_whenRateDecrease() throws IOException {
        Mockito.doReturn(false).when(service).isRateIncreasedRelativeToYesterday(anyString());
        when(giphyServiceClient.getGif(eq(negativeTag))).thenReturn(returnedBytesWhenDecrease);

        Assertions.assertArrayEquals(service.getCorrectGif(anyString()).getBody(), returnedBytesWhenDecrease);
        verify(giphyServiceClient, times(1)).getGif(eq(negativeTag));
        verify(service, times(1)).isRateIncreasedRelativeToYesterday(anyString());
    }

    @Test
    void getCorrectGif_whenReturnsIllegalArgException() throws IOException {
        Mockito.doThrow(IllegalArgumentException.class).when(service).isRateIncreasedRelativeToYesterday(anyString());

        Assertions.assertEquals(service.getCorrectGif(anyString()).getStatusCode(), HttpStatus.BAD_REQUEST);
        verify(service, times(1)).isRateIncreasedRelativeToYesterday(anyString());
        verify(giphyServiceClient, never()).getGif(anyString());
    }

    @Test
    void getCorrectGif_whenReturnsAnyException() throws IOException {
        Mockito.doReturn(true).when(service).isRateIncreasedRelativeToYesterday(anyString());
        Mockito.doThrow(IOException.class).when(giphyServiceClient).getGif(anyString());

        Assertions.assertEquals(service.getCorrectGif(anyString()).getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        verify(service, times(1)).isRateIncreasedRelativeToYesterday(anyString());
        verify(giphyServiceClient, times(1)).getGif(anyString());
    }

    @Test
    void getYesterdayDate() {
        LocalDateTime localDateTime = LocalDateTime.of(2011, 11, 11, 11, 11, 11);
        Long timestamp = localDateTime.toEpochSecond(ZoneOffset.UTC);
        LocalDate localDateToday = localDateTime.toLocalDate();
        LocalDate localDateYesterday = localDateToday.minusDays(1);

        Assertions.assertEquals(service.getYesterdayDate(timestamp), localDateYesterday);
    }
}