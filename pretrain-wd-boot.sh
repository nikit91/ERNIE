#!/bin/bash
ccsalloc --duration 1h --res=rset=2:ncpus=4:mem=30g:gpus=1:tesla=f+1:ncpus=2 dist-train-boot.sh 8820 "code/run_pretrain_dist_util.py" "pretrain_data/merge" "pretrain_wd_liment_out_boot/" "kg_embed/entity2vec.vec" y