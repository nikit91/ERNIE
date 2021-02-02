#!/bin/bash
ccsalloc --duration $1 --res=rset=1:ncpus=4:mem=30g:gpus=1:tesla=f start-master-node.sh $2 $3 $4 $5 $6 $7
echo "Master node started!"