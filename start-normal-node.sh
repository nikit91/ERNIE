#!/bin/bash
NCCL_DEBUG=INFO
echo "Starting normal node: $1 -- Master node address: $2 -- Local address $CCS_NODES "
python3 -m torch.distributed.launch --nproc_per_node=1 --nnodes=$3 --node_rank=$1 --master_addr=$2 --master_port=8989 $6 --do_train --data_dir $4 --bert_model ernie_base --output_dir $5 --task_name pretrain --fp16 --max_seq_length 256