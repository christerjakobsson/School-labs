from PyQt4 import QtGui, QtCore
import datetime

import PyQt4.QtGui

from BookTable import BookTable
import ClientClass
from BookClass import Book


# Class used by the main window contains the tab view and its objects.
# noinspection PyUnresolvedReferences
class TabWidget(QtGui.QTabWidget):
    # Initiate Widget.
    def __init__(self, parent=None):

        super(TabWidget, self).__init__()
        self.genre2_poetry = ["Tirade", "Ballads", "Rhyme"]
        self.genre2_fact = ["Technology", "Biology", "Chemistry", "Mathematics"]
        self.genre2_fiction = ["Detective", "autobiography", "Horror", "Comedy"]
        self.parent = parent
        self.date = 0
        self.change_genres = ['Fiction', 'Fact Book', 'Poetry']
        self.add_tab_genres = ['Fiction', 'Fact Book', 'Poetry']

        self.change_submit_button = QtGui.QPushButton("Submit Changes")
        self.change_back_button = QtGui.QPushButton("Back To Table")
        self.change_title_button = QtGui.QPushButton("Change selected")
        self.change_grade = PyQt4.QtGui.QLineEdit()
        self.change_date = QtGui.QCalendarWidget()
        self.change_genre2_list = PyQt4.QtGui.QListWidget(self)
        self.change_genre_list = PyQt4.QtGui.QListWidget(self)
        self.change_title = PyQt4.QtGui.QLineEdit()
        self.change_author = PyQt4.QtGui.QLineEdit()
        self.get_tab_comments = PyQt4.QtGui.QLineEdit()
        self.change_date_text = PyQt4.QtGui.QLabel("Date Read")
        self.change_comments_text = PyQt4.QtGui.QLabel("Comments")
        self.change_author_text = PyQt4.QtGui.QLabel("Author")
        self.change_grade_text = PyQt4.QtGui.QLabel("Grade")
        self.change_genre_text = PyQt4.QtGui.QLabel("Genre")
        self.change_title_text = PyQt4.QtGui.QLabel("Title")
        self.change_genre2_text = PyQt4.QtGui.QLabel("Genre2")

        self.get_tab_grade_text = PyQt4.QtGui.QLabel("Grade")
        self.get_tab_comments_text = PyQt4.QtGui.QLabel("Comments")
        self.get_tab_date_text = PyQt4.QtGui.QLabel("Date Read")
        self.get_tab_genre2_text = PyQt4.QtGui.QLabel("Genre2")
        self.get_tab_genre_text = PyQt4.QtGui.QLabel("Genre")
        self.get_tab_title_text = PyQt4.QtGui.QLabel("Title")
        self.get_tab_author_text = PyQt4.QtGui.QLabel("Author")
        self.get_tab_grade = PyQt4.QtGui.QLineEdit()
        self.get_tab_date = PyQt4.QtGui.QLineEdit()
        self.get_tab_genre2 = PyQt4.QtGui.QLineEdit()
        self.get_tab_genre = PyQt4.QtGui.QLineEdit()
        self.get_tab_title = PyQt4.QtGui.QLineEdit()
        self.get_tab_author = PyQt4.QtGui.QLineEdit()
        self.get_tab_refresh_list_button = QtGui.QPushButton("Refresh list", self)
        self.get_tab_get_title_button = QtGui.QPushButton("Get title data", self)
        self.get_tab_titles_list = PyQt4.QtGui.QListWidget(self)
        self.get_tab_all_data_text = QtGui.QLabel("All")
        self.get_tab_all_data = QtGui.QLineEdit()

        self.table_tab_delete_button = QtGui.QPushButton("Delete selected")
        self.table_tab_refresh = QtGui.QPushButton("Refresh")

        self.add_tab_submit_button = QtGui.QPushButton("Submit Data", self)
        self.add_tab_genre2_list = PyQt4.QtGui.QListWidget(self)
        self.add_tab_genre2_text = PyQt4.QtGui.QLabel("Genre2")
        self.add_tab_genre_list = PyQt4.QtGui.QListWidget(self)
        self.add_tab_genre_label = PyQt4.QtGui.QLabel("Genre")
        self.add_tab_comments_text = PyQt4.QtGui.QLineEdit()
        self.add_tab_comments_label = PyQt4.QtGui.QLabel("Comments")
        self.add_tab_grade_text = PyQt4.QtGui.QLineEdit()
        self.add_tab_grade_label = PyQt4.QtGui.QLabel("Grade")
        self.add_tab_date_widget = QtGui.QCalendarWidget()
        self.add_tab_date_label = PyQt4.QtGui.QLabel("Date Read")
        self.add_tab_title_text = PyQt4.QtGui.QLineEdit()
        self.add_tab_title_label = PyQt4.QtGui.QLabel("Title")
        self.add_tab_author_text = PyQt4.QtGui.QLineEdit()
        self.add_tab_author_label = PyQt4.QtGui.QLabel("Author")

        self.init_ui()

    # Create a tab where a book can be removed.
    # noinspection PyUnresolvedReferences
    def make_change_book_tab(self, grid):

        grid_left = QtGui.QGridLayout()
        grid_left.addWidget(self.change_author_text, 0, 0)
        self.change_author.setPlaceholderText("Author")
        grid_left.addWidget(self.change_author, 0, 1)

        grid_left.addWidget(self.change_title_text, 1, 0)
        self.change_title.setPlaceholderText("Title")
        grid_left.addWidget(self.change_title, 1, 1)

        vbox = QtGui.QVBoxLayout()

        vbox.addWidget(self.change_date_text)
        vbox.addWidget(self.change_date)

        grid.addLayout(vbox, 0, 1)

        self.connect(self.change_date, QtCore.SIGNAL('selectionChanged()'), self.date_changed)

        grid_left.addWidget(self.change_grade_text, 2, 0)
        self.change_grade.setPlaceholderText("Grade")
        grid_left.addWidget(self.change_grade, 2, 1)

        grid_left.addWidget(self.change_comments_text, 3, 0)
        self.get_tab_comments = PyQt4.QtGui.QLineEdit()
        self.get_tab_comments.setPlaceholderText("Comments")
        grid_left.addWidget(self.get_tab_comments, 3, 1)

        grid.addLayout(grid_left, 0, 0)

        grid.addWidget(self.change_genre_text, 1, 0)
        self.change_genre_list.addItems(self.change_genres)
        self.add_tab_genre_list.setObjectName('genre_list')
        self.change_genre_list.itemClicked.connect(self.change_item_clicked)
        grid.addWidget(self.change_genre_list, 2, 0)

        grid.addWidget(self.change_genre2_text, 1, 1)
        self.change_genre2_list.setObjectName("genre2")
        self.change_genre2_list.addItem("Select genre first")
        grid.addWidget(self.change_genre2_list, 2, 1)

        self.change_back_button.clicked.connect(self.button_clicked)
        self.change_submit_button.clicked.connect(self.button_clicked)

        grid.addWidget(self.change_back_button, 3, 0)
        grid.addWidget(self.change_submit_button)

    # Create a tab where a table with all the books data is shown, and buttons to change or delete a book,
    # and refresh the list.
    def make_table_tab(self, vbox):
        books_dict = ClientClass.Client.make_books_dict(ClientClass.Client.get_all_books())
        # noinspection PyAttributeOutsideInit
        self.table = BookTable(books_dict, len(books_dict['Author']), 7)
        self.table.show()

        vbox.addWidget(self.table)

        hbox = QtGui.QHBoxLayout()

        self.table_tab_refresh.clicked.connect(self.button_clicked)
        hbox.addWidget(self.table_tab_refresh)

        self.change_title_button.clicked.connect(self.button_clicked)
        hbox.addWidget(self.change_title_button)

        self.table_tab_delete_button.clicked.connect(self.button_clicked)
        hbox.addWidget(self.table_tab_delete_button)

        vbox.addLayout(hbox)

    # Create all object that is in the make_add_data_tab tab
    def make_add_data_tab(self, grid):

        grid_left = QtGui.QGridLayout()

        self.add_tab_author_text.setPlaceholderText("Author")

        grid_left.addWidget(self.add_tab_author_label, 0, 0)
        grid_left.addWidget(self.add_tab_author_text, 0, 1)

        self.add_tab_title_text.setPlaceholderText("Title")

        grid_left.addWidget(self.add_tab_title_label, 1, 0)
        grid_left.addWidget(self.add_tab_title_text, 1, 1)

        self.connect(self.add_tab_date_widget, QtCore.SIGNAL('selectionChanged()'), self.date_changed)

        self.add_tab_grade_text.setPlaceholderText("Grade 1-5")
        grid_left.addWidget(self.add_tab_grade_label, 2, 0)
        grid_left.addWidget(self.add_tab_grade_text, 2, 1)

        self.add_tab_comments_text.setPlaceholderText("Comments here")
        grid_left.addWidget(self.add_tab_comments_label, 3, 0)
        grid_left.addWidget(self.add_tab_comments_text, 3, 1)

        vbox_right = QtGui.QVBoxLayout()
        vbox_right.addWidget(self.add_tab_date_label)
        vbox_right.addWidget(self.add_tab_date_widget)

        grid.addLayout(grid_left, 0, 0)
        grid.addLayout(vbox_right, 0, 1)

        self.add_tab_genre_list.addItems(self.add_tab_genres)
        self.add_tab_genre_list.setObjectName('genre_list')
        self.add_tab_genre_list.itemClicked.connect(self.add_tab_item_clicked)

        grid.addWidget(self.add_tab_genre_label, 1, 0)
        grid.addWidget(self.add_tab_genre_list, 2, 0)

        self.add_tab_genre2_list.setObjectName("genre2")
        self.add_tab_genre2_list.addItem("Select genre first")
        grid.addWidget(self.add_tab_genre2_text, 1, 1)
        grid.addWidget(self.add_tab_genre2_list, 2, 1)

        self.add_tab_submit_button.clicked.connect(self.button_clicked)
        grid.addWidget(self.add_tab_submit_button, 3, 1)

    # Creates all the object that is in the make_get_data_tab tab
    def make_get_data_tab(self, vbox_inner):
        grid = QtGui.QGridLayout()
        grid.addWidget(self.get_tab_author_text, 0, 0)
        self.get_tab_author.setPlaceholderText("Author")
        self.get_tab_author.setReadOnly(True)
        grid.addWidget(self.get_tab_author, 0, 1)

        grid.addWidget(self.get_tab_title_text, 1, 0)
        self.get_tab_title.setPlaceholderText("Title")
        self.get_tab_title.setReadOnly(True)
        grid.addWidget(self.get_tab_title, 1, 1)

        grid.addWidget(self.get_tab_genre_text, 2, 0)
        self.get_tab_genre.setPlaceholderText("Genre")
        self.get_tab_genre.setReadOnly(True)
        grid.addWidget(self.get_tab_genre, 2, 1)

        grid.addWidget(self.get_tab_genre2_text, 3, 0)
        self.get_tab_genre2.setPlaceholderText("Genre2")
        self.get_tab_genre2.setReadOnly(True)
        grid.addWidget(self.get_tab_genre2, 3, 1)

        grid.addWidget(self.get_tab_date_text, 4, 0)
        self.get_tab_date.setPlaceholderText("Date Read")
        self.get_tab_date.setReadOnly(True)
        grid.addWidget(self.get_tab_date, 4, 1)

        grid.addWidget(self.get_tab_grade_text, 5, 0)
        self.get_tab_grade.setPlaceholderText("Grade")
        self.get_tab_grade.setReadOnly(True)
        grid.addWidget(self.get_tab_grade, 5, 1)

        grid.addWidget(self.get_tab_comments_text, 6, 0)
        self.get_tab_comments.setPlaceholderText("Comments")
        self.get_tab_comments.setReadOnly(True)
        grid.addWidget(self.get_tab_comments, 6, 1)

        grid.addWidget(self.get_tab_all_data_text, 7, 0)
        self.get_tab_all_data.setPlaceholderText("Author, Title, Genre, Genre2, Date Read, Grade, Comments")
        self.get_tab_all_data.setReadOnly(True)
        grid.addWidget(self.get_tab_all_data, 7, 1)

        vbox_inner.addLayout(grid)
        vbox_inner.addWidget(self.get_tab_titles_list)
        hbox = QtGui.QHBoxLayout()

        self.get_tab_refresh_list_button.clicked.connect(self.button_clicked)
        hbox.addWidget(self.get_tab_refresh_list_button)
        self.get_tab_get_title_button.clicked.connect(self.button_clicked)
        hbox.addWidget(self.get_tab_get_title_button)

        vbox_inner.addLayout(hbox)

    # Used when the QCalenderWidgets are changed
    def date_changed(self):
        self.date = self.add_tab_date_widget.selectedDate()

    # Initiate the widgets gui
    def init_ui(self):

        grid = QtGui.QGridLayout()
        self.make_add_data_tab(grid)
        tab1 = PyQt4.QtGui.QWidget()
        tab1.setLayout(grid)

        vbox_inner = PyQt4.QtGui.QVBoxLayout()
        self.make_get_data_tab(vbox_inner)
        tab2 = PyQt4.QtGui.QWidget()
        tab2.setLayout(vbox_inner)

        # Tab 3: Remove book
        grid_change = QtGui.QGridLayout()
        self.make_change_book_tab(grid_change)
        tab3 = PyQt4.QtGui.QWidget()
        tab3.setLayout(grid_change)

        table_vbox = QtGui.QVBoxLayout()
        self.make_table_tab(table_vbox)
        tab4 = PyQt4.QtGui.QWidget()
        tab4.setLayout(table_vbox)

        self.addTab(tab4, "Table")
        self.addTab(tab1, "Add Book")
        self.addTab(tab2, "Get Book")
        self.addTab(tab3, "Change Book")
        self.move(150, 150)
        self.show()

    # Function for when a QListWidgetItem is clicked if the add_tab_genre_list is
    # clicked, will check which genre was chosen and populate add_tab_genre2_list
    # with subgenres of that genre.
    def add_tab_item_clicked(self):
        genre = self.sender()
        if genre.objectName() == "genre_list":
            curr = genre.currentItem().text()
            if curr == self.add_tab_genres[0]:
                genre2_list = ["Detective", "autobiography", "Horror", "Comedy"]
            elif curr == self.add_tab_genres[1]:
                genre2_list = ["Technology", "Biology", "Chemistry", "Mathematics"]
            elif curr == self.add_tab_genres[2]:
                genre2_list = ["Tirade", "Ballads", "Rhyme"]
            else:
                genre2_list = ["None"]

            self.add_tab_genre2_list.clear()
            self.add_tab_genre2_list.addItems(genre2_list)

    # Function for when a QListWidgetItem is clicked if the change_genre_list is
    # clicked, will check which genre was chosen and populate change_genre2_list
    # with subgenres of that genre.
    def change_item_clicked(self):
        genre = self.sender()
        if genre.objectName() == "genre_list":
            curr = genre.currentItem().text()
            if curr == self.add_tab_genres[0]:
                genre2_list = self.genre2_fiction
            elif curr == self.add_tab_genres[1]:
                genre2_list = self.genre2_fact
            elif curr == self.add_tab_genres[2]:
                genre2_list = self.genre2_poetry
            else:
                genre2_list = ["None"]

            self.change_genre2_list.clear()
            self.change_genre2_list.addItems(genre2_list)

    # Validates the input from all QLineEdits and verify's that they are correct format, if one is not a popup will
    # warn the user which field is incorrect and break return False. Else if all fields are ok returns True.
    def validate_fields(self, book):

        book.author = str(book.author)
        if self.contains_digit(book.author):
            ClientClass.Client.show_popup("Author cant contain digits", "", "Invalid author",
                                          PyQt4.QtGui.QMessageBox.Warning)
            return False

        if len(book.author) == 0:
            ClientClass.Client.show_popup("Author is required", "", "Invalid author", PyQt4.QtGui.QMessageBox.Warning)
            return False

        try:
            str(book.title)
        except (UnicodeEncodeError, NameError):
            ClientClass.Client.show_popup("Error in comments, only A-z, 1-9 allowed", "", "Invalid title",
                                          PyQt4.QtGui.QMessageBox.Warning)
            return False

        if len(book.title) == 0:
            ClientClass.Client.show_popup("Title is a required field", "", "Invalid title",
                                          PyQt4.QtGui.QMessageBox.Warning)
            return False

        if len(book.genre) is not 0:
            str(book.genre)
        else:
            ClientClass.Client.show_popup("Genre is required", "", "Required field", PyQt4.QtGui.QMessageBox.Warning)
            return False

        if len(book.genre2) is not 0:
            str(book.genre2)
        else:
            ClientClass.Client.show_popup("Genre2 is required", "", "Required field", PyQt4.QtGui.QMessageBox.Warning)
            return False

        try:
            d = str(book.date_read)
            book.date_read = datetime.datetime.strptime(d, '%y-%m-%d').date()
        except (AttributeError, ValueError):
            ClientClass.Client.show_popup("Date is required", "", "Date required", PyQt4.QtGui.QMessageBox.Warning)
            return False

        if book.date_read == 0:
            ClientClass.Client.show_popup("Date is required", "", "Date required", PyQt4.QtGui.QMessageBox.Warning)
            return False

        try:
            book.grade = int(str(book.grade))
        except ValueError:
            ClientClass.Client.show_popup("Grade out of range", "Grade needs to be a number between 1-5",
                                          "Invalid grade", PyQt4.QtGui.QMessageBox.Warning)
            return False

        if book.grade > 5 or book.grade < 1:
            ClientClass.Client.show_popup("Grade out of range", "Grade needs to be a number between 1-5",
                                          "Invalid grade", PyQt4.QtGui.QMessageBox.Warning)
            return False

        try:
            str(book.comments)
        except (UnicodeEncodeError, NameError):
            ClientClass.Client.show_popup("Invalid format of comments", "Only A-z 1-9 allowed", "Invalid comment",
                                          PyQt4.QtGui.QMessageBox.Warning)
            return False
        if len(book.comments) == 0:
            book.comments = "No comments"

        return True

    # checks if a string contains digits
    @staticmethod
    def contains_digit(string):
        has_digits = False
        for x in string:
            if x.isdigit():
                has_digits = True
                break
        return has_digits

    # Checks if new_book is changed from real_book
    @staticmethod
    def values_has_changed(new_book, real_book):
        if new_book.author != real_book.author:
            return True
        elif new_book.title != real_book.title:
            return True
        elif new_book.genre != real_book.genre:
            return True
        elif new_book.genre2 != real_book.genre2:
            return True
        elif new_book.date_read != real_book.date_read:
            return True
        elif new_book.grade != real_book.grade:
            return True
        elif new_book.comments != real_book.comments:
            return True
        else:
            return False

    # Makes in_list's item t to selected.
    @staticmethod
    def set_list_item_selected(in_list, t):
        list_length = in_list.count()
        iterator = 0
        index = 0
        while iterator < list_length:
            if t == in_list.item(iterator).text():
                index = iterator
                break
            iterator += 1

        in_list.setCurrentRow(index)

    # Function for when a button is clicked
    def change_selected(self):

        # noinspection PyAttributeOutsideInit
        self.book = self.table.get_selected_item()
        if self.book is not None:
            self.setCurrentIndex(3)
            self.set_list_item_selected(self.change_genre_list, self.book.genre)
            # TODO
            curr = self.change_genre_list.currentItem().text()
            if curr == self.add_tab_genres[0]:
                genre2_list = self.genre2_fiction
            elif curr == self.add_tab_genres[1]:
                genre2_list = self.genre2_fact
            elif curr == self.add_tab_genres[2]:
                genre2_list = self.genre2_poetry
            else:
                genre2_list = ["None"]

            self.change_genre2_list.clear()
            self.change_genre2_list.addItems(genre2_list)
            self.set_list_item_selected(self.change_genre2_list, self.book.genre2)
            self.change_author.setText(self.book.author)
            self.change_title.setText(self.book.title)
            date = self.book.date_read.split("-")
            date_list = QtCore.QDate()
            date_list.setDate(int(date[0]), int(date[1]), int(date[2]))
            self.change_date.setSelectedDate(date_list)
            self.change_grade.setText(self.book.grade)
            self.get_tab_comments.setText(self.book.comments)

    # Called when the Delete Selected button is pressed, changes tab to the change tab and puts the books
    # data in fields for the user to manipulate.
    def delete_selected(self):
        title_to_remove = self.table.get_selected_title()
        if title_to_remove != "":
            # noinspection PyAttributeOutsideInit
            self.mess = QtGui.QMessageBox()
            self.mess.setText("Delete title: " + title_to_remove)
            self.mess.setInformativeText("Are you sure?")
            self.mess.addButton(QtGui.QPushButton("Yes"), PyQt4.QtGui.QMessageBox.YesRole)
            self.mess.addButton(QtGui.QPushButton("No"), PyQt4.QtGui.QMessageBox.NoRole)
            self.mess.setIcon(PyQt4.QtGui.QMessageBox.Question)
            self.mess.setWindowTitle("Remove book")
            ret = self.mess.exec_()
            if ret == 0 and ClientClass.Client.remove_title(title_to_remove):
                self.table_tab_refresh.animateClick()

    # Submits changes if all fields is valid.
    def submit_changes(self):
        book = Book()
        try:
            book.author = self.change_author.text()
            book.title = self.change_title.text()
            book.genre = self.change_genre_list.currentItem().text()
            book.genre2 = self.change_genre2_list.currentItem().text()
            book.date_read = self.change_date.selectedDate().toString('yy-MM-d')
            book.grade = self.change_grade.text()
            book.comments = self.get_tab_comments.text()

            if self.values_has_changed(book, self.book) and self.validate_fields(book):
                ClientClass.Client.submit_changes(book)
            else:
                ClientClass.Client.show_popup("Fields not changed", "", "Error", PyQt4.QtGui.QMessageBox.Warning)

        except AttributeError:
            ClientClass.Client.show_popup("Get book from table tab",
                                          "Go to the table tab and choose a book to change"
                                          "it's data ", "Error", PyQt4.QtGui.QMessageBox.Warning)

    # Submit book if all fields are valid.
    def submit_book(self):
        genre_item = self.add_tab_genre_list.currentItem()
        genre2_item = self.add_tab_genre2_list.currentItem()
        if (genre_item is not None) and (genre2_item is not None) and type(self.date) != int:
            book = Book()
            book.author = self.add_tab_author_text.text()

            book.title = self.add_tab_title_text.text()

            book.genre = str(self.add_tab_genre_list.currentItem().text())

            book.genre2 = str(self.add_tab_genre2_list.currentItem().text())

            book.date_read = self.date.toString('yy-MM-d')

            book.grade = self.add_tab_grade_text.text()

            book.comments = self.add_tab_comments_text.text()

            if self.validate_fields(book):
                ClientClass.Client.add_book(book)

        elif type(self.date) is int:
            ClientClass.Client.show_popup("Date is required", "", "Required field",
                                          PyQt4.QtGui.QMessageBox.Warning)
        elif self.add_tab_genre_list.currentItem() is None:
            ClientClass.Client.show_popup("Genre is required", "", "Required field",
                                          PyQt4.QtGui.QMessageBox.Warning)
        elif self.add_tab_genre2_list.currentItem() is None:
            ClientClass.Client.show_popup("Genre2 is required", "", "Required field",
                                          PyQt4.QtGui.QMessageBox.Warning)

    # Called when a button is clicked, checks which button is pressed and executes actions depending on which button.
    def button_clicked(self):
        sender = self.sender()

        if sender.text() == "Refresh list":
            titles = ClientClass.Client.get_all_titles()
            self.get_tab_titles_list.clear()
            if titles != "":
                self.get_tab_titles_list.addItems(titles)
        elif sender.text() == "Change selected":
            self.change_selected()

        elif sender.text() == "Refresh":
            books_dict = ClientClass.Client.make_books_dict(ClientClass.Client.get_all_books())
            self.table.set_data(books_dict)
            self.table.delete()
            self.table.setRowCount(len(books_dict['Author']))
            self.table.set_my_data()
        elif sender.text() == "Delete selected":
            self.delete_selected()

        elif sender.text() == "Get title data":
            if self.get_tab_titles_list.currentItem() is not None:
                title_to_get = str(self.get_tab_titles_list.currentItem().text())

                if title_to_get is not None:
                    title_data = ClientClass.Client.get_title(title_to_get)

                    self.get_tab_author.setText(title_data.author)
                    self.get_tab_title.setText(title_data.title)
                    self.get_tab_genre.setText(title_data.genre)
                    self.get_tab_genre2.setText(title_data.genre2)
                    self.get_tab_date.setText(title_data.date_read)
                    self.get_tab_grade.setText(title_data.grade)
                    self.get_tab_comments.setText(title_data.comments)

                    self.get_tab_all_data.setText(
                        title_data.author + ", " + title_data.title + ", " + title_data.genre + ", " +
                        title_data.genre2 + ", " + title_data.date_read + ", " + title_data.grade + ", "
                        + title_data.comments)

        elif sender.text() == "Remove Title":
            title_to_remove = self.removeTitle_text.text()

            if title_to_remove != "":
                ClientClass.Client.remove_title(str(title_to_remove))
        elif sender.text() == "Back To Table":
            self.setCurrentIndex(0)
        elif sender.text() == "Submit Changes":
            self.submit_changes()

        elif sender.text() == "Submit Data":
            self.submit_book()
