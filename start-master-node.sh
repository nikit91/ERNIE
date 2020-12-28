#!/bin/bash
#print the host name
MASTER_IP=$(hostname -i)
echo $MASTER_IP
#run the python script
python3 -m torch.distributed.launch --nproc_per_node=1 --nnodes=8 --node_rank=0 --master_addr=$MASTER_IP --master_port=8200 code/run_pretrain_dist_util.py --do_train --data_dir sample_ptdata/sample --bert_model ernie_base --output_dir sample_pretrain_out/ --task_name pretrain --fp16 --max_seq_length 256