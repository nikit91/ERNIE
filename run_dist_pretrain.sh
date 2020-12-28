#!/bin/bash
#run master node
./run-master-node.sh
#wait for output to be written
echo "Blocking for 10 seconds"
sleep 10
#assign master node address
MASTER_NODE_ADDR=$(head -n 1 start-master-node.sh.*.out)
echo "Master node deployed at: $MASTER_NODE_ADDR"
#start normal nodes
./run-normal-nodes.sh