#!/bin/bash
aws ec2 run-instances --image-id ami-08111162 --count 3 --instance-type t2.micro --key-name us-east1-keypair --security-groups yhf_SG_useast --user-data file://installation2.sh --region us-east-1
