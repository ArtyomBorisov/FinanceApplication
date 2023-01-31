package by.itacademy.classifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "by.itacademy.classifier.repository")
public class ClassifierServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClassifierServiceApplication.class, args);
    }
}
