#!/usr/bin/python
# -*- coding: utf-8 -*-

# Imports
from PyQt4.QtGui import QMainWindow, QDialog, QFormLayout, QLabel, QLineEdit, \
    QDialogButtonBox, QPushButton, QVBoxLayout
from PyQt4 import QtGui
from PyQt4.Qt import QListWidget, Qt, QMessageBox, QWidget
import sys
import datetime
import socket

# Global socket
global sock
sock = None

 
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
        connectAction = QtGui.QAction('&Connect', self)
        connectAction.setShortcut('Ctrl+C')
        connectAction.setStatusTip('Connect to server')
        connectAction.triggered.connect(self.connect)
        disconnectAction = QtGui.QAction('&Disconnect', self)
        disconnectAction.setShortcut('Ctrl+D')
        disconnectAction.setStatusTip('Disconnect to server')
        disconnectAction.triggered.connect(self.disconnect)
        exitAction = QtGui.QAction(QtGui.QIcon('exit.png'), '&Exit', self)
        exitAction.setShortcut('Ctrl+Q')
        exitAction.setStatusTip('Exit application')
        exitAction.triggered.connect(self.rejected)
        fileMenu.addAction(connectAction)
        fileMenu.addAction(disconnectAction)
        fileMenu.addAction(exitAction)

    # Creates a window that is used by the user to input which server 
    # to connect to.
    def makeConnectWindow(self):
        
        self.dialog = QDialog()
        self.dialog.setWindowTitle('Connect to server')
        self.qform = QFormLayout(self.dialog)
        self.qform.addRow(QLabel("Connect (Blank for localhost)"))
        self.list = QListWidget()
        self.ipLabel = QLabel('IP')
        self.ip = QLineEdit(self.dialog)
        self.portLabel = QLabel('Port')
        self.port = QLineEdit(self.dialog)
        self.qform.addRow(self.ipLabel, self.ip)
        self.qform.addRow(self.portLabel, self.port)
        self.buttonBox = QDialogButtonBox(Qt.Horizontal)
        self.connectButton = QPushButton("&Connect")
        self.connectButton.setDefault(True)
        self.cancelButton = QPushButton("&Cancel")
        self.cancelButton.setCheckable(True)
        self.cancelButton.setAutoDefault(False)
        self.buttonBox.addButton(self.connectButton, QDialogButtonBox.AcceptRole)
        self.buttonBox.addButton(self.cancelButton, QDialogButtonBox.RejectRole)
        self.buttonBox.accepted.connect(self.accepted)
        self.buttonBox.rejected.connect(self.rejected)
        self.qform.addRow(self.buttonBox)
    
    # Initiate Window
    def __init__(self, parent=None):
        
        super(MainWindow, self).__init__(parent)
        self.tab_widget = tab_widget(self)
        self.setWindowTitle('Book Library (Disconnected)')
        self.setCentralWidget(self.tab_widget)
        self.centerOnScreen()
        
        self.makeMenu()
        
        self.makeConnectWindow()
        
        self.dialog.show()        
        self.show()
 
    # Connect to server
    def connect(self, HOST, PORT):        
        global sock
        try:
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.connect((HOST,PORT))
            sock.settimeout(10)
            self.setWindowTitle("Book Library (Connected)")
            self.dialog.hide()
        except:
            self.dialog.show()
            self.setWindowTitle("Book Library (Disconnected)")
            QMessageBox.warning(self, "Failure", "Connection failed")
       
    # Function for when QDialog windows accepted button is pressed, used 
    # gets the ip and port input and connects to that server
    def accepted(self):
 
        ip = str(self.ip.text())
        port = str(self.port.text())
        if len(ip.lstrip()) > 0 and len(port.lstrip()) > 0:
            try:
                HOST = ip.lstrip()
                PORT = int(port.lstrip())
            except:
                QMessageBox.warning(self, "Failure", "Failed connecting to server")
                HOST = ''  # Symbolic name meaning the local host    
                PORT = 24069  # Arbitrary non-privileged port
        else:
            HOST = ''  # Symbolic name meaning the local host    
            PORT = 24069  # Arbitrary non-privileged port
        print HOST
        print PORT
        self.connect(HOST, PORT)
    
    # Function used when the user clicks on the cancel button in the 
    # QDialog window.
    def rejected(self):
        if self.dialog.isHidden():
            self.disconnect()
        sys.exit(0)
        

    # Disconnect from the server
    def disconnect(self):
        global sock
        
        try:
            sock.send("EXIT")
            data = sock.recv(1024)
            if data == "200 OK":
                sock.close()
                self.setWindowTitle('Book Library (Disconnected)')
                self.dialog.show()
        except:
            self.dialog.hide()
            QMessageBox.warning(self, "Error", "Failed to disconnect from server")
        
    
    # Send BookData to server.
    def sendBookData(self, author, title, genre, genre2, dateRead, grade, comments):
        if self.dialog.isHidden():
            global sock
            try:
                sock.send('SEND_ROW_DATA "' + author + '", "' + title + '", "' + genre + '", "' + genre2 + 
                     '", ' + dateRead + ', ' + grade + ', "' + comments + '"\n')
                data = sock.recv(1024)
                if data == '200 OK':
                    dialog = QMessageBox()
                    dialog.setText("Data added successfully")
                    dialog.exec_()
            except socket.timeout:
                dialog = QMessageBox()
                dialog.set
                dialog.setText("Connection timed out...")
                dialog.exec_()
            except:
                self.parent().setWindowTitle("Book Library (Disconnected)")
                dialog = QMessageBox()
                dialog.set
                dialog.setText("Connect to server to be able to send data.")
                dialog.exec_()
        else:
            dialog = QMessageBox()
            dialog.set
            dialog.setText("Connect to server to be able to send data.")
            dialog.exec_()
   
    
    # Moves the window to the center of the screen
    def centerOnScreen (self):  
        resolution = QtGui.QDesktopWidget().screenGeometry()
        self.move((resolution.width() / 2) - (self.frameSize().width() / 2),
                  (resolution.height() / 2) - (self.frameSize().height() / 2))

    # Event when user clicks the windows cross.
    def closeEvent(self, event):
        if self.dialog.isHidden():
            self.disconnect()
        sys.exit(0)        
            

# Get a titles BookData from the server.
def getTitleData(self, titleToGet):
    global sock
    try:
        command = "GET_TITLE_DATA "+titleToGet
        sock.send(command)
        data = sock.recv(1024)
        token = data.split(' ', 1)
        return token[1]
    except socket.timeout:
        QMessageBox.warning(self.parent(), "Failure", "Connection timed out...")
    except:
        QMessageBox.warning(self.parent(), "Failure", "Connect to the server before refreshing.")
        self.setWindowTitle("Book Library (Disconnected)")
        
# Function to get all the data from the file from the server
def getAllTitles(self):
    global sock  
    try:
        sock.send("GET_ALL_DATA")
        data = sock.recv(1024)
        tokens = data.split(':')
        print tokens
        if tokens[0] != "Empty":
            titles = []
            for line in tokens:
                splitLine = line.split(",", 2)
                titles.append(splitLine[1].replace('"', "").lstrip())
            return titles
        else:
            return [""]
    except socket.timeout:
        QMessageBox.warning(self.parent(), "Failure", "Connection timed out...")
    except:
        QMessageBox.warning(self.parent(), "Failure", "Connect to the server before refreshing.")
        self.parent().setWindowTitle("Book Library (Disconnected)")


# Class used by Main window, contains all objects for the gui in two different
# Tabs.
class tab_widget(QtGui.QTabWidget):
    
    
    # Initiate Widget. 
    def __init__(self, parent=MainWindow):
        super(tab_widget, self).__init__()
        self.initUI()
    
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
    
        self.addTab(tab1, "Add Book Data")
        self.addTab(tab2, "Get Book Data")
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
            if self.parent().dialog.isHidden():
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
                if self.parent().dialog.isHidden():
                    titleToGet = str(self.titles_list.currentItem().text())
                    
                    if titleToGet != "": 
                        titleData = getTitleData(self.parent(), titleToGet)
                        splitLine = []
                        splitLine = titleData.split(",", 7)
                    
                        if titleData != "Empty":
                            self.authorData.setText(splitLine[0].replace('"', "").lstrip())
                            self.titleData.setText(splitLine[1].replace('"', "").lstrip())
                            self.genreData.setText(splitLine[2].replace('"', "").lstrip())
                            self.genre2Data.setText(splitLine[3].replace('"', "").lstrip())
                            self.dateData.setText(splitLine[4].replace('"', "").lstrip())
                            self.gradeData.setText(splitLine[5].replace('"', "").lstrip())
                            self.commentsData.setText(splitLine[6].replace('"', "").lstrip())
                        else:
                            QMessageBox.warning(self, "Failed", "Connect to server first")
                            self.dialog.setWindowTitle("Book Library (Disconnected)")
                    
        elif sender.text() == "Submit Data":
            
            while self.parent().dialog.isHidden():
                    
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
                
                d = str(self.date_text.text())  
                
                if len(d) == 0:
                    QMessageBox.warning(self, "Wrong input", "Date is a required field")
                    break
                try:
                    dateRead = datetime.datetime.strptime(d, '%y-%m-%d').date() 
                except:
                    QMessageBox.warning(self, "Wrong input", "Error: Date needs to be in format: yy-mm-dd")         
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
   
