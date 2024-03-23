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