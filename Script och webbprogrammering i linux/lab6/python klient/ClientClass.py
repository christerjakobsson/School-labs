#!/usr/bin/python
# -*- coding: utf-8 -*-

# Imports
from PyQt4 import QtGui
import sys
import json

from PyQt4.QtGui import QMainWindow, QDesktopWidget
from PyQt4.Qt import QMessageBox
import requests

from BookClass import Book
import TabClass


# Class that is the main window in the gui.
class Client(QMainWindow):
    # Initiate Window
    def __init__(self, parent=None):

        super(Client, self).__init__(parent)
        self.tab_widget = TabClass.TabWidget(self)

        self.setWindowTitle('Book Library')
        self.setCentralWidget(self.tab_widget)
        dw = QDesktopWidget()

        x = dw.width() * 0.7
        y = dw.height() * 0.7
        self.resize(x, y)
        self.make_menu()

        self.show()

    # Sends a http request to the server to get all the data from the table, format it list of books and return it.
    @staticmethod
    def get_all_books():
        data = json.dumps({'command': 'get_whole_list'})

        r = requests.post('http://shinowa.tk/lab6/BookHandler.php', data)
        if r.status_code == 202:
            book_list = []
            try:
                for book in r.json():
                    book_data = Book()
                    for attribute, value in book.iteritems():
                        if attribute == "author":
                            book_data.author = value
                        elif attribute == "title":
                            book_data.title = value
                        elif attribute == "genre":
                            book_data.genre = value
                        elif attribute == "genre2":
                            book_data.genre2 = value
                        elif attribute == "dateRead":
                            book_data.date_read = value
                        elif attribute == "grade":
                            book_data.grade = value
                        elif attribute == "comments":
                            book_data.comments = value

                    book_list.append(book_data)
                return book_list
            except ValueError:
                return None
        else:
            Client.show_popup("Could not connect to server", r.text, "Failure", QMessageBox.Warning)
            return None

    # Make a dict with a list for each value in a Book.
    @staticmethod
    def make_books_dict(books):
        author_list = []
        title_list = []
        genre_list = []
        genre2_list = []
        date_list = []
        grade_list = []
        comments_list = []

        if books is not None:

            for book_data in books:
                author_list.append(book_data.author)
                title_list.append(book_data.title)
                genre_list.append(book_data.genre)
                genre2_list.append(book_data.genre2)
                date_list.append(book_data.date_read)
                grade_list.append(book_data.grade)
                comments_list.append(book_data.comments)

            return {'Author': author_list, 'Title': title_list, 'Genre': genre_list, 'Genre2': genre2_list,
                    'Date Read': date_list, 'Grade': grade_list, 'Comments': comments_list}
        else:
            return {'Author': author_list, 'Title': title_list, 'Genre': genre_list, 'Genre2': genre2_list,
                    'Date Read': date_list, 'Grade': grade_list, 'Comments': comments_list}
    # Create the menu for the main window.
    # noinspection PyUnresolvedReferences
    def make_menu(self):
        menu_bar = self.menuBar()
        file_menu = menu_bar.addMenu('&File')
        exit_action = QtGui.QAction(QtGui.QIcon('exit.png'), '&Exit', self)
        exit_action.setShortcut('Ctrl+Q')
        exit_action.setStatusTip('Exit application')
        exit_action.triggered.connect(self.exit_action)
        file_menu.addAction(exit_action)

        help_menu = menu_bar.addMenu('&Help')
        help_action = QtGui.QAction(QtGui.QIcon('hell.png'), '&Help', self)
        help_action.triggered.connect(self.help)
        help_menu.addAction(help_action)

    # Exit action
    @staticmethod
    def exit_action():
        sys.exit(0)

    # Help action
    @staticmethod
    def help():
        help_window = QtGui.QMessageBox()
        help_window.setText(
            "This is a program made for the course \"Script och webbprogrammering\" at Umea University.\n\n"
            "The Program is used to keep track of information about books a person has read,"
            "it communicates with a webbserver that stores all data in a MySql database table which is "
            "retrieved by using the http protocol to send requests. \n\nCopyright Christer Jakobsson 2015;"
            " All Rights Reserved.")
        help_window.addButton(QtGui.QPushButton("Ok"), QMessageBox.YesRole)
        help_window.setIcon(QMessageBox.Information)
        help_window.setWindowTitle("Help")
        help_window.exec_()

    # Send BookData to server.
    @staticmethod
    def add_book(book):
        book.make_strings()
        request = json.dumps({'command': 'add_book',
                              'title': book.title,
                              'author': book.author,
                              'genre': book.genre,
                              'genre2': book.genre2,
                              'dateRead': book.date_read,
                              'grade': book.grade,
                              'comments': book.comments})

        r = requests.post('http://shinowa.tk/lab6/BookHandler.php', request)
        if r.status_code == 201:
            Client.show_popup("Data added successfully", "", "Success", QMessageBox.Information)
        else:
            Client.show_popup("Failed to add data", r.text, "Failure", QMessageBox.Warning)

    # Moves the window to the center of the screen
    def center_on_screen(self):
        resolution = QtGui.QDesktopWidget().screenGeometry()
        self.move((resolution.width() / 2) - (self.frameSize().width() / 2),
                  (resolution.height() / 2) - (self.frameSize().height() / 2))

    # Event when user clicks the windows cross.
    def closeEvent(self, event):
        sys.exit(0)

    # Get a titles data from the server.
    @staticmethod
    def get_title(title_to_get):
        request = json.dumps({'command': 'get_title', 'title': title_to_get})

        r = requests.post('http://shinowa.tk/lab6/BookHandler.php', request)
        print r.json()
        if r.status_code == 404:
            Client.show_popup("Failed getting title's data", r.text, "Failed", QMessageBox.Warning)
            return None
        elif r.status_code == 200:
            book = Book()
            items = r.json()[0]
            book.author = items['author']
            book.title = items['title']
            book.genre = items['genre']
            book.genre2 = items['genre2']
            book.date_read = items['dateRead']
            book.grade = items['grade']
            book.comments = items['comments']
            return book
        else:
            Client.show_popup("Could not connect to server", "", "Failure", QMessageBox.Warning)

    # Shows a QMessageBox with the specified parameters
    @staticmethod
    def show_popup(set_text, detailed, window_title, message_type):
        mess = QMessageBox()
        mess.setText(set_text)
        if len(detailed) > 0:
            mess.setInformativeText("For more information click \"Show Details\"")
        mess.setDetailedText(detailed)
        mess.setIcon(message_type)
        mess.setWindowTitle(window_title)
        mess.setMinimumWidth(500)
        mess.exec_()

    # Function to get all titles data from the file from the server
    @staticmethod
    def get_all_titles():
        request = json.dumps({'command': 'get_list'})
        r = requests.post('http://shinowa.tk/lab6/BookHandler.php', request)
        print r.json()
        if r.status_code == 404:
            Client.show_popup("Failed to get titles", r.text, "Error", QMessageBox.Warning)
            return ""
        elif r.status_code == 202:
            output = r.json()

            title_list = []

            for x in output:
                title_list.append(x['title'])

            return title_list
        else:
            Client.show_popup("Could not connect to server", "", "Failure", QMessageBox.Warning)

    # Sends a http request to remove a book
    @staticmethod
    def remove_title(title_to_remove):
        request = json.dumps({'command': 'remove_book',
                              'titleToRemove': title_to_remove})

        r = requests.post('http://shinowa.tk/lab6/BookHandler.php', request)

        if r.status_code == 200:
            Client.show_popup("Title removed successfully", "", "Success", QMessageBox.Information)
            return True
        elif r.status_code == 404:
            Client.show_popup("Failed to remove title", r.text, "Failure", QMessageBox.Warning)
            return False
        elif r.status_code == 400:
            Client.show_popup("Failed to remove title", r.text, "Failure", QMessageBox.Warning)
            return False
        elif r.status_code == 204:
            Client.show_popup("Failed to remove title", r.text, "Failure", QMessageBox.Warning)
            return False

    # Sends a http request to update a books data
    @staticmethod
    def submit_changes(book):
        book.make_strings()
        request = json.dumps({'command': 'update_book',
                              'title': book.title,
                              'author': book.author,
                              'genre': book.genre,
                              'genre2': book.genre2,
                              'dateRead': book.date_read,
                              'grade': book.grade,
                              'comments': book.comments})

        r = requests.post('http://shinowa.tk/lab6/BookHandler.php', request)
        print r.status_code
        if r.status_code == 202:
            Client.show_popup("Change was successful", "", "Success", QMessageBox.Information)
            return True
        elif r.status_code == 404:
            Client.show_popup("Failed to change book data", r.text, "Failure", QMessageBox.Warning)
            return False
        elif r.status_code == 400:
            Client.show_popup("Failed to change book data", r.text, "Failure", QMessageBox.Warning)
            return False
        else:
            Client.show_popup("Could not connect to server", "", "Failure", QMessageBox.Warning)
            return False


# Main
def main():
    app = QtGui.QApplication(sys.argv)
    Client()

    sys.exit(app.exec_())


if __name__ == '__main__':
    main()
