#!/usr/bin/python
# -*- coding: utf-8 -*-

import re
import sys
import socket
from os.path import os

# Check if program arguments are more then one, in that case take the arguments.
# The first argument is the host and the second is which port to listen on. 
if len(sys.argv) > 1:
    try:
        HOST = sys.argv[1]  # Symbolic name meaning the local host
        PORT = int(sys.argv[2])  # Arbitrary non-privileged port
    except:
        print "Error in arguments. (\"HOST PORT\")"
        HOST = ''
        PORT = 24069
else:
    HOST = ''
    PORT = 24069

# Find a book row with the 'titleToGet', return whole row
def findTitleData(titleToGet):
    f = open('bibliotek.txt', 'r')
    
    for line in f:
        splitLine = line.split(",", 2)
        if exact_match(splitLine[1], titleToGet):
            title = line[:-1]
    return title

# Get all titles from the file
def getTitlesFromFile():
    if os.path.isfile("bibliotek.txt"):
        f = open('bibliotek.txt', 'r')
        titleList = ""
        for line in f:
            titleList += line.strip('\n') + ":"
        f.close()
        return titleList[:-1]
    else:
        return "Empty"
# Check if two strings match
def exact_match(phrase, word):
    b = r'(\s|^|$)' 
    res = re.match(b + "\"" + word + "\"" + b, phrase, flags=re.IGNORECASE)
    return bool(res)


# Write the input data to the file
def writeDataToFile(author, title, genre, genre2, dateRead, grade, comments):
    f = open('bibliotek.txt', 'a+')
    f.write('"' + author + '", "' + title + '", "' + genre + '", "' + genre2 + 
             '", ' + dateRead + ', ' + grade + ', "' + comments + '"\n')
    f.close()
    

# while loop until user exits with CTRL+C
while True:
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.setsockopt(socket.SOCK_STREAM, socket.SO_REUSEADDR, 1)
    try:
        sock.bind((HOST, PORT))
    except socket.error , msg:
        print 'Bind failed. Error code: ' + str(msg[0]) + ' Error message: ' + msg[1]
        sys.exit(1)

    print 'Socket now listening (Exit server with CTRL+C)'
    sock.listen(1)
        
    (conn, addr) = sock.accept()
    
    # While loop for the established connection
    while 1:
        reply = None
        print 'Server: got connection from client ' + addr[0] + ':' + str(addr[1])
        data = conn.recv(1024)
            
        tokens = data.split(' ', 1)
        command = tokens[0]
        print "["+command+"]"
        
        if command == 'GET_ALL_DATA':
            print addr[0] + ':' + str(addr[1]) + ' sends GET_ALL_DATA'
            titleToGet = data.split(' ', 2)
            reply = getTitlesFromFile()
            if len(reply) == 0:
                reply = "Empty"
            print "sending reply"
            conn.send(reply)
        elif command == 'SEND_ROW_DATA':
            print addr[0] + ':' + str(addr[1]) + ' sends SEND_ROW_DATA'
            row = data.split(' ', 1)
            entries = row[1].split(',')
            
            # Remove trailing quotation, newlines and blank spaces at start 
            # of string.
            bookData = []
            for x in entries:
                bookData.append(x.replace('"', "").lstrip().rstrip())
                
            writeDataToFile(bookData[0], bookData[1], bookData[2], bookData[3],
                             bookData[4], bookData[5], bookData[6])
            
            reply = '200 OK'
            print "Sending "+reply
            conn.send(reply)
        elif command == 'GET_TITLE_DATA':
            print addr[0] + ':' + str(addr[1]) + ' sends GET_TITLE_DATA'
            row = data.split(' ', 1)
            print "Title ["+row[1]+"]"
            if row[1] == "Empty":
                reply = "Empty"
            else:
                titleData = findTitleData(row[1])
                reply = "TITLE_DATA "+titleData
                print "Sending reply"
                conn.send(reply)
                
        elif command == 'EXIT':
            print addr[0] + ':' + str(addr[1]) + ' sends EXIT'
            conn.send('200 OK')
            conn.close()
            sock.close()
            break
        else:
            print '400 Command not valid.'
            conn.send("400 Command not valid")
            
    conn.close()
    sock.close()
