package my.aws.java.sandbox.application.aws.elb;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClientBuilder;

public class AmazonALBService {

    private final AmazonElasticLoadBalancing amazonElasticLoadBalancing;

    public AmazonALBService(AWSCredentials awsCredentials) {
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        amazonElasticLoadBalancing = AmazonElasticLoadBalancingClientBuilder
                .standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.EU_WEST_1)
                .build();
    }

    public void creareALB() {
        /*CreateLoadBalancerRequest createLoadBalancerRequest = new CreateLoadBalancerRequest()
                .withLoadBalancerName("")
                .withAvailabilityZones("")
                .withListeners("")
                .withSecurityGroups("")
                .withSubnets("")
                .withScheme("")
                .withTags("");
        amazonElasticLoadBalancing.createLoadBalancer(createLoadBalancerRequest);*/
    }

    // 1. Create ALB
    // 2. Create and launch EC2 instance
    // 3. Create target group
    // 4. Add instance to target group
    // 5. Create ALB listener rule and map to the target group

}
