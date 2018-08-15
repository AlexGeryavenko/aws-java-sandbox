package my.aws.java.sandbox.application.aws.sns;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.amazonaws.services.sns.model.UnsubscribeRequest;

public class AmazonSNSService {

    private final AmazonSNS amazonSNS;

    public AmazonSNSService(AWSCredentials awsCredentials) {
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        amazonSNS = AmazonSNSClientBuilder
                .standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.EU_WEST_1)
                .build();
    }

    public String createTopic(String topicName) {
        System.out.println("Creating Topic: " + topicName);
        CreateTopicResult createTopicResult = amazonSNS.createTopic(topicName);
        String topicArn = createTopicResult.getTopicArn();
        System.out.println("Newly created topic ARN: " + topicArn);

        return topicArn;
    }

    public void deleteTopic(String topicARN) {
        System.out.println("Deleting topic");
        amazonSNS.deleteTopic(topicARN);
    }

    public String subscribe(String topicARN, String protocol, String endpoint) {
        System.out.println("Creating subscription to a topic: " + topicARN);
        SubscribeRequest subscribeRequest = new SubscribeRequest()
                .withTopicArn(topicARN)
                .withProtocol(protocol)
                .withEndpoint(endpoint);
        SubscribeResult subscribeResult = amazonSNS.subscribe(subscribeRequest);
        String subscriptionArn = subscribeResult.getSubscriptionArn();
        System.out.println("Subscribed done, : subscription ARN" + subscriptionArn);

        return subscriptionArn;
    }

    public void unsubscribe(String subscriptionARN) {
        UnsubscribeRequest unsubscribeRequest = new UnsubscribeRequest()
                .withSubscriptionArn(subscriptionARN);
        amazonSNS.unsubscribe(unsubscribeRequest);
        System.out.println("Unsubscribed.");
    }

    public void publish(String topicARN, String message) {
        System.out.println("Publishing to a topic: " + topicARN);
        PublishRequest publishRequest = new PublishRequest()
                .withTopicArn(topicARN)
                .withMessage(message);
        PublishResult publishResult = amazonSNS.publish(publishRequest);
        System.out.println("Published message: \"" + message + "\", id = " + publishResult.getMessageId());
    }

    // 1. pushNotificationMessages
    // 2. mobileSMS
    // 3. SQS
    // 4. lambda


}
