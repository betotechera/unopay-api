package br.com.unopay.api.fileuploader.service;

import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AmazonS3Service {

    private static final long ONE_EYAR = 31536000;

    private static final long MAX_AGE_IN_SECONDS = ONE_EYAR;

    @Value("${amazon.s3.bucketName}")
    private String bucketName;

    @Value("${amazon.s3.cdn.uri}")
    private String cdnUri;

    private TransferManager transferManager;

    @Autowired
    public AmazonS3Service(TransferManager transferManager) {
        this.transferManager = transferManager;
    }

    String upload(String objectKey, byte[] binaryFile) {
        try {
            Upload bucketUpload = uploadToBucket(bucketName, objectKey, binaryFile);
            bucketUpload.waitForUploadResult();
            return cdnUri.concat(objectKey);

        } catch (InterruptedException e) {
            log.info("Error on upload", e);
            throw UnovationExceptions.internalError();
        }
    }

    public void delete(String objectKey) {
        log.info("Removing file {} from Amazon bucket {}", objectKey, bucketName);
        transferManager.getAmazonS3Client().deleteObject(bucketName, objectKey);
    }

    @SneakyThrows
    private Upload uploadToBucket(String bucketName, String objectKey, byte[] binaryFile) {
        log.info("Sending file {} to bucket {}", objectKey, bucketName);
        ObjectMetadata metadata = new ObjectMetadata();
        PutObjectRequest request = createPutObjectRequest(bucketName, objectKey,
                new ByteArrayInputStream(binaryFile), metadata);
        addHeaders(metadata);
        setContentTypeAndContentLength(metadata, objectKey, binaryFile.length);
        return transferPutObjectRequest(request);
    }

    private void addHeaders(ObjectMetadata metadata) {
        addMaxAgeHeader(metadata);
        addPublicReadHeader(metadata);
    }

    private void addPublicReadHeader(ObjectMetadata objectMetadata) {
        setHeader(objectMetadata, Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());
    }

    private void addMaxAgeHeader(ObjectMetadata objectMetadata) {
        setHeader(objectMetadata, Headers.CACHE_CONTROL, String.format("max-age=%d", MAX_AGE_IN_SECONDS));
    }

    private static void setHeader(ObjectMetadata objectMetadata, String header, Object value) {
        objectMetadata.setHeader(header, value);
    }

    private static void setContentTypeAndContentLength(ObjectMetadata metadata, String fileName, long size) {
        metadata.setContentType(Mimetypes.getInstance().getMimetype(fileName));
        metadata.setContentLength(size);
    }

    private static PutObjectRequest createPutObjectRequest(String bucketName, String objectKey,
                                                    InputStream inputStream, ObjectMetadata metadata) {
        return new PutObjectRequest(bucketName, objectKey, inputStream, metadata);
    }

    private Upload transferPutObjectRequest(PutObjectRequest request)
            throws InterruptedException {
        return transferManager.upload(request);
    }
}