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
#clear previous outputs
./clear_output.sh $OUT_DIR
#run master node
./run-master-node.sh 2d $WORLD_N $TRAINING_DATA $OUT_DIR $PRETRAIN_FILE $EMBED_FILE $LIM_FLAG
#wait for output to be written
echo "Blocking for 10 seconds"
sleep 10
#assign master node address
MASTER_NODE_ADDR="$(head -n 1 start-master-node.sh.*.out)"
echo "Master node deployed at: $MASTER_NODE_ADDR"
#start normal nodes
./run-normal-nodes.sh 2d $MASTER_NODE_ADDR $WORLD_N $TRAINING_DATA $OUT_DIR $PRETRAIN_FILE $EMBED_FILE $LIM_FLAG