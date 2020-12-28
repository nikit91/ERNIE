#!/bin/bash
echo "Starting normal node: $1 -- Master node address: $2 -- Local address $CCS_NODES "
python3 -m torch.distributed.launch --nproc_per_node=1 --nnodes=8 --node_rank=$1 --master_addr=$2 --master_port=8200 code/run_pretrain_dist_util.py --do_train --data_dir sample_ptdata/sample --bert_model ernie_base --output_dir sample_pretrain_out/ --task_name pretrain --fp16 --max_seq_length 256