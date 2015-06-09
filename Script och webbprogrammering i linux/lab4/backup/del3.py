#!/usr/bin/python
# -*- coding: utf-8 -*-

# Imports
import datetime
import sys
import re
from PyQt4 import QtGui
from PyQt4 import QtCore
from PyQt4.Qt import QMainWindow, QVBoxLayout, QLabel, QLineEdit, QListWidget, \
    QWidget, QMessageBox


# Check if two string match
def exact_match(phrase, word):
    b = r'(\s|^|$)' 
    res = re.match(b + "\"" + word + "\"" + b, phrase, flags=re.IGNORECASE)
    return bool(res)

# Write the input data to the file
def writeDataToFile(author, title, genre, genre2, dateRead, grade, comments):
    f = open('bibliotek.txt', 'a')
    f.write('"' + author + '", "' + title + '", "' + genre + '", "' + genre2 +
             '", ' + dateRead + ', ' + grade + ', "' + comments + '"\n')
    f.close()

# Get all titles from the file
def getTitlesFromFile():
    f = open('bibliotek.txt', 'r')
    titleList = []
    for line in f:
        splitLine = line.split(",", 2)
        titleList.append(splitLine[1].replace('"', '').lstrip())
    f.close()
    return titleList

# Find a book row with the 'titleToGet', return whole row
def findTitleData(titleToGet):
    f = open('bibliotek.txt', 'r')
    for line in f:
        splitLine = line.split(",", 2)
        if exact_match(splitLine[1], titleToGet):
            title = line[:-1]
    return title


# checks if a string contains digits
def containsDigit(string):
        containsDigit = False
            
        for x in string:
            if x.isdigit():
                containsDigit = True
                break
        return containsDigit
    
# Class that is the main window in the gui.
class MainWindow(QMainWindow):
    
    # Initiate Window
    def __init__(self, parent=None):
        
        super(MainWindow, self).__init__(parent)
        self.tab_widget = tab_widget(self)
        self.setWindowTitle('Bibliotek')
        self.setCentralWidget(self.tab_widget)
        self.centerOnScreen()
        self.show()
    
    # Moves the window to the center of the screen
    def centerOnScreen (self):  
        resolution = QtGui.QDesktopWidget().screenGeometry()
        self.move((resolution.width() / 2) - (self.frameSize().width() / 2),
                  (resolution.height() / 2) - (self.frameSize().height() / 2))

    
# Class used by Main window, contains all objects for the gui in two different
# Tabs.
class tab_widget(QtGui.QTabWidget):
    
    
    # Initiate Widget. 
    def __init__(self, parent=MainWindow):
        super(tab_widget, self).__init__()
        self.initUI()
    
    # Create interface, and connect buttons to signals.
    def initUI(self):
        vbox_inner = QVBoxLayout()
        grid = QtGui.QGridLayout()
        
        self.genreList = ['Fiction', 'Fact book', 'Poetry']
        
        # Tab 1: Add Data        
        self.author_label = QLabel("Author")
        self.author_text = QLineEdit()
        self.author_text.setPlaceholderText("Author")
        grid.addWidget(self.author_label, 0, 0)
        grid.addWidget(self.author_text, 1, 0)
        
        self.title_label = QLabel("Title")
        self.title_text = QLineEdit()
        self.title_text.setPlaceholderText("Title")

        grid.addWidget(self.title_label, 0, 1)
        grid.addWidget(self.title_text, 1, 1)

        self.date_label = QLabel("Date Read")
        self.date_text = QLineEdit()
        self.date_text.setPlaceholderText("yy-mm-dd")
        grid.addWidget(self.date_label, 2, 1)
        grid.addWidget(self.date_text, 3, 1)
        
        self.grade_label = QLabel("Grade")
        self.grade_text = QLineEdit()
        self.grade_text.setPlaceholderText("Grade 1-5")
        grid.addWidget(self.grade_label, 2, 0)
        grid.addWidget(self.grade_text, 3, 0)
        
        self.comments_label = QLabel("Comments")
        self.comments_text = QLineEdit()
        self.comments_text.setPlaceholderText("Comments here")
        grid.addWidget(self.comments_label, 4, 0)
        grid.addWidget(self.comments_text, 5, 0)

        self.genre_label = QLabel("Genre")
        self.genre_list = QListWidget(self)
        self.genre_list.addItems(self.genreList)
        self.genre_list.setObjectName('genre_list')
        self.genre_list.itemClicked.connect(self.item_clicked)
        grid.addWidget(self.genre_label, 6, 0)
        grid.addWidget(self.genre_list, 7, 0)
        
        self.genre2_label = QLabel("Genre2")
        self.genre2_list = QListWidget(self)
        self.genre2_list.setObjectName("genre2")
        self.genre2_list.addItem("Select genre first")
        grid.addWidget(self.genre2_label, 6, 1)
        grid.addWidget(self.genre2_list, 7, 1)
        
        
        # Tab 2:Get Data from file
        self.authorData = QLineEdit()
        self.authorData.setPlaceholderText("Author")
        self.authorData.setReadOnly(True)
        vbox_inner.addWidget(self.authorData)
        
        self.titleData = QLineEdit()
        self.titleData.setPlaceholderText("Title")
        self.titleData.setReadOnly(True)
        vbox_inner.addWidget(self.titleData)
        
        self.genreData = QLineEdit()
        self.genreData.setPlaceholderText("Genre")
        self.genreData.setReadOnly(True)
        vbox_inner.addWidget(self.genreData)
        
        self.genre2Data = QLineEdit()
        self.genre2Data.setPlaceholderText("Genre2")
        self.genre2Data.setReadOnly(True)
        vbox_inner.addWidget(self.genre2Data)
        
        self.dateData = QLineEdit()
        self.dateData.setPlaceholderText("Date read")
        self.dateData.setReadOnly(True)
        vbox_inner.addWidget(self.dateData)
        
        self.gradeData = QLineEdit()
        self.gradeData.setPlaceholderText("Grade")
        self.gradeData.setReadOnly(True)
        vbox_inner.addWidget(self.gradeData)
        
        self.commentsData = QLineEdit()
        self.commentsData.setPlaceholderText("Comments")
        self.commentsData.setReadOnly(True)
        vbox_inner.addWidget(self.commentsData)
        
        self.titles_list = QListWidget(self)
        self.titles_list.addItems(getTitlesFromFile())
        vbox_inner.addWidget(self.titles_list)
        
        
        self.getTitleData = QtGui.QPushButton("Get title data", self)
        self.getTitleData.clicked.connect(self.buttonClicked)
        vbox_inner.addWidget(self.getTitleData)
        
        
        self.submit = QtGui.QPushButton("Submit Data", self)
        self.submit.clicked.connect(self.buttonClicked)            
        grid.addWidget(self.submit, 8, 1)
            
        self.quit = QtGui.QPushButton("Exit", self)
        self.quit.clicked.connect(QtCore.QCoreApplication.instance().quit)
        self.quit.setMaximumWidth(80)
        grid.addWidget(self.quit, 8, 0)
    
        tab1 = QWidget()
        tab1.setLayout(grid)
        
        tab2 = QWidget()
        tab2.setLayout(vbox_inner)
    
        self.addTab(tab1, "Add Data")
        self.addTab(tab2, "Get Data")
        self.move(150, 150)
        self.show()
        
    # Function for when a QListWidgetItem is clicked if the genre_list is 
    # clicked, will check which genre was chosen and populate genre2_list
    # with subgenres of that genre.    
    def item_clicked(self):
        genre = self.sender()
        if genre.objectName() == "genre_list":
            curr = genre.currentItem().text()
            if curr == genreList[0]:
                genre2List = ["Detective", "autobiograpyh", "Horror", "Comedy"]
            elif curr == genreList[1]:
                genre2List = ["Technology", "Biology", "Chemistry", "Mathematics"]
            elif curr == genreList[2]:
                genre2List = ["Tirade", "Ballads", "Rhyme"]
            else:
                genre2List = ["None"]
            
            self.genre2_list.clear()
            self.genre2_list.addItems(genre2List)
       
    # Function for when a button is clicked, if 'Submit Data' is clicked,
    # will check that all input is correct format and if so write 
    # the data to the file. If the button pressed is 'Get title data'
    # will get the selected title in the QListWidget and show the row data
    def buttonClicked(self):
        sender = self.sender()
        if sender.text() == "Get title data":
            if self.titles_list.currentItem() != None:
                titleToGet = str(self.titles_list.currentItem().text())
                titleData = findTitleData(titleToGet)
                splitLine = []
                splitLine = titleData.split(",", 7)
                                
                self.authorData.setText(splitLine[0].replace('"', "").lstrip())
                self.titleData.setText(splitLine[1].replace('"', "").lstrip())
                self.genreData.setText(splitLine[2].replace('"', "").lstrip())
                self.genre2Data.setText(splitLine[3].replace('"', "").lstrip())
                self.dateData.setText(splitLine[4].replace('"', "").lstrip())
                self.gradeData.setText(splitLine[5].replace('"', "").lstrip())
                self.commentsData.setText(splitLine[6].replace('"', "").lstrip())
                
        elif sender.text() == "Submit Data":
            comments = str(self.comments_text.text())
        
            while True:
                author = str(self.author_text.text())
                if containsDigit(author) == True:
                    QMessageBox.about(self, "Wrong input", "Error: Author cant contain digits")
                    break
                
                if len(author) == 0:
                    QMessageBox.about(self, "Wrong input", "Author is a required field")
                    break
                
                title = str(self.title_text.text())
                try:
                    title = str(self.title_text.text())
                except:
                    QMessageBox.about(self, "Wrong input", "Error in comments, only A-z, 1-9 allowed")
                    break
                
                if len(title) == 0:
                    QMessageBox.about(self, "Wrong input", "Title is a required field")
                    break
                
                
                if self.genre_list.currentItem() != None:
                    genre = str(self.genre_list.currentItem().text())
                else:
                    QMessageBox.about(self, "Wrong input", "Error: Need to pick genre")
                    break
                                    
                if self.genre2_list.currentItem() != None:
                    genre2 = str(self.genre2_list.currentItem().text())
                else:
                    QMessageBox.about(self, "Wrong input", "Error: Need to pick genre2")
                    break
                
                d = str(self.date_text.text())  
                
                if len(d) == 0:
                    QMessageBox.about(self, "Wrong input", "Date is a required field")
                    break
                try:
                    dateRead = datetime.datetime.strptime(d, '%y-%m-%d').date() 
                except:
                    QMessageBox.about(self, "Wrong input", "Error: Date needs to be in format: yy-mm-dd")         
                    break
                
                
                try:
                    grade = int(str(self.grade_text.text()))
                except:
                    QMessageBox.about(self, "Wrong input", "Error: Grade needs to be a number between 1-5")
                    break
                
                if grade > 5 or grade < 1:
                    QMessageBox.about(self, "Wrong input", "Error: Grade needs to be a number between 1-5")
                    break
                
                try:
                    comments = str(self.comments_text.text())
                except:
                    QMessageBox.about(self, "Wrong input", "Error: in comments, only A-z, 1-9 allowed")
                    break
                
                if len(comments) == 0:
                    comments = "No comments"
                
                writeDataToFile(author, title, genre, genre2, str(dateRead), str(grade), comments)
                self.titles_list.clear()
                self.titles_list.addItems(getTitlesFromFile())
                QMessageBox.about(self, "Information", "Data added successfully")
                break

# Main
def main():
   app = QtGui.QApplication(sys.argv)
   gui = MainWindow()
   sys.exit(app.exec_())

if __name__ == '__main__':
   main()
   
