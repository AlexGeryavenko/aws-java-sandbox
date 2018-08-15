package my.aws.java.sandbox.application;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.route53.model.ChangeAction;
import com.amazonaws.services.route53.model.RRType;

import my.aws.java.sandbox.application.aws.ec2.AmazonEC2Service;
import my.aws.java.sandbox.application.aws.iam.AmazonIAMService;
import my.aws.java.sandbox.application.aws.route53.AmazonRoute53Service;
import my.aws.java.sandbox.application.aws.s3.AmazonS3Service;
import my.aws.java.sandbox.application.aws.sns.AmazonSNSService;
import my.aws.java.sandbox.application.aws.sqs.AmazonSQSService;
import my.aws.java.sandbox.config.AmazonCredentials;
import my.aws.java.sandbox.config.AmazonStaticCredentialsProvider;

public class SandboxMain {

    private static final String TEST_S3_BUCKET_NAME = "sandbox13";

    private static final String TEST_EC2_KEY_PAIR_NAME = "kpTest13";
    private static final String TEST_EC2_SECURITY_GROUP_NAME = "sgSandboxTest13";
    private static final String TEST_EC2_SECURITY_GROUP_DESCRIPTION = "Test creation security group";

    private static final String TEST_SQS_QUEUE_NAME = "testQueue13";
    private static final String TEST_SQS_TAG_KEY_NAME = "testTagKey13";
    private static final String TEST_SQS_TAG_VALUE = "testTagValue13";
    private static final String TEST_SQS_MESSAGE_BODY = "Message body 13";

    private static final String TEST_SNS_TOPIC_NAME = "testTopic13";
    private static final String TEST_SNS_SUBSCRIBER_PROTOCOL = "email";
    private static final String TEST_SNS_SUBSCRIBER_ENDPOINT = "aws.certf.purpose@gmail.com";
    private static final String TEST_SNS_MESSAGE = "Test message 13";

    private static final String TEST_IAM_GROUP_NAME = "testGroup13";
    private static final String TEST_IAM_USER_NAME = "testUser13";
    private static final String TEST_IAM_ROLE_NAME = "testRole13";
    private static final String TEST_IAM_S3_READ_ONLY_ACCESS_POLICY_ARN = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess";
    private static final String TEST_IAM_ALEXA_FOR_BUSINESS_DEVICE_SETUP_POLICY_ARN = "arn:aws:iam::aws:policy/AlexaForBusinessDeviceSetup";

    private static final String TEST_ROUTE53_HOSTED_ZONE_ID = "Z2EKF1W4O0NEMY";
    private static final String TEST_ROUTE53_SUB_DOMAIN_NAME = "ec2-createNewSubDomainAndAssignToEC2Instance-2.myaws.top";
    private static final Long TEST_ROUTE53_RESOURCE_RECORD_CACHE_TIME_TO_LIVE = 300L;
    private static final String TEST_ROUTE53_PUBLIC_DNS = "52.210.51.122";

    private final AWSCredentials awsCredentials;

    private SandboxMain(AmazonCredentials amazonCredentials) {
        this.awsCredentials = amazonCredentials.getCredentials();
    }

    public static void main(String[] args) {
        AmazonCredentials amazonCredentials = new AmazonStaticCredentialsProvider();
        SandboxMain sandboxMain = new SandboxMain(amazonCredentials);

        sandboxMain.testAmazonS3();
        sandboxMain.testAmazonEC2();
        sandboxMain.testAmazonSQS();
        sandboxMain.testAmazonSNS();
        sandboxMain.testAmazonIAM();
        sandboxMain.testRoute53();
    }

    private void testAmazonS3() {
        AmazonS3Service amazonS3Service = new AmazonS3Service(awsCredentials);
        amazonS3Service.createBucket(TEST_S3_BUCKET_NAME);
        amazonS3Service.deleteBucket(TEST_S3_BUCKET_NAME);
    }

    private void testAmazonEC2() {
        AmazonEC2Service amazonEC2Service = new AmazonEC2Service(awsCredentials);

        String securityGroupId = amazonEC2Service.createSecurityGroup(TEST_EC2_SECURITY_GROUP_NAME, TEST_EC2_SECURITY_GROUP_DESCRIPTION);
        amazonEC2Service.describeSecurityGroups(securityGroupId);
        amazonEC2Service.deleteSecurityGroup(securityGroupId);

        amazonEC2Service.createKeyPair(TEST_EC2_KEY_PAIR_NAME);
        amazonEC2Service.describeKeyPairs();
        amazonEC2Service.deleteKeyPair(TEST_EC2_KEY_PAIR_NAME);

        amazonEC2Service.describeRegionsAndZones();

        String instanceId = amazonEC2Service.createAndLaunchInstance();
        amazonEC2Service.describeInstances();
        amazonEC2Service.deleteInstance(instanceId);
    }

    private void testAmazonSQS() {
        AmazonSQSService amazonSQSService = new AmazonSQSService(awsCredentials);

        amazonSQSService.createQueue(TEST_SQS_QUEUE_NAME);
        amazonSQSService.tagQueue(TEST_SQS_QUEUE_NAME, TEST_SQS_TAG_KEY_NAME, TEST_SQS_TAG_VALUE);
        amazonSQSService.messageQueue(TEST_SQS_QUEUE_NAME, TEST_SQS_MESSAGE_BODY);
        amazonSQSService.deleteQueue(TEST_SQS_QUEUE_NAME);
    }

    private void testAmazonSNS() {
        AmazonSNSService amazonSNSService = new AmazonSNSService(awsCredentials);

        String topicARN = amazonSNSService.createTopic(TEST_SNS_TOPIC_NAME);
        String subscriptionARN = amazonSNSService.subscribe(topicARN, TEST_SNS_SUBSCRIBER_PROTOCOL, TEST_SNS_SUBSCRIBER_ENDPOINT);
        amazonSNSService.publish(topicARN, TEST_SNS_MESSAGE);
        amazonSNSService.unsubscribe(subscriptionARN);
        amazonSNSService.deleteTopic(topicARN);
    }

    private void testAmazonIAM() {
        AmazonIAMService amazonIAMService = new AmazonIAMService(awsCredentials);

        amazonIAMService.createGroup(TEST_IAM_GROUP_NAME);
        amazonIAMService.createUser(TEST_IAM_USER_NAME);
        amazonIAMService.createRole(TEST_IAM_ROLE_NAME);
        amazonIAMService.addUserToGroup(TEST_IAM_GROUP_NAME, TEST_IAM_USER_NAME);
        amazonIAMService.attachPolicyToGroup(TEST_IAM_GROUP_NAME, TEST_IAM_ALEXA_FOR_BUSINESS_DEVICE_SETUP_POLICY_ARN);
        amazonIAMService.attachPolicyToRole(TEST_IAM_ROLE_NAME, TEST_IAM_S3_READ_ONLY_ACCESS_POLICY_ARN);

        amazonIAMService.listGroups();
        amazonIAMService.listUsers();
        amazonIAMService.listRoles();

        amazonIAMService.detachPolicyFromRole(TEST_IAM_ROLE_NAME, TEST_IAM_S3_READ_ONLY_ACCESS_POLICY_ARN);
        amazonIAMService.detachPolicyFromGroup(TEST_IAM_GROUP_NAME, TEST_IAM_ALEXA_FOR_BUSINESS_DEVICE_SETUP_POLICY_ARN);
        amazonIAMService.removeUserFromGroup(TEST_IAM_GROUP_NAME, TEST_IAM_USER_NAME);
        amazonIAMService.deleteRole(TEST_IAM_ROLE_NAME);
        amazonIAMService.deleteUser(TEST_IAM_USER_NAME);
        amazonIAMService.deleteGroup(TEST_IAM_GROUP_NAME);
    }

    private void testRoute53() {
        AmazonRoute53Service amazonRoute53Service = new AmazonRoute53Service(awsCredentials);

        amazonRoute53Service.listHostedZones();
        amazonRoute53Service.createNewSubDomainAndAssignToEC2Instance(
                TEST_ROUTE53_HOSTED_ZONE_ID,
                ChangeAction.CREATE,
                TEST_ROUTE53_SUB_DOMAIN_NAME,
                RRType.A,
                TEST_ROUTE53_RESOURCE_RECORD_CACHE_TIME_TO_LIVE,
                TEST_ROUTE53_PUBLIC_DNS);
    }

}
