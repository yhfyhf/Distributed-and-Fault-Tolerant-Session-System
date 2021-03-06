aws configure set preview.sdb true

aws sdb create-domain --domain-name cs5300hy456
aws sdb domain-metadata --domain-name cs5300hy456
aws sdb put-attributes --domain-name cs5300hy456 --item-name server1 --attributes Name=ip,Value=192.168.0.3,Replace=true&Name=domain,Value=baidu.com,Replace=true
aws sdb get-attributes --domain-name cs5300hy456 --item-name server1

aws sdb delete-domain --domain-name cs5300hy456

aws sdb select --select-expression "select count(*) from cs5300hy456"
