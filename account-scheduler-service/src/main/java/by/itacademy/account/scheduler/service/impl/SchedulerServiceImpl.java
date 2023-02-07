package by.itacademy.account.scheduler.service.impl;

import by.itacademy.account.scheduler.constant.TimeUnit;
import by.itacademy.account.scheduler.dto.Schedule;
import by.itacademy.account.scheduler.constant.MessageError;
import by.itacademy.account.scheduler.exception.ServerException;
import by.itacademy.account.scheduler.service.SchedulerService;
import by.itacademy.account.scheduler.utils.Generator;
import org.quartz.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    private final ConversionService conversionService;
    private final Scheduler scheduler;
    private final Generator generator;

    private static final String OPERATIONS = "operations";

    public SchedulerServiceImpl(ConversionService conversionService,
                                Scheduler scheduler,
                                Generator generator) {
        this.conversionService = conversionService;
        this.scheduler = scheduler;
        this.generator = generator;
    }

    @Override
    public void addScheduledOperation(Schedule schedule, UUID idScheduledOperation) {
        JobDetail job = JobBuilder.newJob(CreateOperationJob.class)
                .withIdentity(idScheduledOperation.toString(), OPERATIONS)
                .usingJobData("operation", idScheduledOperation.toString())
                .build();

        long interval = schedule.getInterval();
        LocalDateTime startTime = schedule.getStartTime();

        TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger()
                .withIdentity(idScheduledOperation.toString(), OPERATIONS);

        if (startTime == null) {
            builder.startNow();
            startTime = generator.now();
        } else {
            Date startDate = conversionService.convert(startTime, Date.class);
            builder.startAt(startDate);
        }

        if (interval > 0 && schedule.getTimeUnit() != null) {
            ScheduleBuilder<?> scheduleBuilder = getSchedule(schedule.getTimeUnit(), (int) interval, startTime);

            if (scheduleBuilder != null) {
                builder.withSchedule(scheduleBuilder);
            }
        }

        if (schedule.getStopTime() != null) {
            Date stopDate = conversionService.convert(schedule.getStopTime(), Date.class);
            builder.endAt(stopDate);
        }

        Trigger trigger = builder.build();

        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new ServerException(MessageError.SCHEDULED_OPERATION_CREATING_EXCEPTION, e);
        }
    }

    @Override
    public void deleteScheduledOperation(UUID idScheduledOperation) {
        TriggerKey triggerKey = new TriggerKey(idScheduledOperation.toString(), OPERATIONS);

        try {
            scheduler.deleteJob(new JobKey(idScheduledOperation.toString(), OPERATIONS));
            scheduler.unscheduleJob(triggerKey);
        } catch (SchedulerException e) {
            throw new ServerException(MessageError.SCHEDULED_OPERATION_DELETING_EXCEPTION, e);
        }
    }

    private ScheduleBuilder<?> getSchedule(TimeUnit timeUnit, int interval, LocalDateTime startTime) {
        switch (timeUnit) {
            case SECOND:
                return SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval).repeatForever();
            case MINUTE:
                return SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(interval).repeatForever();
            case HOUR:
                return SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(interval).repeatForever();
            case DAY:
                return SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(interval * 24).repeatForever();
            case WEEK:
                return SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(interval * 24 * 7).repeatForever();
            case MONTH:
                String expression = String.format("%d %d %d %d * ?",
                        startTime.getSecond(),
                        startTime.getMinute(),
                        startTime.getHour(),
                        startTime.getDayOfMonth());
                return CronScheduleBuilder.cronSchedule(expression);
            case YEAR:
                expression = String.format("%d %d %d %d %d ?",
                        startTime.getSecond(),
                        startTime.getMinute(),
                        startTime.getHour(),
                        startTime.getDayOfMonth(),
                        startTime.getMonthValue());
                return CronScheduleBuilder.cronSchedule(expression);
        }

        return null;
    }
}
