package by.itacademy.telegram.service.impl;

import by.itacademy.telegram.dto.LoginDto;
import by.itacademy.telegram.service.RestService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class RestServiceImpl implements RestService {

    private final RestTemplate restTemplate;

    private final String urlForSignIn = "";

    public RestServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String signIn(LoginDto loginDto) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(urlForSignIn, HttpMethod.POST, entity, String.class).getBody();
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException();
        }

    }
}
