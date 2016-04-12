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


# Get ami-launch-index, local ip, and public hostname
ami_launch_index=`curl --silent http://169.254.169.254/latest/meta-data/ami-launch-index`
ip=`curl --silent http://169.254.169.254/latest/meta-data/local-ipv4`
public_hostname=`curl --silent http://169.254.169.254/latest/meta-data/public-hostname`

echo "ami-launch-index is $ami_launch_index"
echo "local ip is $ip"
echo "public hostname is $public_hostname"


# Upload ami-launch-index and local ip into simpledb
aws sdb put-attributes --domain-name cs5300-hy456 --item-name $ami_launch_index --attributes Name=ip,Value=$ip,Replace=true Name=hostname,Value=$public_hostname,Replace=true
