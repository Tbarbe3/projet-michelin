package fst_sw.michelin.repositories;

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

    private static final Duration URL_DURATION = Duration.ofMinutes(10);

    @Value("${s3.endpoint}")
    private String s3url;

    @Value("${s3.buckets.evaluation}")
    private String evaluationBucket;

    @Value("${s3.buckets.restaurant}")
    private String restaurantBucket;

    @Value("${s3.access-key}")
    private String s3AccessKey;

    @Value("${s3.secret-key}")
    private String s3SecretKey;

    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.builder()
                                .accessKeyId(s3AccessKey)
                                .secretAccessKey(s3SecretKey)
                                .build()))
                .endpointOverride(URI.create(s3url))
                .region(Region.US_EAST_1)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    public String getRestaurantImageUploadUrl(String key) {
        return this.getUploadFileUrl(restaurantBucket, key);
    }

    public String getRestaurantImageUrl(String key) {
        return this.getFileURL(restaurantBucket, key);
    }

    public String getDishPictureUploadUrl(String key) {
        return this.getUploadFileUrl(evaluationBucket, key);
    }

    public String getDishPictureUrl(String key) {
        return this.getFileURL(evaluationBucket, key);
    }

    public String getFileURL(String bucket, String fileId) {
        GetObjectRequest request = GetObjectRequest.builder().bucket(bucket)
                .key(fileId)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder().signatureDuration(URL_DURATION).getObjectRequest(request).build();

        try (S3Presigner presigner = this.s3Presigner()) {
            return presigner.presignGetObject(presignRequest).url().toString();
        }
    }

    public String getUploadFileUrl(String bucket, String key) {
        PutObjectRequest request = PutObjectRequest.builder().bucket(bucket)
                .key(key)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder().signatureDuration(URL_DURATION).putObjectRequest(request).build();

        try (S3Presigner presigner = this.s3Presigner()) {
            return presigner.presignPutObject(presignRequest).url().toString();
        }
    }
}
