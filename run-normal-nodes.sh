#!/bin/bash
echo "Starting normal nodes"
#MASTER_NODE_ADDR=$1
ccsalloc --duration 2h --res=rset=1:ncpus=4:mem=30g:gpus=1:rtx2080=t start-normal-node.sh 1 $MASTER_NODE_ADDR
ccsalloc --duration 2h --res=rset=1:ncpus=4:mem=30g:gpus=1:rtx2080=t start-normal-node.sh 2 $MASTER_NODE_ADDR
ccsalloc --duration 2h --res=rset=1:ncpus=4:mem=30g:gpus=1:rtx2080=t start-normal-node.sh 3 $MASTER_NODE_ADDR
ccsalloc --duration 2h --res=rset=1:ncpus=4:mem=30g:gpus=1:rtx2080=t start-normal-node.sh 4 $MASTER_NODE_ADDR
ccsalloc --duration 2h --res=rset=1:ncpus=4:mem=30g:gpus=1:rtx2080=t start-normal-node.sh 5 $MASTER_NODE_ADDR
ccsalloc --duration 2h --res=rset=1:ncpus=4:mem=30g:gpus=1:rtx2080=t start-normal-node.sh 6 $MASTER_NODE_ADDR
ccsalloc --duration 2h --res=rset=1:ncpus=4:mem=30g:gpus=1:rtx2080=t start-normal-node.sh 7 $MASTER_NODE_ADDR
echo "Done! Please, check ccsinfo -s | grep <user_name> for allocations and resource usage."