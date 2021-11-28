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
import ru.nasyrov.alfatest.client.Giphy;
import ru.nasyrov.alfatest.dto.giphy.GiphyResponseDto;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
class GiphyServiceTest {

    @MockBean
    Giphy giphyServiceClient;

    @SpyBean
    GiphyService service;

    @Value("${feign.giphy.param.applicationId}")
    String applicationId;

    static GiphyResponseDto returnedDto;

    static File testGif;

    @BeforeAll
    static void createTestEntities() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = ExchangeRateServiceTest.class.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("GiphyResponseDefault.json")).getFile());
        returnedDto = objectMapper.readValue(file, GiphyResponseDto.class);
        testGif = new File(Objects.requireNonNull(classLoader.getResource("giphy.gif")).getFile());
    }

    @Test
    void getGif() throws Exception {
        Mockito.doReturn(testGif.toURI().toURL()).when(service).prepareUrlToDownload(any(URL.class));
        Mockito.doReturn(Files.readAllBytes(testGif.toPath())).when(service).downloadFromUrl(any(URL.class));
        when(giphyServiceClient.search(anyString(), anyString(), anyInt(), anyInt())).thenReturn(returnedDto);
        byte[] bytes = Files.readAllBytes(testGif.toPath());

        Assertions.assertEquals(Arrays.hashCode(service.getGif(anyString())), Arrays.hashCode(bytes));
        verify(giphyServiceClient, times(1)).search(anyString(), anyString(), anyInt(), anyInt());
        verify(service, times(1)).prepareUrlToDownload(any(URL.class));
        verify(service, times(1)).downloadFromUrl(any(URL.class));
    }

    @Test
    void downloadFromUrl() throws IOException {
        Assertions.assertArrayEquals(service.downloadFromUrl(testGif.toURI().toURL()), Files.readAllBytes(testGif.toPath()));
    }
}