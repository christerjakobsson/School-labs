#!/usr/bin/python

import sys
import glob
import os

# Function that opens all the .txt files in the directories given as arguments
# and prints the two first lines in them to standard out.
def openAndReadTxtFiles():
	lista = []
		
	for i in range(1, len(sys.argv)):
	   	lista.append(sys.argv[i])
	
	for path in lista:
      		for filename in glob.glob(os.path.join(path, '*.txt')):
      			openFile = open(filename, 'r')
			print os.path.basename(filename)
			print "\t"+openFile.readline()[:-2]
			print "\t"+openFile.readline()[:-2]
	return

# If there is more then one argument to the program it run openAndReadTxtFiles.
if (len(sys.argv) <= 1):
    sys.stderr.write('Not enough arguments, exiting...\n')
    raise SystemExit(1)
else:    
	openAndReadTxtFiles()
