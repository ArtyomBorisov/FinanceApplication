package by.itacademy.report.service.impl;

import by.itacademy.report.constant.MessageError;
import by.itacademy.report.dto.FileData;
import by.itacademy.report.exception.ServerException;
import by.itacademy.report.service.FileStorageService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class MinioFileStorageService implements FileStorageService {

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioFileStorageService(MinioClient minioClient,
                                   @Value("${minio.bucket}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    @Override
    public void upload(FileData fileData) {
        byte[] data = fileData.getData();
        try (InputStream stream = new ByteArrayInputStream(data)) {
            PutObjectArgs objectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileData.getName())
                    .stream(stream, data.length, -1)
                    .build();
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            throw new ServerException(MessageError.REPORT_UPLOADING_EXCEPTION, e);
        }
    }

    @Override
    public FileData download(String fileName) {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build();

        try (InputStream stream =  minioClient.getObject(args)) {
            byte[] bytes = stream.readAllBytes();
            return new FileData(fileName, bytes);
        } catch (Exception e) {
            throw new ServerException(MessageError.REPORT_DOWNLOADING_EXCEPTION, e);
        }
    }
}
























