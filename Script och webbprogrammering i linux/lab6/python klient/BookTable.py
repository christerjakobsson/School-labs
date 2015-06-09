from PyQt4 import QtGui
from PyQt4.QtGui import QTableWidget, QAbstractItemView, QTableWidgetItem, QHeaderView
from BookClass import Book


# Class used to show all the book data from the server.
class BookTable(QTableWidget):
    def __init__(self, data, *args):
        QTableWidget.__init__(self, *args)
        self.setSelectionBehavior(QAbstractItemView.SelectRows)
        self.setSelectionMode(QtGui.QAbstractItemView.SingleSelection)
        self.data = data
        self.set_my_data()
        self.horizontalHeader().setMovable(True)
        self.horizontalHeader().setResizeMode(QHeaderView.Stretch)

    # Set the data
    def set_data(self, data):
        self.data = data

    # Delete all contents in table.
    def delete(self):
        self.clear()

    # Inserts items containing the data to the table.
    def set_my_data(self):
        self.clear()
        hor_headers = []
        table_order = ['Author', 'Title', 'Genre', 'Genre2', 'Date Read', 'Grade', 'Comments']
        for n, key in enumerate(table_order):
            hor_headers.append(key)
            for m, item in enumerate(self.data[key]):
                new_item = QTableWidgetItem(item)
                self.setItem(m, n, new_item)
        self.setHorizontalHeaderLabels(hor_headers)

    # Get the selected item, only one row can be selected at a time.
    def get_selected_item(self):
        items = self.selectedItems()

        if len(items) == 0:
            return None
        elif len(items) == 7:
            book = Book()
            book.author = str(items[0].text())
            book.title = str(items[1].text())
            book.genre = str(items[2].text())
            book.genre2 = str(items[3].text())
            book.date_read = str(items[4].text())
            book.grade = str(items[5].text())
            book.comments = str(items[6].text())
            book.make_strings()
            return book

    # Get the selected rows title.
    def get_selected_title(self):
        items = self.selectedItems()
        if len(items) == 0:
            return ""
        elif len(items) == 7:
            item = self.selectedItems()
            return str(item[1].text())