package fst_sw.demo.repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URI;
import java.time.Duration;

@Repository
public class S3Repository {

    @Value("${s3.endpoint}")
    private String s3url;

    @Value("${s3.buckets.cover}")
    private String coverBucket;

    @Value("${s3.buckets.screenshot}")
    private String screenshotBucket;

    @Value("${s3.access-key}")
    private String s3AccessKeyId;

    @Value("${s3.secret-key}")
    private String s3SecretKey;

    private S3Presigner s3Presigner() {
        return S3Presigner.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.builder()
                .accessKeyId(s3AccessKeyId)
                .secretAccessKey(s3SecretKey)
                .build()))
            .endpointOverride(URI.create(s3url))
            .serviceConfiguration(S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build())
            .build();
    }

    public String getCoverUploadUrl(String key) {
        return this.getUploadFileUrl(coverBucket, key);
    }

    public String getCoverUrl(String key) {
        return this.getFileUrl(coverBucket, key);
    }

    public String getScreenshotUploadUrl(String key) {
        return this.getUploadFileUrl(screenshotBucket, key);
    }

    public String getScreenshotUrl(String key) {
        return this.getFileUrl(screenshotBucket, key);
    }

    // Lien pour envoyer un fichier sur le S3
    private String getUploadFileUrl(String bucket, String key) {
        try (var s3Presigner = s3Presigner()) {

            PutObjectPresignRequest request = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build())
                .build();

            return s3Presigner.presignPutObject(request)
                .url()
                .toExternalForm();
        }
    }

    // Lien pour récupérer un fichier sur le S3
    private String getFileUrl(String bucket, String filePath) {
        try (var s3Presigner = s3Presigner()) {

            GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(filePath)
                    .build())
                .build();

            return s3Presigner.presignGetObject(request)
                .url()
                .toExternalForm();
        }
    }
}
