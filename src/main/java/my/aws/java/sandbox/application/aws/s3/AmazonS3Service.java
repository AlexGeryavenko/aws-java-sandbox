package my.aws.java.sandbox.application.aws.s3;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;

public class AmazonS3Service {

    private final AmazonS3 amazonS3;

    public AmazonS3Service(AWSCredentials awsCredentials) {
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.EU_WEST_1)
                .build();
    }

    public void createBucket(String bucketName) {
        System.out.println("Creating S3 bucket: " + bucketName);
        if (amazonS3.doesBucketExistV2(bucketName)) {
            System.out.format("Bucket %s already exists.\n", bucketName);
        } else {
            try {
                amazonS3.createBucket(bucketName);
            } catch (AmazonS3Exception e) {
                System.err.println(e.getErrorMessage());
            }
        }
        System.out.println("Done! Bucket created.");
    }

    public void deleteBucket(String bucketName) {
        System.out.println("Deleting S3 bucket: " + bucketName);
        try {
            System.out.println(" - removing objects from bucket");
            ObjectListing object_listing = amazonS3.listObjects(bucketName);
            while (true) {
                for (S3ObjectSummary summary : object_listing.getObjectSummaries()) {
                    amazonS3.deleteObject(bucketName, summary.getKey());
                }

                // more object_listing to retrieve?
                if (object_listing.isTruncated()) {
                    object_listing = amazonS3.listNextBatchOfObjects(object_listing);
                } else {
                    break;
                }
            }

            System.out.println(" - removing versions from bucket");
            VersionListing version_listing = amazonS3.listVersions(new ListVersionsRequest().withBucketName(bucketName));
            while (true) {
                for (S3VersionSummary vs : version_listing.getVersionSummaries()) {
                    amazonS3.deleteVersion(bucketName, vs.getKey(), vs.getVersionId());
                }

                if (version_listing.isTruncated()) {
                    version_listing = amazonS3.listNextBatchOfVersions(
                            version_listing);
                } else {
                    break;
                }
            }

            System.out.println(" OK, bucket ready to delete!");
            amazonS3.deleteBucket(bucketName);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done! Bucket deleted.");
    }

    public void putObject(String bucketName, String filePath) {
        System.out.format("Uploading \"%s\" to S3 bucket %s...\n", filePath, bucketName);
        String fileName = Paths.get(filePath).getFileName().toString();
        try {
            amazonS3.putObject(bucketName, fileName, new File(filePath));
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done! Object upload.");
    }

    public void listObjects(String bucketName) {
        ListObjectsV2Result result = amazonS3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
            System.out.println("* " + os.getKey());
        }
    }

}
