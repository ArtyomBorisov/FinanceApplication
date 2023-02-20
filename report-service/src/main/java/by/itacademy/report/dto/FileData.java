package by.itacademy.report.dto;

public class FileData {
    private String name;
    private byte[] data;

    public FileData(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }
}
