#!/bin/bash
#sample CCS_NODES will look like this "node03-016 gpu16 gpu01 gpu08 gpu12"
echo "Boot script started. CCS_NODES: ${CCS_NODES}"
MASTER_NODE_FOUND_FLAG="n"
#Required variables
#Hostname of Master node
MASTER_NODE_ADDR=""
#Port to communicate master node on
MASTER_PORT=$1
#Total number of nodes involved in distributed training
TOT_NODE_COUNT=$(($(wc -w <<< "$CCS_NODES")-1))
#Global rank of the current node (0 is reserved for master node)
NODE_RANK=0
#Name of the python script to execute
PY_FILE=$2
#directroy for input data
INPUT_DATA_DIR=$3
#directory to write output data to
OUTPUT_DATA_DIR=$4
#path to kg embeddings file
VEC_FILE=$5
#flag for enabling limited entity check y/n
LIM_ENT_FLAG=$6

for node in $CCS_NODES :
do
	if [[ $node =~ ^.*[gG][pP][uU].*$ ]]
	then
		echo "Deciding task for node $node"
		#Decide master GPU node
		if [ "$MASTER_NODE_FOUND_FLAG" == "n" ]; then
		  echo "$node to be assigned as Master node"
		  #set as master node
		  MASTER_NODE_ADDR=$node
		  MASTER_NODE_FOUND_FLAG="y"
		  #send the job
		  ssh -n $node $PC2PFS/hpc-prf-nina/nikit/ERNIE/up-dist-training-node.sh $MASTER_NODE_ADDR $MASTER_PORT $TOT_NODE_COUNT $NODE_RANK $PY_FILE $INPUT_DATA_DIR $OUTPUT_DATA_DIR $VEC_FILE $LIM_ENT_FLAG >& "${node}-master-${CCS_REQID}.log"
		  #increment node rank
      NODE_RANK=$(($NODE_RANK + 1))
    else
      #set as worker node
      echo "$node to be assigned as worker node $NODE_RANK"
      #send the job
      ssh -n $node $PC2PFS/hpc-prf-nina/nikit/ERNIE/up-dist-training-node.sh $MASTER_NODE_ADDR $MASTER_PORT $TOT_NODE_COUNT $NODE_RANK $PY_FILE $INPUT_DATA_DIR $OUTPUT_DATA_DIR $VEC_FILE $LIM_ENT_FLAG >& "${node}-worker-${CCS_REQID}.log"
      #increment node rank
      NODE_RANK=$(($NODE_RANK + 1))
		fi
	fi
done

echo "All jobs sent to the respective nodes. Logs available for individual node at <node_name>.log"