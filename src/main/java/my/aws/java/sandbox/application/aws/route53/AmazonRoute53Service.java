package my.aws.java.sandbox.application.aws.route53;

import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53ClientBuilder;
import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeAction;
import com.amazonaws.services.route53.model.ChangeBatch;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResult;
import com.amazonaws.services.route53.model.HostedZone;
import com.amazonaws.services.route53.model.ListHostedZonesResult;
import com.amazonaws.services.route53.model.RRType;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;

public class AmazonRoute53Service {

    private final AmazonRoute53 amazonRoute53;

    public AmazonRoute53Service(AWSCredentials awsCredentials) {
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        amazonRoute53 = AmazonRoute53ClientBuilder
                .standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.EU_WEST_1)
                .build();
    }

    public void listHostedZones() {
        ListHostedZonesResult listHostedZonesResult = amazonRoute53.listHostedZones();
        List<HostedZone> hostedZones = listHostedZonesResult.getHostedZones();
        hostedZones.forEach(System.out::println);
    }

    public void createNewSubDomainAndAssignToEC2Instance(String hostedZoneId, ChangeAction action, String name, RRType type, Long ttl, String publicDns) {
        ResourceRecord resourceRecord = new ResourceRecord()
                .withValue(publicDns);

        ResourceRecordSet resourceRecordSet = new ResourceRecordSet()
                .withName(name)
                .withType(type)
                .withTTL(ttl)
                .withResourceRecords(resourceRecord);

        Change change = new Change()
                .withAction(action)
                .withResourceRecordSet(resourceRecordSet);
        ChangeBatch changeBatch = new ChangeBatch()
                .withChanges(change);

        ChangeResourceRecordSetsRequest changeResourceRecordSetsRequest = new ChangeResourceRecordSetsRequest()
                .withHostedZoneId(hostedZoneId)
                .withChangeBatch(changeBatch);

        ChangeResourceRecordSetsResult changeResourceRecordSetsResult = amazonRoute53.changeResourceRecordSets(changeResourceRecordSetsRequest);
        System.out.println(changeResourceRecordSetsResult.getChangeInfo());
    }

}
