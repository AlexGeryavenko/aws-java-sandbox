package my.aws.java.sandbox.config;

import java.io.IOException;
import java.util.Properties;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

public class AmazonStaticCredentialsProvider implements AmazonCredentials {

    private final String accessKey;
    private final String secretKey;

    public AmazonStaticCredentialsProvider() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("aws.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        accessKey = properties.getProperty("aws.accessKey");
        secretKey = properties.getProperty("aws.secretKey");
    }

    public AWSCredentials getCredentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

}
