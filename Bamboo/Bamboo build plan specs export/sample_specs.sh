#!/bin/sh
yearoldplanlist="buildkey.txt"
bamboo_url="http://bamboo.cdk.com/"

export_the_specs()
{
while read line
do
    echo ${line}
    mkdir ${line}
    curl -s -u $un:$passwd "${bamboo_url}/exportSpecs/deploymentProject.action?deploymentProjectId=${line}" > ${line}/log.html
    python3 get_the_specs.py ${line}/log.html
done < ${yearoldplanlist}
}

export_the_specs


#http://bamboo-lab.cdk.com/deploy/viewDeploymentProjectEnvironments.action?id=94633990