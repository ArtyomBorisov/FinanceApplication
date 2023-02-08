package by.itacademy.account.config;

import by.itacademy.account.utils.impl.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(new JwtInterceptor());
        interceptors.add(new ContentTypeInterceptor(MediaType.APPLICATION_JSON));
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    private static class JwtInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request,
                                            byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
            UserDetails userDetails =
                    (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            String jwt = "Bearer " + JwtTokenUtil.generateAccessToken(userDetails);
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, jwt);
            return execution.execute(request, body);
        }
    }

    private static class ContentTypeInterceptor implements ClientHttpRequestInterceptor {

        private final MediaType type;

        private ContentTypeInterceptor(MediaType type) {
            this.type = type;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request,
                                            byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
            request.getHeaders().setContentType(type);
            return execution.execute(request, body);
        }
    }
}
