REBOOTNUM_PATH=/rebootnum.txt

reboot_num=`cat ${REBOOTNUM_PATH}`
echo "The reboot number is $reboot_num"
echo $(($reboot_num+1)) > ${REBOOTNUM_PATH}

# Re-run install-my.app.sh to recover from the installation script failure
cd /
sudo ./install-my-app.sh

sudo service tomcat8 start
