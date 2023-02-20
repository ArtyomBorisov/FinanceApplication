package by.itacademy.report.service;

import by.itacademy.report.dto.FileData;

public interface FileStorageService {
    void upload(FileData fileData);

    FileData download(String fileName);
}
