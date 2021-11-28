package ru.nasyrov.alfatest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.nasyrov.alfatest.service.ExchangeRateService;

@RestController
@RequestMapping("/api")
public class RatesController {

    private final ExchangeRateService rateService;

    @Autowired
    public RatesController(ExchangeRateService rateService) {
        this.rateService = rateService;
    }

    /**
     * Метод для возврата ответа сервиса
     *
     * @param requiredCurrency код валюты
     * @return gif в виде массива байтов
     */
    @GetMapping(value = "/gif", produces = MediaType.IMAGE_GIF_VALUE)
    public ResponseEntity<byte[]> gif(@RequestParam("base") String requiredCurrency) {

        return rateService.getCorrectGif(requiredCurrency);
    }
}
