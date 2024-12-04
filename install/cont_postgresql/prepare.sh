#!/bin/sh

alias_host_name="b_db"
container_ip=$(hostname -I|awk -F\  '{ print $(NF) }')

echo "Set route: $alias_host_name -> $container_ip"
grep -q $alias_host_name /etc/hosts
hosts_have_alias=$(echo $?)
if [ $hosts_have_alias -ne 0 ]; then
    # no data for alias_host_name
    echo "$container_ip $alias_host_name" >> /etc/hosts
else
    # data for alias_host_name already exists
    cp /etc/hosts ~/hosts.new

    reg_exp_ip="((1?[0-9][0-9]?|2[0-4][0-9]|25[0-5])\.){3}(1?[0-9][0-9]?|2[0-4][0-9]|25[0-5])"
    sed -ri "s/$reg_exp_ip \t$alias_host_name/$container_ip $alias_host_name/" ~/hosts.new
    sed -ri "s/$reg_exp_ip $alias_host_name/$container_ip $alias_host_name/" ~/hosts.new

    cat ~/hosts.new > /etc/hosts
fi
