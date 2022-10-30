package by.itacademy.account.scheduler.service.scheduler;

import by.itacademy.account.scheduler.controller.web.controllers.utils.JwtTokenUtil;
import by.itacademy.account.scheduler.dto.Operation;
import by.itacademy.account.scheduler.dto.ScheduledOperation;
import by.itacademy.account.scheduler.repository.api.IScheduledOperationRepository;
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

    @Value("${account_backend_url}")
    private String accountBackendUrl;

    private final IScheduledOperationRepository scheduledOperationRepository;
    private final RestTemplate restTemplate;
    private final ConversionService conversionService;

    public CreateOperationJob(IScheduledOperationRepository scheduledOperationRepository,
                              ConversionService conversionService) {
        this.scheduledOperationRepository = scheduledOperationRepository;
        this.conversionService = conversionService;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String idOperation = context.getMergedJobDataMap().getString("operation");
        ScheduledOperation scheduledOperation = conversionService.convert(
                scheduledOperationRepository.getById(UUID.fromString(idOperation)),
                ScheduledOperation.class);

        Operation operation = scheduledOperation.getOperation();
        String url = accountBackendUrl + "/" + operation.getAccount() + "/operation";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = JwtTokenUtil.generateAccessToken(operation.getUser());
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        Operation operationForPost = Operation.Builder.createBuilder()
                .setDescription(operation.getDescription())
                .setCategory(operation.getCategory())
                .setCurrency(operation.getCurrency())
                .setValue(operation.getValue())
                .build();

        HttpEntity<Operation> request = new HttpEntity<>(operationForPost, headers);

        restTemplate.postForObject(url, request, String.class);
    }
}
