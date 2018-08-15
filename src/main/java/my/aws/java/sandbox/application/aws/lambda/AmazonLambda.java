package my.aws.java.sandbox.application.aws.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class AmazonLambda implements RequestHandler<String, String> {

    @Override
    public String handleRequest(String input, Context context) {
        return "Test first lambda";
    }
}
