package my.aws.java.sandbox.application.aws.iam;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.AddUserToGroupRequest;
import com.amazonaws.services.identitymanagement.model.AttachGroupPolicyRequest;
import com.amazonaws.services.identitymanagement.model.AttachRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.CreateGroupRequest;
import com.amazonaws.services.identitymanagement.model.CreateGroupResult;
import com.amazonaws.services.identitymanagement.model.CreateRoleRequest;
import com.amazonaws.services.identitymanagement.model.CreateRoleResult;
import com.amazonaws.services.identitymanagement.model.CreateUserRequest;
import com.amazonaws.services.identitymanagement.model.CreateUserResult;
import com.amazonaws.services.identitymanagement.model.DeleteGroupRequest;
import com.amazonaws.services.identitymanagement.model.DeleteRoleRequest;
import com.amazonaws.services.identitymanagement.model.DeleteUserRequest;
import com.amazonaws.services.identitymanagement.model.DetachGroupPolicyRequest;
import com.amazonaws.services.identitymanagement.model.DetachRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.Group;
import com.amazonaws.services.identitymanagement.model.ListAttachedRolePoliciesRequest;
import com.amazonaws.services.identitymanagement.model.ListAttachedRolePoliciesResult;
import com.amazonaws.services.identitymanagement.model.ListGroupsRequest;
import com.amazonaws.services.identitymanagement.model.ListGroupsResult;
import com.amazonaws.services.identitymanagement.model.ListRolePoliciesRequest;
import com.amazonaws.services.identitymanagement.model.ListRolePoliciesResult;
import com.amazonaws.services.identitymanagement.model.ListRolesRequest;
import com.amazonaws.services.identitymanagement.model.ListRolesResult;
import com.amazonaws.services.identitymanagement.model.ListUsersRequest;
import com.amazonaws.services.identitymanagement.model.ListUsersResult;
import com.amazonaws.services.identitymanagement.model.RemoveUserFromGroupRequest;
import com.amazonaws.services.identitymanagement.model.Role;
import com.amazonaws.services.identitymanagement.model.User;

public class AmazonIAMService {

    private final AmazonIdentityManagement amazonIdentityManagement;

    public AmazonIAMService(AWSCredentials awsCredentials) {
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        amazonIdentityManagement = AmazonIdentityManagementClientBuilder
                .standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.EU_WEST_1)
                .build();
    }

    public void createGroup(String groupName) {
        System.out.println("Start create group with name: " + groupName);
        CreateGroupRequest createGroupRequest = new CreateGroupRequest()
                .withGroupName(groupName);
        CreateGroupResult createGroupResult = amazonIdentityManagement.createGroup(createGroupRequest);
        System.out.println("Created group: " + createGroupResult.getGroup());
    }

    public void deleteGroup(String groupName) {
        DeleteGroupRequest deleteGroupRequest = new DeleteGroupRequest()
                .withGroupName(groupName);
        amazonIdentityManagement.deleteGroup(deleteGroupRequest);
        System.out.println("Group deleted.");
    }

    public void createUser(String userName) {
        CreateUserRequest createUserRequest = new CreateUserRequest()
                .withUserName(userName);
        CreateUserResult createUserResult = amazonIdentityManagement.createUser(createUserRequest);
        System.out.println("Created user: " + createUserResult.getUser());
    }

    public void deleteUser(String userName) {
        DeleteUserRequest deleteUserRequest = new DeleteUserRequest()
                .withUserName(userName);
        amazonIdentityManagement.deleteUser(deleteUserRequest);
        System.out.println("User deleted.");
    }

    public void createRole(String roleName) {
        CreateRoleRequest createRoleRequest = new CreateRoleRequest()
                .withRoleName(roleName)
                .withDescription("From sandbox: Allows EC2 instances to call AWS services on your behalf.")
                .withAssumeRolePolicyDocument("{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"Service\":\"ec2.amazonaws.com\"},\"Action\":\"sts:AssumeRole\"}]}");
        CreateRoleResult createRoleResult = amazonIdentityManagement.createRole(createRoleRequest);
        System.out.println("Created role: " + createRoleResult.getRole());
    }

    public void deleteRole(String roleName) {
        DeleteRoleRequest deleteRoleRequest = new DeleteRoleRequest()
                .withRoleName(roleName);
        amazonIdentityManagement.deleteRole(deleteRoleRequest);
        System.out.println("Role deleted.");
    }

    public void listGroups() {
        System.out.println("List of groups: ");
        ListGroupsRequest listGroupsRequest = new ListGroupsRequest();
        while (true) {
            ListGroupsResult listGroupsResult = amazonIdentityManagement.listGroups(listGroupsRequest);
            for (Group group : listGroupsResult.getGroups()) {
                System.out.format("Retrieved group %s\n", group.getGroupName());
            }
            listGroupsRequest.setMarker(listGroupsResult.getMarker());
            if (!listGroupsResult.getIsTruncated()) {
                break;
            }
        }
    }

    public void listUsers() {
        System.out.println("List of users: ");
        ListUsersRequest listUsersRequest = new ListUsersRequest();
        while (true) {
            ListUsersResult listUsersResult = amazonIdentityManagement.listUsers(listUsersRequest);
            for (User user : listUsersResult.getUsers()) {
                System.out.format("Retrieved user %s\n", user.getUserName());
            }
            listUsersRequest.setMarker(listUsersResult.getMarker());
            if (!listUsersResult.getIsTruncated()) {
                break;
            }
        }
    }

    public void listRoles() {
        System.out.println("List of roles: ");
        ListRolesRequest listRolesRequest = new ListRolesRequest();
        while (true) {
            ListRolesResult listRolesResult = amazonIdentityManagement.listRoles(listRolesRequest);
            for (Role role : listRolesResult.getRoles()) {
                System.out.println("Retrieved role: " + role.getRoleName());
                System.out.println(" - assumeRolePolicyDocument: " + role.getAssumeRolePolicyDocument());
                System.out.println(" - description: " + role.getDescription());
                System.out.println(" - arn: " + role.getArn());

                listRolePolicies(role.getRoleName());

                System.out.println();
            }
            listRolesRequest.setMarker(listRolesResult.getMarker());
            if (!listRolesResult.getIsTruncated()) {
                break;
            }
        }
    }

    private void listRolePolicies(String roleName) {
        ListRolePoliciesRequest listRolePoliciesRequest = new ListRolePoliciesRequest()
                .withRoleName(roleName);
        ListRolePoliciesResult listRolePoliciesResult = amazonIdentityManagement.listRolePolicies(listRolePoliciesRequest);
        System.out.println(listRolePoliciesResult);

        ListAttachedRolePoliciesRequest listAttachedRolePoliciesRequest = new ListAttachedRolePoliciesRequest()
                .withRoleName(roleName);
        ListAttachedRolePoliciesResult listAttachedRolePoliciesResult = amazonIdentityManagement.listAttachedRolePolicies(listAttachedRolePoliciesRequest);
        System.out.println(listAttachedRolePoliciesResult);
    }

    public void addUserToGroup(String groupName, String userName) {
        AddUserToGroupRequest addUserToGroupRequest = new AddUserToGroupRequest()
                .withGroupName(groupName)
                .withUserName(userName);
        amazonIdentityManagement.addUserToGroup(addUserToGroupRequest);
    }

    public void removeUserFromGroup(String groupName, String userName) {
        RemoveUserFromGroupRequest removeUserFromGroupRequest = new RemoveUserFromGroupRequest()
                .withGroupName(groupName)
                .withUserName(userName);
        amazonIdentityManagement.removeUserFromGroup(removeUserFromGroupRequest);
        System.out.println("User removed from group.");
    }

    public void attachPolicyToGroup(String groupName, String policyArn) {
        AttachGroupPolicyRequest attachGroupPolicyRequest = new AttachGroupPolicyRequest()
                .withGroupName(groupName)
                .withPolicyArn(policyArn);
        amazonIdentityManagement.attachGroupPolicy(attachGroupPolicyRequest);
    }

    public void detachPolicyFromGroup(String groupName, String policyArn) {
        DetachGroupPolicyRequest detachGroupPolicyRequest = new DetachGroupPolicyRequest()
                .withGroupName(groupName)
                .withPolicyArn(policyArn);
        amazonIdentityManagement.detachGroupPolicy(detachGroupPolicyRequest);
        System.out.println("Policy detached from group.");
    }

    public void attachPolicyToRole(String roleName, String policyArn) {
        AttachRolePolicyRequest attachRolePolicyRequest = new AttachRolePolicyRequest()
                .withRoleName(roleName)
                .withPolicyArn(policyArn);
        amazonIdentityManagement.attachRolePolicy(attachRolePolicyRequest);
    }

    public void detachPolicyFromRole(String roleName, String policyArn) {
        DetachRolePolicyRequest detachRolePolicyRequest = new DetachRolePolicyRequest()
                .withRoleName(roleName)
                .withPolicyArn(policyArn);
        amazonIdentityManagement.detachRolePolicy(detachRolePolicyRequest);
        System.out.println("Policy detached from role.");
    }

}
