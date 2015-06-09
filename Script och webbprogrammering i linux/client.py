#!/usr/bin/python
# -*- coding: utf-8 -*-

# Imports
from PyQt4.QtGui import QMainWindow, QDialog, QFormLayout, QLabel, QLineEdit, \
    QDialogButtonBox, QPushButton, QVBoxLayout, QTableWidget, QTableWidgetItem, \
    QAbstractItemView,QApplication
from PyQt4 import QtGui, QtCore
from PyQt4.Qt import QListWidget, Qt, QMessageBox, QWidget
import sys
import datetime
import httplib,json,urllib,requests


global data
data = {'Author':[],
        'Title':[],
        'Genre':[],
        'Genre2':[],
        'Date Read':[],
        'Grade':[],
        'Comments':[]}
 
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
    
    # Create the menu for the main window. Contains a button to connect, 
    # disconnect and exit.
    def makeMenu(self):
        menubar = self.menuBar()
        fileMenu = menubar.addMenu('&File')
        exitAction = QtGui.QAction(QtGui.QIcon('exit.png'), '&Exit', self)
        exitAction.setShortcut('Ctrl+Q')
        exitAction.setStatusTip('Exit application')
        exitAction.triggered.connect(self.exitAction)
        fileMenu.addAction(exitAction)
        
        helpMenu = menubar.addMenu('&Help')
        helpAction = QtGui.QAction(QtGui.QIcon('hell.png'), '&Help', self)
        helpAction.triggered.connect(self.helpAction)

        helpMenu.addAction(helpAction)

        self.date = 0

    def exitAction(self):
        sys.exit(0)

    def helpAction(self):
        #TODO
        print "HELP WINDOW"

    # Initiate Window
    def __init__(self, parent=None):
        
        super(MainWindow, self).__init__(parent)
        self.tab_widget = tab_widget(self)
        self.setWindowTitle('Book Library')
        self.setCentralWidget(self.tab_widget)
        self.centerOnScreen()
        
        self.makeMenu()
                
        self.show()
        
    
    # Send BookData to server.
    def sendBookData(self, author, title, genre, genre2, dateRead, grade, comments):
        data = json.dumps({'command':'add_book', 
        'title':title,
        'author':author,
        'genre':genre,
        'genre2':genre2,
        'dateRead':dateRead,
        'grade':grade,
        'comments':comments}) 

        r = requests.post('http://shinowa.tk/lab6/add_book.php', data)
        print r.status_code
        if(r.status_code == 200):
            QMessageBox.warning(None, "Success", "Data added successfully")
        elif(r.status_code == 404):
            QMessageBox.warning(None, "Failure", "Failed to add data")

        
   
    
    # Moves the window to the center of the screen
    def centerOnScreen (self):  
        resolution = QtGui.QDesktopWidget().screenGeometry()
        self.move((resolution.width() / 2) - (self.frameSize().width() / 2),
                  (resolution.height() / 2) - (self.frameSize().height() / 2))

    # Event when user clicks the windows cross.
    def closeEvent(self, event):
        sys.exit(0)        
            

# Get a titles BookData from the server.
def getTitleData(self, titleToGet):
    data = json.dumps({'command':'get_title', 'title':titleToGet}) 

    data = requests.post('http://shinowa.tk/lab6/add_book.php', data)

    if(data.status_code != 200):
        QMessageBox.warning(None, "Failure", "Connection failure")
        return "Empty"
    return data.json()[0]
        
# Function to get all the data from the file from the server
def getAllTitles(self):
    data = json.dumps({'command':'get_list'}) 
    data = requests.post('http://shinowa.tk/lab6/add_book.php', data)
    output = data.json()

    titleList = []

    for x in output:
        titleList.append(x['title'])

    for x in titleList:
        print x

    if(data.status_code != 200):
        QMessageBox.warning(None, "Failure", "Failed to get titles")
        return ""

    return titleList 

def removeTitle(titleToRemove):
        data = json.dumps({'command':'remove_book', 
        'titleToRemove':titleToRemove}) 

        r = requests.post('http://shinowa.tk/lab6/add_book.php', data)
        print r.status_code
        if(r.status_code == 200):
            QMessageBox.warning(None, "Success", "Title removed successfully")
        elif(r.status_code == 404):
            QMessageBox.warning(None, "Failure", "Failed to remove title")


# Class used by Main window, contains all objects for the gui in two different
# Tabs.
class tab_widget(QtGui.QTabWidget):
    
    
    # Initiate Widget. 
    def __init__(self, parent=MainWindow):
        super(tab_widget, self).__init__()
        self.initUI()
    
    # Create a tab where a book can be removed.
    def makeRemoveBookTab(self, tableGrid):
        self.removeTitle = QLabel("Title to remove")
        self.removeTitle_text = QLineEdit()
        self.removeTitle_text.setPlaceholderText("Title to remove")
        
        tableGrid.addWidget(self.removeTitle,0,0)
        tableGrid.addWidget(self.removeTitle_text,0,1)

        self.remove = QtGui.QPushButton("Remove Title", self)
        self.remove.clicked.connect(self.buttonClicked)
        self.remove.setMaximumWidth(80)

        tableGrid.addWidget(self.remove,1,0)


    # Create all object thats in the AddData tab
    def makeAddDataTab(self, grid):
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
        self.date_text = QtGui.QCalendarWidget()
        self.connect(self.date_text, QtCore.SIGNAL('selectionChanged()'), self.date_changed)

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
        self.submit = QtGui.QPushButton("Submit Data", self)
        self.submit.clicked.connect(self.buttonClicked)
        grid.addWidget(self.submit, 8, 1)
        self.quit = QtGui.QPushButton("Exit", self)
        self.quit.clicked.connect(self.buttonClicked)
        self.quit.setMaximumWidth(80)
        grid.addWidget(self.quit, 8, 0)

    # Creates all the object thats in the getData tab
    def makeGetDataTab(self, vbox_inner):
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
        vbox_inner.addWidget(self.titles_list)
        hbox_inner = QtGui.QHBoxLayout()
        self.getTitleData = QtGui.QPushButton("Get title data", self)
        self.getTitleData.clicked.connect(self.buttonClicked)
        hbox_inner.addWidget(self.getTitleData)
        self.refreshList = QtGui.QPushButton("Refresh list", self)
        self.refreshList.clicked.connect(self.buttonClicked)
        hbox_inner.addWidget(self.refreshList)
        return hbox_inner
    

    

    def date_changed(self):
        self.parent().date = self.date_text.selectedDate()

    # Initiate the widgets gui
    def initUI(self):
        
        vbox_inner = QVBoxLayout()
        grid = QtGui.QGridLayout()
        
        self.genreList = ['Fiction', 'Fact book', 'Poetry']
        
        # Tab 1: Add Data        
        self.makeAddDataTab(grid)
        
        # Tab 2:Get Data from file
        hbox_inner = self.makeGetDataTab(vbox_inner)
        
     
        tab1 = QWidget()
        tab1.setLayout(grid)
        
        vbox_inner.addLayout(hbox_inner)
        tab2 = QWidget()
        tab2.setLayout(vbox_inner)


        # Tab 3: Remove book
        tableGrid = QtGui.QGridLayout()
        table = self.makeRemoveBookTab(tableGrid)
        
        tab3 = QWidget()
        tab3.setLayout(tableGrid)
        

        self.addTab(tab1, "Add Book Data")
        self.addTab(tab2, "Get Book Data")
        self.addTab(tab3, "Remove Book")
        self.move(150, 150)
        self.show()
        
    # Function for when a QListWidgetItem is clicked if the genre_list is 
    # clicked, will check which genre was chosen and populate genre2_list
    # with subgenres of that genre.    
    def item_clicked(self):
        genre = self.sender()
        if genre.objectName() == "genre_list":
            curr = genre.currentItem().text()
            if curr == self.genreList[0]:
                genre2List = ["Detective", "autobiography", "Horror", "Comedy"]
            elif curr == self.genreList[1]:
                genre2List = ["Technology", "Biology", "Chemistry", "Mathematics"]
            elif curr == self.genreList[2]:
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
        if sender.text() == "Refresh list":
            titles = getAllTitles(self.parent())
            self.titles_list.clear()
            if titles != "":
                self.titles_list.addItems(titles)
            else:
                QMessageBox.warning(self.parent(), "Failure", "Connect to the server before refreshing.")
                
        if sender.text() == "Exit":
            if self.parent().dialog.isHidden():
                self.parent().disconnect()
            sys.exit(0)
           
        elif sender.text() == "Get title data":
            if self.titles_list.currentItem() != None:
                titleToGet = str(self.titles_list.currentItem().text())
                    
                if titleToGet != "": 
                    titleData = getTitleData(self.parent(), titleToGet)
                    #splitLine = []
                    #splitLine = titleData.split(",", 7)
                
                    if titleData != "Empty":
                        self.authorData.setText(titleData['author'].replace('"', "").lstrip())
                        self.titleData.setText(titleData['title'].replace('"', "").lstrip())
                        self.genreData.setText(titleData['genre'].replace('"', "").lstrip())
                        self.genre2Data.setText(titleData['genre2'].replace('"', "").lstrip())
                        self.dateData.setText(titleData['dateRead'].replace('"', "").lstrip())
                        self.gradeData.setText(titleData['grade'].replace('"', "").lstrip())
                        self.commentsData.setText(titleData['comments'].replace('"', "").lstrip())
                    else:
                        QMessageBox.warning(self, "Failed", "something went wrong when getting title data")
        elif sender.text() == "Remove Title":
            titleToRemove = self.removeTitle_text.text()
            print "REMOVE "+titleToRemove
            if(titleToRemove != ""):
                removeTitle(str(titleToRemove))



        elif sender.text() == "Submit Data":
            while(True):        
                author = str(self.author_text.text())
                if containsDigit(author) == True:
                    QMessageBox.warning(self, "Wrong input", "Error: Author cant contain digits")
                    break
                  
                if len(author) == 0:
                    QMessageBox.warning(self, "Wrong input", "Author is a required field")
                    break
                   
                title = str(self.title_text.text())
                try:
                    title = str(self.title_text.text())
                except:
                    QMessageBox.warning(self, "Wrong input", "Error in comments, only A-z, 1-9 allowed")
                    break
                
                if len(title) == 0:
                    QMessageBox.warning(self, "Wrong input", "Title is a required field")
                    break
                    
                if self.genre_list.currentItem() != None:
                    genre = str(self.genre_list.currentItem().text())
                else:
                    QMessageBox.warning(self, "Wrong input", "Error: Need to pick genre")
                    break
                                      
                if self.genre2_list.currentItem() != None:
                    genre2 = str(self.genre2_list.currentItem().text())
                else:
                    QMessageBox.warning(self, "Wrong input", "Error: Need to pick genre2")
                    break
                 
                #d = str(self.date.text())  
                
                #if len(d) == 0:
                #    QMessageBox.warning(self, "Wrong input", "Date is a required field")
                #    break
                #try:
                #    dateRead = datetime.datetime.strptime(d, '%y-%m-%d').date() 
                #except:
                #    QMessageBox.warning(self, "Wrong input", "Error: Date needs to be in format: yy-mm-dd")         
                #    break
                print "date"
                print self.parent().date
                d = str(self.parent().date.toString('yy-MM-d'))
                print "d "+d
                
                dateRead = datetime.datetime.strptime(d, '%y-%m-%d').date() 
                
                print "dateRead"
                print dateRead
                if(dateRead == 0):
                    QMessageBox.warning(self, "Wrong input", "Date is a required field")
                    break
                    
                try:
                    grade = int(str(self.grade_text.text()))
                except:
                    QMessageBox.warning(self, "Wrong input", "Error: Grade needs to be a number between 1-5")
                    break
                
                if grade > 5 or grade < 1:
                    QMessageBox.warning(self, "Wrong input", "Error: Grade needs to be a number between 1-5")
                    break
                
                try:
                    comments = str(self.comments_text.text())
                except:
                    QMessageBox.warning(self, "Wrong input", "Error: in comments, only A-z, 1-9 allowed")
                    break
                
                if len(comments) == 0:
                    comments = "No comments"
                
                self.parent().sendBookData(author, title, genre, genre2, str(dateRead), str(grade), comments)
                break

# Main
def main():
    app = QtGui.QApplication(sys.argv)
    gui = MainWindow()

    sys.exit(app.exec_())

if __name__ == '__main__':
    main()
   
