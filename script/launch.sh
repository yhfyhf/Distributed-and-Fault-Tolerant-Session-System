#!/bin/bash

N=3

aws ec2 run-instances --image-id ami-08111162 --count ${N} --instance-type t2.micro --key-name us-east1-keypair --security-groups yhf_SG_useast --user-data file://install-my-app.sh --region us-east-1
aws s3 cp ../out/artifacts/project1b/project1b.war s3://cs5300hy456
aws s3 cp reboot.sh s3://cs5300hy456
aws s3 cp install-my-app.sh s3://cs5300hy456
