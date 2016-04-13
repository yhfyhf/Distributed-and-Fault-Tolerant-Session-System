#!/bin/bash

N=1

aws ec2 run-instances --image-id ami-08111162 --count ${N} --instance-type t2.micro --key-name us-east1-keypair --security-groups yhf_SG_useast --user-data file://installation.sh --region us-east-1
aws s3 cp ../out/artifacts/project1b_war/project1b_war.war s3://cs5300hy456
