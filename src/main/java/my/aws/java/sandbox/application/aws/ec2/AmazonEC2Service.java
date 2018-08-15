package my.aws.java.sandbox.application.aws.ec2;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

public class AmazonEC2Service {

    private final AmazonEC2 amazonEC2;

    public AmazonEC2Service(AWSCredentials awsCredentials) {
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        amazonEC2 = AmazonEC2ClientBuilder
                .standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.EU_WEST_1)
                .build();
    }

    public String createSecurityGroup(String groupName, String groupDescription) {
        CreateSecurityGroupRequest createSecurityGroupRequest = new CreateSecurityGroupRequest();
        createSecurityGroupRequest
                .withGroupName(groupName)
                .withDescription(groupDescription);
        CreateSecurityGroupResult createSecurityGroupResult = amazonEC2.createSecurityGroup(createSecurityGroupRequest);
        System.out.printf("Successfully created security group with id %s\n", createSecurityGroupResult.getGroupId());

        return createSecurityGroupResult.getGroupId();
    }

    public void describeSecurityGroups(String groupId) {
        DescribeSecurityGroupsRequest request = new DescribeSecurityGroupsRequest().withGroupIds(groupId);
        DescribeSecurityGroupsResult response = amazonEC2.describeSecurityGroups(request);

        for (SecurityGroup group : response.getSecurityGroups()) {
            System.out.printf(
                    "Found security group with id %s,\n" +
                            "vpc id %s\n" +
                            "and description \"%s\"\n\n",
                    group.getGroupId(),
                    group.getVpcId(),
                    group.getDescription());
        }
    }

    public void deleteSecurityGroup(String groupId) {
        DeleteSecurityGroupRequest request = new DeleteSecurityGroupRequest().withGroupId(groupId);
        amazonEC2.deleteSecurityGroup(request);
        System.out.printf("Successfully deleted security group with id %s\n\n", groupId);
    }

    public void createKeyPair(String keyName) {
        CreateKeyPairRequest request = new CreateKeyPairRequest().withKeyName(keyName);
        amazonEC2.createKeyPair(request);
        System.out.printf("Successfully created key pair named %s\n", keyName);
    }

    public void describeKeyPairs() {
        DescribeKeyPairsResult response = amazonEC2.describeKeyPairs();
        for (KeyPairInfo key_pair : response.getKeyPairs()) {
            System.out.printf(
                    "Found key pair with name %s \n" +
                            "and fingerprint %s\n\n",
                    key_pair.getKeyName(),
                    key_pair.getKeyFingerprint());
        }
    }

    public void deleteKeyPair(String keyName) {
        DeleteKeyPairRequest request = new DeleteKeyPairRequest().withKeyName(keyName);
        amazonEC2.deleteKeyPair(request);
        System.out.printf("Successfully deleted key pair named %s\n\n", keyName);
    }

    public void describeRegionsAndZones() {
        DescribeRegionsResult regions_response = amazonEC2.describeRegions();
        for (Region region : regions_response.getRegions()) {
            System.out.printf(
                    "Found region %s \n" +
                            "with endpoint %s\n\n",
                    region.getRegionName(),
                    region.getEndpoint());
        }
        DescribeAvailabilityZonesResult zones_response = amazonEC2.describeAvailabilityZones();
        for (AvailabilityZone zone : zones_response.getAvailabilityZones()) {
            System.out.printf(
                    "Found availability zone %s \n" +
                            "with status %s \n" +
                            "in region %s\n\n",
                    zone.getZoneName(),
                    zone.getState(),
                    zone.getRegionName());
        }
    }

    public String createAndLaunchInstance() {
        String amiId = "ami-9a91b371";
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
                .withImageId(amiId)
                .withInstanceType(InstanceType.T2Micro)
                .withMaxCount(1)
                .withMinCount(1);
        RunInstancesResult runInstancesResult = amazonEC2.runInstances(runInstancesRequest);
        String reservationId = runInstancesResult.getReservation().getReservationId();
        String instanceId = runInstancesResult.getReservation().getInstances().get(0).getInstanceId();
        System.out.printf("Successfully started EC2 instance %s (id=\"%s\") based on AMI %s\n\n", reservationId, instanceId, amiId);
        Tag tag = new Tag()
                .withKey("Name")
                .withValue("testTag");
        CreateTagsRequest tag_request = new CreateTagsRequest()
                .withResources(instanceId)
                .withTags(tag);
        amazonEC2.createTags(tag_request);

        return instanceId;
    }

    public void describeInstances() {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        while (true) {
            DescribeInstancesResult response = amazonEC2.describeInstances(request);
            for (Reservation reservation : response.getReservations()) {
                for (Instance instance : reservation.getInstances()) {
                    System.out.printf(
                            "Found instance with id %s, \n" +
                                    "AMI %s, \n" +
                                    "type %s, \n" +
                                    "state %s \n" +
                                    "and monitoring state %s\n\n",
                            instance.getInstanceId(),
                            instance.getImageId(),
                            instance.getInstanceType(),
                            instance.getState().getName(),
                            instance.getMonitoring().getState());
                }
            }
            request.setNextToken(response.getNextToken());
            if (response.getNextToken() == null) {
                break;
            }
        }
    }

    public void deleteInstance(String instanceId) {
        TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest().withInstanceIds(instanceId);
        amazonEC2.terminateInstances(terminateInstancesRequest);
    }

}
