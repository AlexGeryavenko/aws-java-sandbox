package my.aws.java.sandbox.config;

import com.amazonaws.auth.AWSCredentials;

public interface AmazonCredentials {

    AWSCredentials getCredentials();

}
