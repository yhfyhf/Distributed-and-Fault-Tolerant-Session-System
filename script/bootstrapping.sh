DOMAIN_NAME=cs5300hy456


function isinstalled {
    if yum list installed "$@" >/dev/null 2>&1; then
        true
    else
        false
    fi
}

# Install all tomcat packages
packages=( "tomcat8-webapps" "tomcat8-docs-webapp" "tomcat8-admin-webapps" )
for package in "${packages[@]}"
do
    if isinstalled $package; then
        echo "$package is installed."
    else
        sudo yum -y install $package
    fi
done

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

# Load all servers metadata from SimpleDB
aws sdb select --select-expression "select count(*) from $DOMAIN_NAME" > count.json
num_servers=`python parse.py count.json Items 0 Attributes 0 Value`

aws sdb select --select-expression "select * from $DOMAIN_NAME" > data.json
> servers.txt

for i in $(seq 0 $(($num_servers-1)))
do
    ami_launch_index=`python parse.py data.json Items $i Name`
    hostname=`python parse.py data.json Items $i Attributes 0 Value`
    ip=`python parse.py data.json Items $i Attributes 1 Value`
    echo "$ami_launch_index,$ip,$hostname" >> servers.txt
done

rm count.json data.json
