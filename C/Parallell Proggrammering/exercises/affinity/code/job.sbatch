#!/bin/bash

numnodes=$(numactl --hardware | head -n 1 | awk '{print $2}')
maxnode=$((numnodes - 1))
echo "# Number of nodes: $numnodes"
echo

for node in $(seq 0 $maxnode)
do
    echo "# Node $node"
    numactl --cpunodebind=0 --membind=$node ./test
    echo
done
