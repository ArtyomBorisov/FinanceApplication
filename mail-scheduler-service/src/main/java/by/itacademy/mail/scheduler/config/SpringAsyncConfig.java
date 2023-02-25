package by.itacademy.mail.scheduler.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.reflect.Method;

@Configuration
@EnableAsync
public class SpringAsyncConfig implements AsyncConfigurer {

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

    private static class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

        private final Logger logger = LogManager.getLogger(CustomAsyncExceptionHandler.class);

        @Override
        public void handleUncaughtException(Throwable ex, Method method, Object... params) {
            logger.error("{}: {}; {}", ex.getClass().getSimpleName(), ex.getMessage(), method.getName());
        }
    }
}
