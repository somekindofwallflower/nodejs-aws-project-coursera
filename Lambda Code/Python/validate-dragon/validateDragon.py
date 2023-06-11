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

s3 = boto3.client('s3','us-east-1')
ssm = boto3.client('ssm', 'us-east-1')
bucket_name = ssm.get_parameter( Name='dragon_data_bucket_name',WithDecryption=False)['Parameter']['Value']
file_name = ssm.get_parameter( Name='dragon_data_file_name',WithDecryption=False)['Parameter']['Value']

def validate(event, context):
    
    result = s3.select_object_content(
        Bucket=bucket_name,
        Key=file_name,
        ExpressionType='SQL',
        Expression="select * from S3Object[*][*] s where s.dragon_name_str = '" + event['dragon_name_str'] + "'",
        InputSerialization={'JSON': {'Type': 'Document'}},
        OutputSerialization={'JSON': {}}
    )
    
    for records in result['Payload']:
        if 'Records' in records:
            raise DragonValidationException("Duplicate dragon reported")
        return 'Dragon Validated'

class DragonValidationException(Exception):
    def __init__(self, *args):
        if args:
            self.message = args[0]
        else:
            self.message = None

    def __str__(self):
        if self.message:
            return 'DragonValidationException, {0}'.format(self.message)
        else:
            return 'DragonValidationException has been raised'
