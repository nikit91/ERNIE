#!/bin/bash
#set all required variables
#Hostname of Master node
MASTER_NODE_ADDR=$1
#Port to communicate master node on
MASTER_PORT=$2
#Total number of nodes involved in distributed training
TOT_NODE_COUNT=$3
#Global rank of the current node (0 is reserved for master node)
NODE_RANK=$4
#Name of the python script to execute
PY_FILE=$5
#directroy for input data
INPUT_DATA_DIR=$6
#directory to write output data to
OUTPUT_DATA_DIR=$7
#path to kg embeddings file
VEC_FILE=$8
#flag for enabling limited entity check y/n
LIM_ENT_FLAG=$9
echo "Starting Training node: $NODE_RANK -- Master node address: $MASTER_NODE_ADDR -- Local address $CCS_NODES -- File name $PY_FILE"
python3 -m torch.distributed.launch --nproc_per_node=1 --nnodes=$TOT_NODE_COUNT --node_rank=$NODE_RANK --master_addr=$MASTER_NODE_ADDR --master_port=$MASTER_PORT $PY_FILE --do_train --data_dir $INPUT_DATA_DIR --bert_model ernie_base --output_dir $OUTPUT_DATA_DIR --task_name pretrain --fp16 --max_seq_length 256 --vec_file $VEC_FILE --use_lim_ents $LIM_ENT_FLAG