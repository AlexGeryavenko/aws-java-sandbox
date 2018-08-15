package my.aws.java.sandbox.application.aws.sqs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.ListQueueTagsRequest;
import com.amazonaws.services.sqs.model.ListQueueTagsResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.TagQueueRequest;
import com.amazonaws.services.sqs.model.UntagQueueRequest;


public class AmazonSQSService {

    private final AmazonSQS amazonSQS;

    public AmazonSQSService(AWSCredentials awsCredentials) {
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        amazonSQS = AmazonSQSClientBuilder
                .standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.EU_WEST_1)
                .build();
    }

    public void createQueue(String queueName) {
        System.out.println("Creating Queue: " + queueName);
        CreateQueueResult createQueueResult = amazonSQS.createQueue(queueName);

        System.out.printf(
                "Newly created queue \"%s\"\n" +
                        "queueUrl %s, \n" +
                        "sdkHttpMetadata %s, \n" +
                        "sdkResponseMetadata %s \n\n",
                queueName,
                createQueueResult.getQueueUrl(),
                createQueueResult.getSdkHttpMetadata(),
                createQueueResult.getSdkResponseMetadata());
    }

    public void deleteQueue(String queueName) {
        System.out.println("Deleting Queue: " + queueName);
        amazonSQS.deleteQueue(queueName);
    }

    public void tagQueue(String queueName, String tagKey, String tagValue) {
        System.out.println("Tagging Queue");
        String queueUrl = amazonSQS.getQueueUrl(queueName).getQueueUrl();

        TagQueueRequest tagQueueRequest = new TagQueueRequest()
                .withQueueUrl(queueUrl)
                .addTagsEntry(tagKey, tagValue);
        amazonSQS.tagQueue(tagQueueRequest);

        ListQueueTagsRequest listQueueTagsRequest = new ListQueueTagsRequest()
                .withQueueUrl(queueUrl);
        ListQueueTagsResult listQueueTagsResult = amazonSQS.listQueueTags(listQueueTagsRequest);
        listQueueTagsResult.getTags().entrySet().forEach(System.out::println);

        UntagQueueRequest untagQueueRequest = new UntagQueueRequest()
                .withQueueUrl(queueUrl)
                .withTagKeys(tagKey);
        amazonSQS.untagQueue(untagQueueRequest);
    }

    public void messageQueue(String queueName, String messageBody) {
        System.out.println("Messaging Queue");
        String queueUrl = amazonSQS.getQueueUrl(queueName).getQueueUrl();

        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(queueName)
                .withMessageBody(messageBody);
        amazonSQS.sendMessage(sendMessageRequest);

        ReceiveMessageResult receiveMessageResult = amazonSQS.receiveMessage(queueUrl);
        receiveMessageResult.getMessages().forEach(System.out::println);

        for (Message message : receiveMessageResult.getMessages()) {
            DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withReceiptHandle(message.getReceiptHandle());
            amazonSQS.deleteMessage(deleteMessageRequest);
        }
    }

}
