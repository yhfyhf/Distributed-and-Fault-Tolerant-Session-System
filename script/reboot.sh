REBOOTNUM_PATH=/home/ec2-user/rebootnum.txt

reboot_num=`cat ../src/rebootnum.txt`
echo "The reboot number is $reboot_num"
echo $(($reboot_num+1)) > ../src/rebootnum.txt

sudo service tomcat8 start
