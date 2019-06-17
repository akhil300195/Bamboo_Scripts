#! /bin/bash
un=$1
passwd=$2
plan_key_file="tempPlanKey.txt"
latest_build_times="yearOldPlans.txt"
toBeEvaluated="plans_with_null.txt"
max_result=100

bamboo_url="http://bamboo.cdk.com/"


previous_year_date=$(gdate +%d%m%Y --date="-1 year")

cleanup()
{
	rm -f ${plan_key_file} ${latest_build_times} ${toBeEvaluated}
}

get_the_buildTimes()
{
	curl -s -u "${un}:${passwd}" http://bamboo.cdk.com/rest/api/latest/plan.json?max-result=${max_result} | jq -r '.plans.plan[] | .key' > ${plan_key_file}
	while read line; do
		last_build_date=$(curl -s -u "${un}:${passwd}" http://bamboo.cdk.com/rest/api/latest/result/${line}/latest.json | jq -r '.buildCompletedTime' | cut -d 'T' -f1)
		if [[ ${last_build_date} != "null" ]]
		then
			result=$(python compare_date.py `echo ${last_build_date} | cut -d'-' -f1` `echo ${last_build_date} | cut -d'-' -f2`  `echo ${last_build_date} | cut -d'-' -f3` ${previous_year_date})
			[[ ${result} == "True" ]] && echo "${line}: ${last_build_date}, URL: ${bamboo_url}/browse/${line}" >> ${latest_build_times}
		else
		    echo "${line}: null" >> ${toBeEvaluated}
		fi		
	done < ${plan_key_file}
	cat ${latest_build_times}
	echo "Please look into ${toBeEvaluated} file for Plans will null (latest build was stopped by the user )"
}

cleanup
get_the_buildTimes
