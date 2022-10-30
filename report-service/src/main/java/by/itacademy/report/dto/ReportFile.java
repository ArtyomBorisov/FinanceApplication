package by.itacademy.report.dto;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ReportFile {
    private UUID id;
    private ByteArrayOutputStream data;
    private String user;

    public ReportFile(UUID id, ByteArrayOutputStream data, String user) {
        this.id = id;
        this.data = data;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public ByteArrayOutputStream getData() {
        return data;
    }

    public String getUser() {
        return user;
    }
}
