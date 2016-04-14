REBOOTNUM_PATH=/rebootnum.txt

reboot_num=`cat ${REBOOTNUM_PATH}`
echo "The reboot number is $reboot_num"
echo $(($reboot_num+1)) > ${REBOOTNUM_PATH}

sudo service tomcat8 start
