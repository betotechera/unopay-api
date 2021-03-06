package br.com.unopay.api.fileuploader.service;

import br.com.unopay.api.fileuploader.model.UploadConfiguration;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.InternalServerErrorException;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.amazonaws.AmazonClientException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;
import javax.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceRecord.SEPARATOR;

@Slf4j
@Component
public class FileUploaderService extends AbstractUploadService {

    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(15);

    private SimpleDateFormat folderDateFormat = new SimpleDateFormat("yyyy_MM_dd");

    @Resource(name = "configs")
    private Map<String, UploadConfiguration> configs;

    private AmazonS3Service amazonS3Service;

    @Value("${amazon.s3.cdn.uri}")
    private String cdnUri;

    @Autowired
    public FileUploaderService(AmazonS3Service amazonS3Service) {
        this.amazonS3Service = amazonS3Service;
    }

    public String uploadFile(MultipartFile file, String filePath) {
        log.info("uploading file {}", filePath);
        byte[] content = getBytes(file);
        return uploadBytes(filePath, content);
    }

    public String uploadBytes(String filePath, byte[] content) {
        try {
            FORK_JOIN_POOL.submit(() -> {
                List<Runnable> runnable = Collections.singletonList(() -> amazonS3Service.upload(filePath, content));
                runnable.parallelStream().forEach(Runnable::run);
            }).get();
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error uploading file '{}' to buckets", filePath, e);
            throw new InternalServerErrorException(e.getMessage(), e);
        }
        return getURLAsString(filePath);
    }

    @SneakyThrows
    public String uploadCnab(String generate, String fileUri) {
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Stream.of(generate.split(SEPARATOR)).forEach(line -> write(outputStream, line));
            return uploadBytes(fileUri, outputStream.toByteArray());
        }
    }

    public String upload(MultipartFile file, String service) {
        String relativePath = getRelativePath(service);
        String uploadName = String.format("%s-%s",UUID.randomUUID().toString(),
                slugfyIgnoringExtension(file.getOriginalFilename()));
        return uploadFile(file, String.format("%s/%s",relativePath, uploadName));
    }

    public void deleteFile(String filePath) {
        try {
            amazonS3Service.delete(filePath);
        } catch (AmazonClientException e) {
            log.error("Error removing file '" + filePath + "' from amazon buckets", e);
            throw new InternalServerErrorException(e.getMessage(), e);
        }
    }

    public String getRelativePath(String service) {
        UploadConfiguration configuration = configs.get(service);
        if (configuration != null) {
            return String.format("%s/%s", configuration.getFolder(), folderDateFormat.format(new Date()));
        }
        throw UnovationExceptions.unprocessableEntity().withArguments(service)
                .withErrors(Errors.FILE_SERVICE_NOT_CONFIGURED);
    }


    private String getURLAsString(String filePath) {
        return String.format("%s/%s",cdnUri.replaceFirst("/$", ""),filePath.replaceFirst("^/", ""));
    }

    @SneakyThrows
    private void write(ByteArrayOutputStream outputStream, String line) {
        outputStream.write(line.concat("\r\n").getBytes());
        outputStream.flush();
    }

    private byte[] getBytes(MultipartFile file) {
        byte[] content;
        try {
            content = file.getBytes();
        } catch (IOException e) {
            throw new InternalServerErrorException(e.getMessage(), e);
        }
        return content;
    }

}
