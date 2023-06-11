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

var AWS = require("aws-sdk");

const s3 = new AWS.S3({
    region: 'us-east-1'
});

const ssm = new AWS.SSM({
    region: 'us-east-1'
});


exports.handler =  function(event, context, callback) {
    
  addDragon(event,callback);
  
}

async function addDragon(event, callback) {
  
   var fileName = await getFileName();
   var bucketName = await getBucketName();
   var dragonData = await addDragonToData(bucketName, fileName, event);
   writeToS3(dragonData, bucketName,fileName,callback);
   
}

async function getFileName() {
 
  var fileNameParams = {
        Name: 'dragon_data_file_name', 
        WithDecryption: false
  };
  var promise = await ssm.getParameter(fileNameParams).promise();
  return promise.Parameter.Value;
  
}

async function getBucketName() {
    var bucketNameParams = {
        Name: 'dragon_data_bucket_name', 
        WithDecryption: false
    };
  
  var promise = await ssm.getParameter(bucketNameParams).promise();
  return promise.Parameter.Value;

}

async function addDragonToData(bucketName, fileName, event) {
    var objectParams = {
        Bucket: bucketName,
        Key: fileName
    };
    
    var dragonData = {
         "description_str": event.description_str,
         "dragon_name_str": event.dragon_name_str,
         "family_str": event.family_str,
         "location_city_str": event.location_city_str,
         "location_country_str": event.location_country_str,
         "location_neighborhood_str": event.location_neighborhood_str,
         "location_state_str": event.location_state_str
    }
    
    var jsonData='';
    var promise = await s3.getObject(objectParams).promise();
    jsonData = JSON.parse(promise.Body.toString()); 
    jsonData.push(dragonData);
    return  jsonData;
    
}

function writeToS3(dragonData, bucketName, fileName, callback) {
    
    var uploadParams = {
        Bucket: bucketName, 
        Key: fileName, 
        Body: JSON.stringify(dragonData)
    }

    s3.upload(uploadParams, function (err, data) {
        if (data) {
           callback(null, {
               "statusCode" : 200,
           })
        }
        if(err) {
            callback("Error" + err.message)
        }
    })

}
   

