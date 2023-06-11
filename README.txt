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

Resources needed in your AWS account before beginning the demos:

1. An S3 bucket
    a. You can find an example of the bucket policy in the Permissions folder
2. IAM Roles for the lambda functions
    a. Policies to attach
        i. LambdaBasicExecutionRole
        ii. AWSXrayFullAccess
        iii. AmazonSSMFullAccess or more tightly scoped custom policy
        iiii. AmazonS3FullAccess or more tightly scoped custom policy
3. Parameters in AWS Systems Manager Parameter Store
    a. One parameter for the bucket name, unencrypted
        i. Use dragon_data_bucket_name for the name of the parameter
    b. One parameter for the key name, unencrypted
        i. Use dragon_data_file_name for the name of the parameter
