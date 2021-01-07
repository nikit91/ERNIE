#!/bin/bash
#get world size
WORLD_N=$1
#get training data path
TRAINING_DATA=$2
#get output directory
OUT_DIR=$3
#clear previous outputs
./clear_output.sh $OUT_DIR
#run master node
./run-master-node.sh 3d $WORLD_N $TRAINING_DATA $OUT_DIR
#wait for output to be written
echo "Blocking for 100 seconds"
sleep 100
#assign master node address
MASTER_NODE_ADDR="$(head -n 1 start-master-node.sh.*.out)"
echo "Master node deployed at: $MASTER_NODE_ADDR"
#start normal nodes
./run-normal-nodes.sh 3d $MASTER_NODE_ADDR $WORLD_N $TRAINING_DATA $OUT_DIR