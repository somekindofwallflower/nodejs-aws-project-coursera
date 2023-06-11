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

s3 = boto3.resource('s3')
bucket = s3.Bucket('<BUCKET_NAME>')
# the objects are available as a collection on the bucket object
for obj in bucket.objects.all():
    print(obj.key, obj.last_modified)
    
# access the client from the resource 
s3_client = boto3.resource('s3').meta.client