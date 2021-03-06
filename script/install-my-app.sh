#!/bin/bash
N=3
F=1

KEY_ID=AKIAJ74UNLVZ6ISVOYFQ
KEY_VAL=7/G6b1/IKfzfK6r/HdkHnyrWjX6bvGh0pJEud8j2
DOMAIN_NAME=cs5300hy456
REBOOTNUM_PATH=/rebootnum.txt
SERVERS_PATH=/servers.txt


cd /home/ec2-user

# Install pip, awscli
# sudo yum -y upgrade python-setuptools
# sudo yum -y install python-pip python-wheel
# sudo pip install --upgrade pip
# sudo pip install awscli
# sudo pip install --upgrade awscli

# Config awscli
aws configure set aws_access_key_id ${KEY_ID}
aws configure set aws_secret_access_key ${KEY_VAL}
aws configure set region "us-east-1"

# Install all tomcat packages
sudo yum -y install "tomcat8-webapps" "tomcat8-docs-webapp" "tomcat8-admin-webapps"

#  Get ami-launch-index, local ip, and public hostname
ami_launch_index=`curl --silent http://169.254.169.254/latest/meta-data/ami-launch-index`
ip=`curl --silent http://169.254.169.254/latest/meta-data/local-ipv4`
public_hostname=`curl --silent http://169.254.169.254/latest/meta-data/public-hostname`

echo "ami-launch-index is $ami_launch_index"
echo "local ip is $ip"
echo "public hostname is $public_hostname"

# Upload ami-launch-index and local ip into simpledb
aws configure set preview.sdb true
aws sdb put-attributes --domain-name $DOMAIN_NAME --item-name $ami_launch_index --attributes Name=ip,Value=$ip,Replace=true Name=hostname,Value=$public_hostname,Replace=true

# generate parse.py
echo -e "import json\nimport sys\n\nwith open(sys.argv[1]) as f:\n    data = json.load(f)\n\nfor i in xrange(2, len(sys.argv)):\n    if isinstance(data, list):\n        data = data[int(sys.argv[i])]\n    else:\n        data = data[sys.argv[i]]\n\nprint data.strip()" > parse.py

# Write own server info
> ${SERVERS_PATH}
chmod 777 ${SERVERS_PATH}
echo "$ami_launch_index,$ip,$public_hostname" >> ${SERVERS_PATH}

# Download war package and server.xml
aws s3 cp s3://cs5300hy456/project1b.war .
sudo cp project1b.war /usr/share/tomcat8/webapps
# aws s3 cp s3://cs5300hy456/server.xml .
# sudo cp server.xml /usr/share/tomcat8/conf
sudo rm -rf /usr/share/tomcat8/webapps/ROOT

aws s3 cp s3://cs5300hy456/install-my-app.sh .
sudo chmod +x install-my-app.sh
sudo cp install-my-app.sh /


aws s3 cp s3://cs5300hy456/reboot.sh .
sudo chmod +x reboot.sh
sudo cp reboot.sh /

sudo echo -e "$N\n$F" > "/NF.txt"
sudo chmod 777 /NF.txt

# Generate rebootnum.txt if not exists, else do nothing, which means it's recovering from installation failure
if [ -f ${REBOOTNUM_PATH} ]
    then
        echo "rebootnum.txt already exists."
    else
        # Generate rebootnum.txt
        echo 0 > ${REBOOTNUM_PATH}
        chmod 777 ${REBOOTNUM_PATH}
fi


# wait for all servers complete uploading
num_servers=0
while [[ $num_servers -ne $N ]]; do    
    aws sdb select --select-expression "select count(*) from $DOMAIN_NAME" > count.json
    num_servers=`python parse.py count.json Items 0 Attributes 0 Value`
done

# Load all servers metadata from SimpleDB
aws sdb select --select-expression "select * from $DOMAIN_NAME" > data.json

for i in $(seq 0 $(($num_servers-1)))
do
    ami_launch_index=`python parse.py data.json Items $i Name`
    hostname=`python parse.py data.json Items $i Attributes 0 Value`
    ip=`python parse.py data.json Items $i Attributes 1 Value`
    echo "$ami_launch_index,$ip,$hostname" >> ${SERVERS_PATH}
done

rm count.json data.json

sudo service tomcat8 start
