package by.itacademy.account.scheduler.service.scheduler;

import by.itacademy.account.scheduler.model.Operation;
import by.itacademy.account.scheduler.model.ScheduledOperation;
import by.itacademy.account.scheduler.service.ScheduledOperationService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@Transactional
public class CreateOperationJob implements Job {

    private final ScheduledOperationService scheduledOperationService;
    private final RestTemplate restTemplate;

    public CreateOperationJob(ScheduledOperationService scheduledOperationService) {
        this.scheduledOperationService = scheduledOperationService;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String idOperation = context.getMergedJobDataMap().getString("operation");
        ScheduledOperation scheduledOperation = this.scheduledOperationService.get(UUID.fromString(idOperation));

        String url = "http://localhost:8080/account/" + scheduledOperation.getOperation().getAccount() + "/operation/";
        HttpEntity<Operation> request = new HttpEntity<>(scheduledOperation.getOperation());
        this.restTemplate.postForObject(url, request, String.class);
    }
}
