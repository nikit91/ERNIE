#!/bin/bash
#print the host name
echo $CCS_NODES
NCCL_DEBUG=INFO
echo "Starting Master node -- Local address $CCS_NODES -- File name $4"
#run the python script
python3 -m torch.distributed.launch --nproc_per_node=1 --nnodes=$1 --node_rank=0 --master_addr=$CCS_NODES --master_port=8989 $4 --do_train --data_dir $2 --bert_model ernie_base --output_dir $3 --task_name pretrain --fp16 --max_seq_length 256