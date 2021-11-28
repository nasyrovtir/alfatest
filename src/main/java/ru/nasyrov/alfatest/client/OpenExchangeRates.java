package ru.nasyrov.alfatest.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nasyrov.alfatest.dto.currencyRate.CurrencyRateResponseDto;

@FeignClient(name = "openExchangeRates", url = "${feign.openExchangeRates.url}")
public interface OpenExchangeRates {

    /**
     * Метод для обращения в сервис openExchangeRates и получения курса валюты за определенню дату
     *
     * @param date             дата
     * @param applicationId    ключ доступа
     * @param baseCurrency     код базовой валюты
     * @param requiredCurrency код валюты
     * @return объект DTO на основе ответа JSON
     */
    @GetMapping("/api/historical/{date}.json")
    CurrencyRateResponseDto historical(@PathVariable String date,
                                       @RequestParam("app_id") String applicationId,
                                       @RequestParam("base") String baseCurrency,
                                       @RequestParam("symbols") String requiredCurrency);

    /**
     * Метод для обращения в сервис openExchangeRates и получения курса валюты на текущее время
     *
     * @param applicationId    ключ доступа
     * @param baseCurrency     код базовой валюты
     * @param requiredCurrency код валюты
     * @return объект DTO на основе ответа JSON
     */
    @GetMapping("/api/latest.json")
    CurrencyRateResponseDto latest(@RequestParam("app_id") String applicationId,
                                   @RequestParam("base") String baseCurrency,
                                   @RequestParam("symbols") String requiredCurrency);
}
