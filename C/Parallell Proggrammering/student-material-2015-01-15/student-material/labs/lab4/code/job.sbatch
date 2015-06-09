#!/bin/bash

#SBATCH -A 5DV152-VT15
#SBATCH --time=00:60:00
#SBATCH -N 1
#SBATCH -c 48
#SBATCH --output=raw-data.txt

# Change these settings according to your design.
HEIGHT=400
WIDTH=400
PIXELSIZE=0.01
CENTERREAL=0.0
CENTERIMAG=0.0
MAXITERATIONS=255

# You can probably keep this intact.
./mandel-basic $HEIGHT $WIDTH $PIXELSIZE $CENTERREAL $CENTERIMAG $MAXITERATIONS
