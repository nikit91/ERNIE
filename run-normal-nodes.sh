#!/bin/bash
echo "Starting normal nodes"
for g_rank in $(seq 1 $(($3-1)))
do
ccsalloc --duration $1 --res=rset=1:ncpus=4:mem=30g:gpus=1:tesla=f start-normal-node.sh $g_rank $2 $3 $4 $5 $6 $7 $8
done
echo "Done! Please, check ccsinfo -s | grep <user_name> for allocations and resource usage."
