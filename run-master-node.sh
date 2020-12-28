#!/bin/bash
./clear_output.sh
ccsalloc --duration 2h --res=rset=1:ncpus=4:mem=30g:gpus=1:rtx2080=t ./start-master-node.sh
echo "Master node started!"