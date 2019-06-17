#! /bin/bash
un=$1
passwd=$2
curl -s -u "${un}:${passwd}" http://bamboo.cdk.com/rest/api/latest/plan.json?max-result=1 | jq -r '.plans.plan[] | .key' > temps.txt
while read line; do
 echo -n "${line}:   " >> times.txt
 curl -s -u "${un}:${passwd}" http://bamboo.cdk.com/rest/api/latest/result/${line}/latest.json | jq -r '.buildCompletedTime' >> times.txt
done < temps.txt
cat times.txt
