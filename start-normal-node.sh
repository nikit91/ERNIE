#!/bin/bash
NCCL_DEBUG=INFO
FILE_NAME=$2
MASTER_NODE_ADDR=""
#wait for master proc log file to be created
for (( ; ; ))
do
  echo "Normal node $1 checking for Master proc log file $FILE_NAME"
  if [ -f "$FILE_NAME" ]; then
    echo "$FILE_NAME found."
    sleep 10
    MASTER_NODE_ADDR="$(head -n 1 ${FILE_NAME})"
    break
  else
    echo "$FILE_NAME not yet created. Checking again in 10 secs."
    sleep 10
  fi
done
echo "Starting normal node: $1 -- Master node address: $MASTER_NODE_ADDR -- Local address $CCS_NODES -- File name $6"
python3 -m torch.distributed.launch --nproc_per_node=1 --nnodes=$3 --node_rank=$1 --master_addr=$MASTER_NODE_ADDR --master_port=8286 $6 --do_train --data_dir $4 --bert_model ernie_base --output_dir $5 --task_name pretrain --fp16 --max_seq_length 256 --vec_file $7 --use_lim_ents $8