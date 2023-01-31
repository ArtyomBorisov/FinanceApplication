package by.itacademy.classifier.utils;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Generator {
    UUID generateUUID();
    LocalDateTime now();
}
