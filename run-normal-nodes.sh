#!/bin/bash
echo "Starting normal nodes"
ccsalloc --duration 1d --res=rset=1:ncpus=4:mem=30g:gpus=1:rtx2080=t start-normal-node.sh 1 $1
ccsalloc --duration 1d --res=rset=1:ncpus=4:mem=30g:gpus=1:rtx2080=t start-normal-node.sh 2 $1
ccsalloc --duration 1d --res=rset=1:ncpus=4:mem=30g:gpus=1:rtx2080=t start-normal-node.sh 3 $1
ccsalloc --duration 1d --res=rset=1:ncpus=4:mem=30g:gpus=1:rtx2080=t start-normal-node.sh 4 $1
ccsalloc --duration 1d --res=rset=1:ncpus=4:mem=30g:gpus=1:rtx2080=t start-normal-node.sh 5 $1
ccsalloc --duration 1d --res=rset=1:ncpus=4:mem=30g:gpus=1:rtx2080=t start-normal-node.sh 6 $1
ccsalloc --duration 1d --res=rset=1:ncpus=4:mem=30g:gpus=1:rtx2080=t start-normal-node.sh 7 $1
echo "Done! Please, check ccsinfo -s | grep <user_name> for allocations and resource usage."