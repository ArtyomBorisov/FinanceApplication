package by.itacademy.account.scheduler.service.impl;

import by.itacademy.account.scheduler.utils.impl.JwtTokenUtil;
import by.itacademy.account.scheduler.dto.Operation;
import by.itacademy.account.scheduler.dto.ScheduledOperation;
import by.itacademy.account.scheduler.dao.ScheduledOperationRepository;
import by.itacademy.account.scheduler.dao.entity.ScheduledOperationEntity;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@Transactional
public class CreateOperationJob implements Job {

    private final ScheduledOperationRepository scheduledOperationRepository;
    private final ConversionService conversionService;
    private final String accountBackendUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public CreateOperationJob(ScheduledOperationRepository scheduledOperationRepository,
                              ConversionService conversionService,
                              @Value("${account_url}") String accountBackendUrl) {
        this.scheduledOperationRepository = scheduledOperationRepository;
        this.conversionService = conversionService;
        this.accountBackendUrl = accountBackendUrl;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String idOperation = context.getMergedJobDataMap().getString("operation");
        UUID uuid = UUID.fromString(idOperation);
        ScheduledOperationEntity entity = scheduledOperationRepository.getById(uuid);
        ScheduledOperation scheduledOperation = conversionService.convert(entity, ScheduledOperation.class);
        Operation operation = scheduledOperation.getOperation();
        String url = accountBackendUrl + "/" + operation.getAccount() + "/operation";
        HttpHeaders headers = getHeaders(operation.getUser());

        Operation operationForPost = Operation.Builder.createBuilder()
                .setDescription(operation.getDescription())
                .setCategory(operation.getCategory())
                .setCurrency(operation.getCurrency())
                .setValue(operation.getValue())
                .build();

        HttpEntity<Operation> request = new HttpEntity<>(operationForPost, headers);
        restTemplate.postForObject(url, request, String.class);
    }

    private HttpHeaders getHeaders(String login) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = JwtTokenUtil.generateAccessToken(login);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return headers;
    }
}
