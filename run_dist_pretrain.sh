#!/bin/bash
#get world size
WORLD_N=$1
#get training data path
TRAINING_DATA=$2
#get output directory
OUT_DIR=$3
#get python file to run
PRETRAIN_FILE=$4
#get embeddings file
EMBED_FILE=$5
#get limited embeddings flag
LIM_FLAG=$6
#get resource reserve duration
RES_DUR=$7
#clear previous outputs
./clear_output.sh $OUT_DIR
#run master node
./run-master-node.sh $RES_DUR $WORLD_N $TRAINING_DATA $OUT_DIR $PRETRAIN_FILE $EMBED_FILE $LIM_FLAG
#Ask for process id
#wait for output to be written
read MASTER_PROC_ID
MASTER_PROC_LOG_FILE="start-master-node.sh.${MASTER_PROC_ID}.out"
echo "Expected master proc log file name: ${MASTER_PROC_LOG_FILE}"
#echo "Blocking for 10 seconds"
#sleep 10
#assign master node address
#MASTER_NODE_ADDR="$(head -n 1 start-master-node.sh.*.out)"
#echo "Master node deployed at: $MASTER_NODE_ADDR"
#start normal nodes
./run-normal-nodes.sh $RES_DUR $MASTER_PROC_LOG_FILE $WORLD_N $TRAINING_DATA $OUT_DIR $PRETRAIN_FILE $EMBED_FILE $LIM_FLAG