#! /bin/bash
un=$1
passwd=$2
plan_key_file="tempPlanKey.txt"
latest_build_times="lastSuccessfulBuildTimes.txt"
max_result=50

cleanup()
{
	rm -f ${plan_key_file} ${latest_build_times}
}

get_the_buildTimes()
{
	curl -s -u "${un}:${passwd}" http://bamboo.cdk.com/rest/api/latest/plan.json?max-result=${max_result} | jq -r '.plans.plan[] | .key' > ${plan_key_file}
	while read line; do
		echo -n "${line}:   " >> ${latest_build_times}
		curl -s -u "${un}:${passwd}" http://bamboo.cdk.com/rest/api/latest/result/${line}/latest.json | jq -r '.buildCompletedTime' | cut -d 'T' -f1 >> ${latest_build_times}
	done < ${plan_key_file}
	cat ${latest_build_times}
}

cleanup
get_the_buildTimes
