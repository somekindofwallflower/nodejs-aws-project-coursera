// # Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// #
// # Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except
// # in compliance with the License. A copy of the License is located at
// #
// # https://aws.amazon.com/apache-2-0/
// #
// # or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
// # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
// # specific language governing permissions and limitations under the License.

package com.mycompany.app;

import com.mycompany.app.model.Dragon;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import java.util.*; 
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class App implements RequestHandler<Dragon, String> {

    private static final AWSSimpleSystemsManagement ssm = AWSSimpleSystemsManagementClientBuilder.defaultClient();
    private static final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    public String handleRequest(Dragon event, Context context) {
        addDragon(event);
        return "Dragon added";
    }
     
    private static void addDragon(Dragon event) {
        // get object from S3
        S3Object object = s3Client.getObject(new GetObjectRequest(getBucketName(), getKey()));
        // get object contents as input stream
        InputStream objectInputStream = object.getObjectContent();
        // convert input stream to string
        String dragonDataString = convertTextInputStreamToString(objectInputStream);
        // convert string to List<Dragon> to work with more easily
        // This is because I am trying to avoid doing raw string manipulation
        List<Dragon> dragonDataList = convertStringtoList(dragonDataString);
        // add dragon to List
        addNewDragonToList(event, dragonDataList);
        uploadObjectToS3(getBucketName(), getKey(), dragonDataList);
    }

    private static String getBucketName() {
        GetParameterRequest bucketParameterRequest = new GetParameterRequest().withName("dragon_data_bucket_name").withWithDecryption(false);
        GetParameterResult bucketResult = ssm.getParameter(bucketParameterRequest);
        return bucketResult.getParameter().getValue();
    }
    
    private static String getKey() {
        GetParameterRequest keyParameterRequest = new GetParameterRequest().withName("dragon_data_file_name").withWithDecryption(false);
        GetParameterResult keyResult = ssm.getParameter(keyParameterRequest);
        return keyResult.getParameter().getValue();
    }
   
    private static String convertTextInputStreamToString(InputStream input) {
        // Read the text input stream one line at a time and display each line.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        String objectContent = "";
        try {
        while ((line = reader.readLine()) != null) {
            objectContent += line;
        }
        } catch (IOException e) {
            // in the real world please do something with errors
            // do not just log them
            // do as I say not as I do
            System.out.println(e.getMessage());
        }
        return objectContent;
    }
    
    private static List<Dragon> convertStringtoList(String dragonData) {
         return gson.fromJson(dragonData, new TypeToken<List<Dragon>>(){}.getType());
    }
    
    private static void addNewDragonToList(Dragon newDragon, List<Dragon> dragons) {
       dragons.add(newDragon);
    }

    private static void uploadObjectToS3(String bucketName, String key, List<Dragon> dragons) {
        // uploads the object to S3
        // converts List<Dragon> to a JSON String before writing
        s3Client.putObject(bucketName, key, gson.toJson(dragons));
    }

}