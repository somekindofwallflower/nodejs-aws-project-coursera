# Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except
# in compliance with the License. A copy of the License is located at
#
# https://aws.amazon.com/apache-2-0/
#
# or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

import boto3
import json

s3 = boto3.client('s3')
ssm = boto3.client('ssm', 'us-east-1')
bucket_name = ssm.get_parameter( Name='dragon_data_bucket_name',WithDecryption=False)['Parameter']['Value']
file_name = ssm.get_parameter( Name='dragon_data_file_name',WithDecryption=False)['Parameter']['Value']

def addDragonToFile(event, context):

    dragon_data = {
     "description_str":event['description_str'],
     "dragon_name_str":event['dragon_name_str'],
     "family_str":event['family_str'],
     "location_city_str":event['location_city_str'],
     "location_country_str":event['location_country_str'],
     "location_neighborhood_str":event['location_neighborhood_str'],
     "location_state_str":event['location_state_str']
    }
    
    resp=s3.get_object(Bucket=bucket_name, Key=file_name)
    data=resp.get('Body').read()
    
    json_data = json.loads(data)
    json_data.append(dragon_data)
    s3.put_object(Bucket=bucket_name, Key=file_name, Body=json.dumps(json_data).encode())
