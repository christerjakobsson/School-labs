#!/usr/bin/python

import sys
import datetime

# Gets the date from the users input.
def ObtainDate():
    isValid=False
    while not isValid:
        userIn = raw_input("Type Date yy-mm-dd: ")
        try: 
            d = datetime.datetime.strptime(userIn, "%y-%m-%d")
            isValid=True
        except:
            print "Wrong format, try again!"
    return d.date()

# Gets grade from the users input
def getGrade():
	while True:
		try:
			g = int(input('Grade (1-5): '))		
		except:
			print "Grade needs to be a integer"
			continue
		else:
			if g > 5 or g < 1:
				print "Grade needs to be in range 1-5"
				continue
			else:	
				break
	return g

# Gets all string based inputs from the user.
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

# Gets the author from the user input.
def getAuthor(string):
	while True:
		containsDigit = False
		try:
			g = input(string+': ')		
		except:
			print string+" should be text with surrounding \"\", ex: \""+string+"\"."
		else:
			for x in g:
				if x.isdigit():
					print "Author cant contain digits, try again."
					containsDigit = True
					break
			if containsDigit:
				continue
			else:
				break
	return g

# Get genre from user input.
def getGenre(genreList):
	
	genre = getInput("Genre") 
	while genre not in genreList:
		print("Genre must be:"),
		print genreList	
		genre = getInput("Genre") 
	return genre

# Get genre2 from user input, based on genre
def getGenre2(genre, genreList):
	if genre is genreList[0]:
		genre2List = ["Detective", "autobiograpyh", "Horror", "Comedy"]
	elif genre is genreList[1]:
		genre2List = ["Technology", "Biology", "Chemistry", "Mathematics"]
	elif genre is genreList[2]:
		genre2List = ["Tirade", "Ballads", "Rhyme"]
	else:
		genre2List = ["None"]

	genre2 = getInput("Genre2") 
	while genre2 not in genre2List:
		print("Genre2 must be:"),
		print genre2List
		genre2 = getInput("Genre2")
	return genre2
	
	
author = getAuthor("Author")
title = getInput("Title")
	
genreList = ['Fiction', 'Fact book', 'Poetry']
genre = getGenre(genreList)

genre2 = getGenre2(genre, genreList)
dateRead = ObtainDate()
grade = getGrade()

comments = getInput("Comments") 


f = open('bibliotek.txt', 'a')
f.write('"'+author+'", "'+title+'", "'+genre+'", "'+genre2+'", '+str(dateRead)+', '+str(grade)+', "'+comments+'"\n')
f.close()
