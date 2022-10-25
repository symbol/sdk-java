#!/bin/bash

source "$(dirname $0)/../catbuffer/scripts/schema_lists.sh"
source "$(dirname $0)/../catbuffer/scripts/generate_batch.sh"

if [ "$#" -lt 1 ]; then
	echo "usage: script <builder> <nis2_root>"
	exit 1
fi

builder="$1"
transaction_inputs=("${transaction_inputs[@]}")
if [ "${builder}" = "cpp_builder" ]; then
	# "aggregate/aggregate" tracked by issue #26
	delete=("aggregate/aggregate")
	transaction_inputs=("${transaction_inputs[@]/${delete}}")
fi

if [ "$#" -lt 2 ]; then
	PYTHONPATH=".:${PYTHONPATH}" generate_batch transaction_inputs "catbuffer" ${builder}
else
	nis2_root="$2"
	rm -rf catbuffer/_generated/${builder}
	PYTHONPATH=".:${PYTHONPATH}" generate_batch transaction_inputs "catbuffer" ${builder}
	cp catbuffer/_generated/${builder}/* ${nis2_root}/sdk/src/builders/
fi
