package ru.nasyrov.alfatest.service;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nasyrov.alfatest.client.Giphy;
import ru.nasyrov.alfatest.dto.giphy.GiphyResponseDto;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

@Service
public class GiphyService {

    private final Giphy giphyClient;

    @Value("${feign.giphy.param.applicationId}")
    private String applicationId;

    @Value("${feign.giphy.format}")
    private String format;

    @Autowired
    public GiphyService(Giphy giphyClient) {
        this.giphyClient = giphyClient;
    }

    /**
     * Метод для обращения в сервис и возврата gif по тегу
     *
     * @param tag тег для поиска
     * @return gif в виде массива байтов
     * @throws IOException если произошла ошибка при парсинге html-страницы или скачивании файла
     */
    public byte[] getGif(String tag) throws IOException {
        GiphyResponseDto responseDto = giphyClient.search(applicationId, tag, 1, RandomUtils.nextInt(0, 100));
        URL url = new URL(responseDto.getData()[0].getImages().get(format).getUrl());
        URL preparedUrl = prepareUrlToDownload(url);
        return downloadFromUrl(preparedUrl);
    }

    /**
     * Метод для возврата ссылки на скачивание
     *
     * @param raw ссылка на html страницу
     * @return ссылка на скачивание
     * @throws IOException если произошла ошибка при парсинге html-страницы
     */
    public URL prepareUrlToDownload(URL raw) throws IOException {
        Document document = Jsoup.parse(raw, 3000);
        Element element = document.selectFirst("meta[property=og:url][content]");
        return new URL(element.attr("content"));
    }

    /**
     * Метод для скачивания и возарата файла по ссылке
     *
     * @param url ссылка на скачивание
     * @return gif в виде массива байтов
     * @throws IOException если произошла ошибка при скачивании
     */
    public byte[] downloadFromUrl(URL url) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(url.openStream());
             ByteArrayInputStream bais = new ByteArrayInputStream(bis.readAllBytes())) {
            return IOUtils.toByteArray(bais);
        }
    }
}
