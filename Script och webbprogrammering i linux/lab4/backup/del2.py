#!/usr/bin/python

import sys
import re

def getInput(string):
	while True:
		try:
			g = input(string+': ')		
		except:
			print string+" should be text with surrounding \"\", ex: \""+string+"\"."
			continue
		else:
			break
	return g

def exact_match(phrase, word):
	b = r'(\s|^|$)' 
	res = re.match(b + "\""+word+"\"" + b, phrase, flags=re.IGNORECASE)
	return bool(res)

def printLine(titleToSearch, f):
	for line in f:
		splitLine = line.split(",", 2)
		if exact_match(splitLine[1], titleToSearch):
			print(line[:-1])
	return 


f = open('bibliotek.txt', 'r')
if (len(sys.argv) > 1):
	printAll = sys.argv[1]
	if sys.argv[1] is printAll:
		print(f.read()),
	else:
	    	lista = []
		
		for i in range(1, len(sys.argv)):
		   	lista.append(sys.argv[i])
	
		for x in lista:
			f.seek(0)
			printLine(x, f)
else:
	titleToSearch = getInput("Title To Search For (For all, type \"ALL\")")


	if titleToSearch is "ALL":
	    		print(f.read()),
	  
	else:
		printLine(titleToSearch ,f)
