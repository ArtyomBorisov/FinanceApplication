package by.itacademy.report.model;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ReportFile {
    private UUID id;
    private ByteArrayOutputStream data;

    public ReportFile(UUID id, ByteArrayOutputStream data) {
        this.id = id;
        this.data = data;
    }

    public UUID getId() {
        return id;
    }

    public ByteArrayOutputStream getData() {
        return data;
    }
}
