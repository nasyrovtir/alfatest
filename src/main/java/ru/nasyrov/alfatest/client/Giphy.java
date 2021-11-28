package ru.nasyrov.alfatest.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nasyrov.alfatest.dto.giphy.GiphyResponseDto;

@FeignClient(name = "giphy", url = "${feign.giphy.url}")
public interface Giphy {

    /**
     * Метод для обращения в сервис giphy и получения gif по определенному тегу
     *
     * @param applicationId ключ доступа
     * @param tag           тег для поиска
     * @param limit         ограничение по количеству gif файлов
     * @param offset        количество пропускаемых gif файлов
     * @return объект DTO на основе ответа JSON
     */
    @GetMapping("/v1/gifs/search")
    GiphyResponseDto search(@RequestParam("api_key") String applicationId,
                            @RequestParam("q") String tag,
                            @RequestParam("limit") int limit,
                            @RequestParam("offset") int offset);
}
