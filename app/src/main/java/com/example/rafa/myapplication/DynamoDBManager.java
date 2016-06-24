/*
 * Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package com.example.rafa.myapplication;

import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class DynamoDBManager {

    private static final String TAG = "DynamoDBManager";

    /*
     * Scans the table and returns the list of users.
     */
    public static List<String> getTablesList() {

        AmazonDynamoDBClient ddb = MainActivity.clientManager
                .ddb();
        try {
            return ddb.listTables().getTableNames();

        } catch (AmazonServiceException ex) {
            MainActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }

        return null;
    }

    /*
     * Scans the table and returns the list of users.
     */
    public static ArrayList<UserPreference> getItemsList() {
        ArrayList<UserPreference> resultList = new ArrayList<UserPreference>();
        AmazonDynamoDBClient ddb = MainActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        try {
            PaginatedScanList<UserPreference> result = mapper.scan(
                    UserPreference.class, scanExpression);


            for (UserPreference up : result) {
                resultList.add(up);

            }
            return resultList;

        } catch (AmazonServiceException ex) {
            MainActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }

        return resultList;
    }

    /*
    * Inserts ten users with userNo from 1 to 10 and random names.
    */
   public static void insertUsers(UserPreference userPreference) {
       AmazonDynamoDBClient ddb = MainActivity.clientManager
               .ddb();
       DynamoDBMapper mapper = new DynamoDBMapper(ddb);

       try {
           Log.d(TAG, "Inserting user");
           mapper.save(userPreference);
           Log.d(TAG, "User inserted");
       } catch (AmazonServiceException ex) {
           Log.e(TAG, "Error inserting users");
           MainActivity.clientManager
                   .wipeCredentialsOnAuthError(ex);
       }
   }

   /*
    * Deletes the specified item and all of its attribute/value pairs.
    */
    public static void deleteUser(UserPreference deleteUserPreference) {

        AmazonDynamoDBClient ddb = MainActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
            mapper.delete(deleteUserPreference);

        } catch (AmazonServiceException ex) {
            MainActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }
    }

    @DynamoDBTable(tableName = "Devices")
    public static class UserPreference {
        private long Id;
        private String Name;
        private String User;
        private String Situation;
        private String Version;

        @DynamoDBHashKey(attributeName = "Id")
        public long getIdDevice() {
            return Id;
        }

        public void setIdDevice(long Id) {
            this.Id = Id;
        }

        @DynamoDBRangeKey(attributeName = "NameDevice")
        public String getNameDevice() {
            return Name;
        }

        public void setNameDevice(String Name) {
            this.Name = Name;
        }

        @DynamoDBAttribute(attributeName = "UserName")
        public String getUserName() {
            return User;
        }

        public void setUserName(String User) {
            this.User = User;
        }

        @DynamoDBAttribute(attributeName = "Situation")
        public String getSituation() {
            return Situation;
        }

        public void setSituation(String Situation) {
            this.Situation = Situation;
        }

        @DynamoDBAttribute(attributeName = "VersionHardware")
        public String getVersionHardware() {
            return Version;
        }

        public void setVersionHardware(String Version) {
            this.Version = Version;
        }

    }


    /*
    * Retrieves the table description and returns the table status as a string.
    *
    public static String getTestTableStatus() {

        try {
            AmazonDynamoDBClient ddb = MainActivity.clientManager
                    .ddb();

            DescribeTableRequest request = new DescribeTableRequest()
                    .withTableName("Devices");
            DescribeTableResult result = ddb.describeTable(request);

            String status = result.getTable().getTableStatus();
            return status == null ? "" : status;

        } catch (ResourceNotFoundException e) {
        } catch (AmazonServiceException ex) {
            MainActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }

        return "";
    }

    /*
    * Creates a table with the following attributes: Table name: testTableName
    * Hash key: userNo type N Read Capacity Units: 10 Write Capacity Units: 5
    *
    public static void listTables() {

        Log.d(TAG, "List tables called");

        AmazonDynamoDBClient ddb = MainActivity.clientManager
                .ddb();
        try {
            List<String> tables = ddb.listTables().getTableNames();

            System.out.println("Listing table names");
            for(String elem : tables){
                System.out.println(elem);
            }

        } catch (AmazonServiceException ex) {
            Log.e(TAG, "Error sending create table request", ex);
            MainActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }

    }

    /*
     * Creates a table with the following attributes: Table name: testTableName
     * Hash key: userNo type N Read Capacity Units: 10 Write Capacity Units: 5
     *
    public static void createTable() {

        Log.d(TAG, "Create table called");

        AmazonDynamoDBClient ddb = MainActivity.clientManager
                .ddb();

        KeySchemaElement kse = new KeySchemaElement().withAttributeName(
                "userNo").withKeyType(KeyType.HASH);
        AttributeDefinition ad = new AttributeDefinition().withAttributeName(
                "userNo").withAttributeType(ScalarAttributeType.N);
        ProvisionedThroughput pt = new ProvisionedThroughput()
                .withReadCapacityUnits(10l).withWriteCapacityUnits(5l);

        CreateTableRequest request = new CreateTableRequest()
                .withTableName(Constants.TEST_TABLE_NAME)
                .withKeySchema(kse).withAttributeDefinitions(ad)
                .withProvisionedThroughput(pt);

        try {
            Log.d(TAG, "Sending Create table request");
            ddb.createTable(request);
            Log.d(TAG, "Create request response successfully recieved");
        } catch (AmazonServiceException ex) {
            Log.e(TAG, "Error sending create table request", ex);
            MainActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }
    }



    /*
     * Retrieves all of the attribute/value pairs for the specified user.
     *
    public static UserPreference getUserPreference(int userNo) {

        AmazonDynamoDBClient ddb = MainActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
            UserPreference userPreference = mapper.load(UserPreference.class,
                    userNo);

            return userPreference;

        } catch (AmazonServiceException ex) {
            MainActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }

        return null;
    }

    /*
     * Updates one attribute/value pair for the specified user.
     *
    public static void updateUserPreference(UserPreference updateUserPreference) {

        AmazonDynamoDBClient ddb = MainActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
            mapper.save(updateUserPreference);

        } catch (AmazonServiceException ex) {
            MainActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }
    }



    /*
     * Deletes the test table and all of its users and their attribute/value
     * pairs.
     *
    public static void cleanUp() {

        AmazonDynamoDBClient ddb = MainActivity.clientManager
                .ddb();

        DeleteTableRequest request = new DeleteTableRequest()
                .withTableName(Constants.TEST_TABLE_NAME);
        try {
            ddb.deleteTable(request);

        } catch (AmazonServiceException ex) {
            MainActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }
    }
    */


}
