/*
 * Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.amazonaws.services.dynamodbv2.util.Tables;

import info.debatty.java.stringsimilarity.NGram;

/**
 * This sample demonstrates how to perform a few simple operations with the
 * Amazon DynamoDB service.
 */
public class AmazonDynamoDB {

    /*
     * Before running the code:
     *      Fill in your AWS access credentials in the provided credentials
     *      file template, and be sure to move the file to the default location
     *      (C:\\Users\\Vijay\\.aws\\credentials) where the sample code will load the
     *      credentials from.
     *      https://console.aws.amazon.com/iam/home?#security_credential
     *
     * WARNING:
     *      To avoid accidental leakage of your credentials, DO NOT keep
     *      the credentials file in your source directory.
     */

    static AmazonDynamoDBClient dynamoDB;
    static String tableName;
    static String tableName2;
    static String linkertable;
    
    /**
     * The only information needed to create a client are security credentials
     * consisting of the AWS Access Key ID and Secret Access Key. All other
     * configuration, such as the service endpoints, are performed
     * automatically. Client parameters, such as proxies, can be specified in an
     * optional ClientConfiguration object when constructing a client.
     *
     * @see com.amazonaws.auth.BasicAWSCredentials
     * @see com.amazonaws.auth.ProfilesConfigFile
     * @see com.amazonaws.ClientConfiguration
     */
    private static void init() throws Exception {
        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (C:\\Users\\Vijay\\.aws\\credentials).
         */
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\Vijay\\.aws\\credentials), and is in valid format.",
                    e);
        }
        dynamoDB = new AmazonDynamoDBClient(credentials);
        Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        dynamoDB.setRegion(usWest2);
        
    }

    public static void createTable() throws Exception {
        init();

        try {
            tableName = "Index";

            // Create table if it does not exist yet
            if (Tables.doesTableExist(dynamoDB, tableName)) {
                System.out.println("Table " + tableName + " is already ACTIVE");
            } else {
                // Create a table with a primary hash key named 'name', which holds a string
                CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
                    .withKeySchema(new KeySchemaElement().withAttributeName("keyname").withKeyType(KeyType.HASH))
                    .withAttributeDefinitions(new AttributeDefinition().withAttributeName("keyname").withAttributeType(ScalarAttributeType.S))
                    .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));
                    TableDescription createdTableDescription = dynamoDB.createTable(createTableRequest).getTableDescription();
                System.out.println("Created Table: " + createdTableDescription);

                // Wait for it to become active
                System.out.println("Waiting for " + tableName + " to become ACTIVE...");
                Tables.awaitTableToBecomeActive(dynamoDB, tableName);
            }
            
            // Describe our new table
            DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(linkertable);
            TableDescription tableDescription = dynamoDB.describeTable(describeTableRequest).getTable();
            System.out.println("Table Description: " + tableDescription);
            
            linkertable = "Links";

            // Create table if it does not exist yet
            if (Tables.doesTableExist(dynamoDB, linkertable)) {
                System.out.println("Table " + linkertable + " is already ACTIVE");
            } else {
                // Create a table with a primary hash key named 'name', which holds a string
                CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(linkertable)
                    .withKeySchema(new KeySchemaElement().withAttributeName("keyname").withKeyType(KeyType.HASH))
                    .withAttributeDefinitions(new AttributeDefinition().withAttributeName("keyname").withAttributeType(ScalarAttributeType.S))
                    .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));
                    TableDescription createdTableDescription = dynamoDB.createTable(createTableRequest).getTableDescription();
                System.out.println("Created Table: " + createdTableDescription);

                // Wait for it to become active
                System.out.println("Waiting for " + linkertable + " to become ACTIVE...");
                Tables.awaitTableToBecomeActive(dynamoDB, linkertable);
            }

            // Describe our new table
            describeTableRequest = new DescribeTableRequest().withTableName(linkertable);
            tableDescription = dynamoDB.describeTable(describeTableRequest).getTable();
            System.out.println("Table Description: " + tableDescription);
            
            tableName2 = "Reverse-Index";

            // Create table if it does not exist yet
            if (Tables.doesTableExist(dynamoDB, tableName2)) {
                System.out.println("Table " + tableName2 + " is already ACTIVE");
            } else {
                // Create a table with a primary hash key named 'name', which holds a string
                CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName2)
                    .withKeySchema(new KeySchemaElement().withAttributeName("valuename").withKeyType(KeyType.HASH))
                    .withAttributeDefinitions(new AttributeDefinition().withAttributeName("valuename").withAttributeType(ScalarAttributeType.S))
                    .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));
                    TableDescription createdTableDescription = dynamoDB.createTable(createTableRequest).getTableDescription();
                System.out.println("Created Table: " + createdTableDescription);

                // Wait for it to become active
                System.out.println("Waiting for " + tableName2 + " to become ACTIVE...");
                Tables.awaitTableToBecomeActive(dynamoDB, tableName2);
            }

            // Describe our new table
            describeTableRequest = new DescribeTableRequest().withTableName(tableName);
            tableDescription = dynamoDB.describeTable(describeTableRequest).getTable();
            System.out.println("Table Description: " + tableDescription);

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to AWS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with AWS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    static void newPair(String key, String value) {
        List<String> values = values(key);
        if(values == null){
            values = new ArrayList<String>();
        }
        values.add(value);
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("keyname", new AttributeValue(key));
        item.put("valuenames", new AttributeValue(values));
        PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
        PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
    }
    
    static void newReversePair(String key, String value) {
        List<String> keys = reverseValues(value);
        if(keys == null){
            keys = new ArrayList<String>();
        }
        keys.add(key);
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("valuename", new AttributeValue(value));
        item.put("keynames", new AttributeValue(keys));
        PutItemRequest putItemRequest = new PutItemRequest(tableName2, item);
        PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
    }
    
    static void newLink(String key, String value) {
                
        Map<String, ArrayList<String>> list = new HashMap<String, ArrayList<String>>();
        List<String> docs = new ArrayList<String>();
        String keyword;
        
        ScanRequest scanRequest = new ScanRequest().withTableName(linkertable);
        
        ScanResult result = dynamoDB.scan(scanRequest);
        
        if(result.getItems().isEmpty()) {
            Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put("key", new AttributeValue(key.toLowerCase()));
            docs.add(value);
            item.put("docs", new AttributeValue(docs));
            PutItemRequest putItemRequest = new PutItemRequest(linkertable, item);
            PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
            
        }
        
        else {
            boolean found = false;
            for (Map<String, AttributeValue> item : result.getItems()) {
                keyword = item.get("key").getS();
                
                if(keyword.equalsIgnoreCase(key)) {
                    docs = item.get("docs").getSS();
                    docs.add(value);
                    
                    Map<String, AttributeValue> same_key = new HashMap<String, AttributeValue>();
                    same_key.put("key", new AttributeValue(keyword));
                    Map<String, AttributeValueUpdate> new_value = new HashMap<String, AttributeValueUpdate>();
                    new_value.put("docs", new AttributeValueUpdate(new AttributeValue(docs), "put"));
                    UpdateItemRequest update = new UpdateItemRequest(linkertable, same_key, new_value);
                    
                    found = true;
           
                }
            }
            if(found = false) {
                docs.add(value);
                Map<String, AttributeValue> new_item = new HashMap<String, AttributeValue>();
                new_item.put("key", new AttributeValue(key.toLowerCase()));
                new_item.put("docs", new AttributeValue(docs));
                PutItemRequest putItemRequest = new PutItemRequest(linkertable, new_item);
                PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
            }
        }
    }
    
    static Map<String, Double> linkQuery(String key, String doc) {
        Map<String, Double> results = new HashMap<String, Double>();
        List<String> docs = new ArrayList<String>();
        String keyword;
                
        ScanRequest scanRequest = new ScanRequest().withTableName(linkertable);
                
        ScanResult result = dynamoDB.scan(scanRequest);
        
        for (Map<String, AttributeValue> item : result.getItems()){
            keyword = item.get("key").getS();
            docs = item.get("docs").getSS();
            
            NGram ngram = new NGram();
            
            double n = ngram.distance(key, keyword);
            
            if (docs.contains(doc)) {
                for(String d : docs) {
                    if(!(d.equalsIgnoreCase(doc))) {
                        if(results.containsKey(d)) {
                            double temp = results.get(d);
                            temp += n;
                            results.put(d, temp);
                        }
                        else {
                            results.put(d, n);
                        }
                    }
                    else continue;
                }
            }
            
            
        }
        
        return results;
    }
        
    
    static List<String> values(String key){
        List<String> list = new ArrayList<String>();
        
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":key", new AttributeValue().withS(key));
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(tableName)
                .withFilterExpression("keyname = :key")
                .withProjectionExpression("valuenames")
                .withExpressionAttributeValues(expressionAttributeValues);
        ScanResult result = dynamoDB.scan(scanRequest);
        for (Map<String, AttributeValue> item : result.getItems()) {
            list = item.get("valuenames").getSS();
        }
        return list;
    }

    static List<String> reverseValues(String key){
        List<String> list = new ArrayList<String>();
        
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":value", new AttributeValue().withS(key));
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(tableName2)
                .withFilterExpression("valuename = :value")
                .withProjectionExpression("keynames")
                .withExpressionAttributeValues(expressionAttributeValues);
        ScanResult result = dynamoDB.scan(scanRequest);
        for (Map<String, AttributeValue> item : result.getItems()) {
            list = item.get("keynames").getSS();
        }
        return list;
    }
    
    static boolean returnPermission(String user, String id){
	 	List<String> list = new ArrayList<String>();
	 	String access = null;
	 	
	 	Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
	 	expressionAttributeValues.put(":id", new AttributeValue().withS(id));
	 	ScanRequest scanRequest = new ScanRequest()
	 		    .withTableName("dataitems")
	 		    .withFilterExpression("id = :id")
	 		    .withProjectionExpression("isPub, viewers")
	 		    .withExpressionAttributeValues(expressionAttributeValues);
	 	ScanResult result = dynamoDB.scan(scanRequest);
	 	List<Map<String, AttributeValue>> results = result.getItems();
	 	if(results.isEmpty()){
	 		return false;
	 	} else {
	    	for (Map<String, AttributeValue> item : result.getItems()) {
	    	    list = item.get("viewers").getSS();
	    	    access = item.get("isPub").getN();
	    	}
	    	if(access.equals("1") || list.contains(user)){
	    		return true;
	    	} else {
	    		return false;
	    	}
	 	}
    }
}