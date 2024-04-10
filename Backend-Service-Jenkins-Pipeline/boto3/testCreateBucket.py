#################################################################################
# MIT License                                                                   #
#                                                                               #
# Copyright (c) 2024 Chris Yang                                                 #
#                                                                               #
# Permission is hereby granted, free of charge, to any person obtaining a copy  #
# of this software and associated documentation files (the "Software"), to deal #
# in the Software without restriction, including without limitation the rights  #
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     #
# copies of the Software, and to permit persons to whom the Software is         #
# furnished to do so, subject to the following conditions:                      #
#                                                                               #
# The above copyright notice and this permission notice shall be included in all#
# copies or substantial portions of the Software.                               #
#                                                                               #
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    #
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      #
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   #
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        #
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, #
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE #
# SOFTWARE.                                                                     #
#################################################################################
"""
use boto3 module a resource directly 
"""
import boto3
import logging

## create bucket
bucket_name = 'chris-boto3-test'
try:
    s3 = boto3.resource('s3')

    all_bucket = [bucket.name for bucket in s3.buckets.all()]

    if(bucket_name not in all_bucket):
        print(f"'{bucket_name}' bucket not exists, and create it now...")
        s3.create_bucket(Bucket=bucket_name)
        print(f"'{bucket_name}' bucket has been created")
    else:
        print(f"'{bucket_name}' bucket has been created ALREADY... ")
except Exception as exp:
    logging.error(exp)

## upload file
file1='s3-f1.txt'
s3.Bucket(bucket_name).upload_file('./s3-f1.txt', 's3-f1.txt')
print(f"file '{file1}' has been uploaded into bucket '{bucket_name}'")