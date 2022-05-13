package by.itacademy.account.scheduler.service.scheduler;

import by.itacademy.account.scheduler.model.Schedule;
import by.itacademy.account.scheduler.service.api.ISchedulerService;
import org.quartz.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class SchedulerService implements ISchedulerService {

    private final ConversionService conversionService;
    private final Scheduler scheduler;

    public SchedulerService(ConversionService conversionService, Scheduler scheduler) {
        this.conversionService = conversionService;
        this.scheduler = scheduler;
    }

    @Override
    public void addScheduledOperation(Schedule schedule, UUID idScheduledOperation) {
        JobDetail job = JobBuilder.newJob(CreateOperationJob.class)
                .withIdentity(idScheduledOperation.toString(), "operations")
                .usingJobData("operation", idScheduledOperation.toString())
                .build();

        long interval = schedule.getInterval();
        LocalDateTime startTime = schedule.getStartTime();

        TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger()
                .withIdentity(idScheduledOperation.toString(), "operations");

        if (startTime == null) {
            builder.startNow();
            startTime = LocalDateTime.now();
        } else {
            builder.startAt(this.conversionService.convert(startTime, Date.class));
        }

        if (interval > 0 && schedule.getTimeUnit() != null) {
            SimpleScheduleBuilder ssb = null;
            CronScheduleBuilder csb = null;
            String expression;

            switch (schedule.getTimeUnit()) {
                case SECOND:
                    ssb = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds((int) interval);
                    break;
                case MINUTE:
                    ssb = SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes((int) interval);
                    break;
                case HOUR:
                    ssb = SimpleScheduleBuilder.simpleSchedule().withIntervalInHours((int) interval);
                    break;
                case DAY:
                    ssb = SimpleScheduleBuilder.simpleSchedule().withIntervalInHours((int) (interval * 24));
                    break;
                case WEEK:
                    ssb = SimpleScheduleBuilder.simpleSchedule().withIntervalInHours((int) (interval * 24 * 7));
                    break;
                case MONTH:
                    expression = String.format("%d %d %d %d * ?",
                            startTime.getSecond(),
                            startTime.getMinute(),
                            startTime.getHour(),
                            startTime.getDayOfMonth());
                    csb = CronScheduleBuilder.cronSchedule(expression);
                    break;
                case YEAR:
                    expression = String.format("%d %d %d %d %d ?",
                            startTime.getSecond(),
                            startTime.getMinute(),
                            startTime.getHour(),
                            startTime.getDayOfMonth(),
                            startTime.getMonthValue());
                    csb = CronScheduleBuilder.cronSchedule(expression);
                    break;
            }

            if (ssb != null) {
                builder.withSchedule(ssb.repeatForever());
            } else if (csb != null) {
                builder.withSchedule(csb);
            }
        }

        if (schedule.getStopTime() != null) {
            builder.endAt(this.conversionService.convert(schedule.getStopTime(), Date.class));
        }

        Trigger trigger = builder.build();

        try {
            this.scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("Ошибка создания запланированной операции", e);
        }
    }

    @Override
    public void deleteScheduledOperation(UUID idScheduledOperation) {
        TriggerKey triggerKey = new TriggerKey(idScheduledOperation.toString(), "operations");

        try {
            this.scheduler.deleteJob(new JobKey(idScheduledOperation.toString(), "operations"));
            this.scheduler.unscheduleJob(triggerKey);
        } catch (SchedulerException e) {
            throw new RuntimeException("Ошибка удаления запланированной операции", e);
        }
    }
}
