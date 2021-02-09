#!/bin/bash
ccsalloc --duration 10h --res=rset=10:ncpus=4:mem=30g:gpus=1:rtx2080=t+1:ncpus=2 dist-train-boot.sh 8821 "code/run_pretrain_dist_util_dbp.py" "pretrain_data/merge_dbp" "pretrain_dbp_complex_pbg_dot_liment_out_boot/" "kg_embeddings/complex_pbg_dot_dbp1504/spec_ent_vectors.txt" y