package ru.nasyrov.alfatest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.nasyrov.alfatest.client.OpenExchangeRates;
import ru.nasyrov.alfatest.dto.currencyRate.CurrencyRateResponseDto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class ExchangeRateService {

    private final OpenExchangeRates openExchangeRatesClient;
    private final GiphyService giphyServiceClient;

    @Value("${feign.openExchangeRates.param.baseCurrency}")
    private String baseCurrency;

    @Value("${feign.openExchangeRates.param.applicationId}")
    private String applicationId;

    @Value("${feign.giphy.param.tag.positive}")
    private String positiveTag;

    @Value("${feign.giphy.param.tag.negative}")
    private String negativeTag;

    @Autowired
    public ExchangeRateService(OpenExchangeRates openExchangeRatesClient, GiphyService giphyServiceClient) {
        this.openExchangeRatesClient = openExchangeRatesClient;
        this.giphyServiceClient = giphyServiceClient;
    }

    /**
     * Метод для определения повышения/понижения курса валюты относительно вчерашней даты
     *
     * @param requiredCurrency код валюты
     * @return true если курс валюты повысился
     * @throws IllegalArgumentException если в сервисе нет такой валюты
     */
    public boolean isRateIncreasedRelativeToYesterday(String requiredCurrency) throws IllegalArgumentException {
        CurrencyRateResponseDto todayRate = openExchangeRatesClient.latest(applicationId, baseCurrency, requiredCurrency);
        LocalDate yesterdayDate = getYesterdayDate(todayRate.getTimestamp());
        CurrencyRateResponseDto yesterdayRate = openExchangeRatesClient.historical(yesterdayDate.toString(), applicationId, baseCurrency, requiredCurrency);
        if (todayRate.getRates().isEmpty() || yesterdayRate.getRates().isEmpty()) {
            throw new IllegalArgumentException();
        }
        return todayRate.getRates().get(requiredCurrency) > yesterdayRate.getRates().get(requiredCurrency);
    }

    /**
     * Метод для определения вчерашней даты
     *
     * @param timestamp Unix время, секунды
     * @return вчерашняя дата
     */
    public LocalDate getYesterdayDate(Long timestamp) {
        LocalDate todayDate = Instant.ofEpochMilli(timestamp * 1000).atZone(ZoneOffset.UTC).toLocalDate();
        return todayDate.minusDays(1);
    }

    /**
     * Метод для возврата gif в зависимости от курса
     *
     * @param requiredCurrency код валюты
     * @return gif в виде массива байтов
     */
    public ResponseEntity<byte[]> getCorrectGif(String requiredCurrency) {
        try {
            if (isRateIncreasedRelativeToYesterday(requiredCurrency)) {
                return ResponseEntity.ok(giphyServiceClient.getGif(positiveTag));
            } else {
                return ResponseEntity.ok(giphyServiceClient.getGif(negativeTag));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
