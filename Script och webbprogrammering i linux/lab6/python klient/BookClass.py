

# Class that is data holder for a book,
# used to easily store book data.
class Book:
    def __init__(self):
        self.author = None
        self.title = None
        self.genre = None
        self.genre2 = None
        self.grade = None
        self.date_read = None
        self.comments = None

    # Make strings the values in each item.
    def make_strings(self):
        self.title = str(self.title)
        self.author = str(self.author)
        self.genre = str(self.genre)
        self.genre2 = str(self.genre2)
        self.date_read = str(self.date_read)
        self.grade = str(self.grade)
        self.comments = str(self.comments)

    # Set all values in one method
    def set_values(self, a, t, g, g2, d, gr, c):
        self.author = a
        self.title = t
        self.genre = g
        self.genre2 = g2
        self.date_read = d
        self.grade = gr
        self.comments = c

    # Overridden string method, if a books is being printed this is the format it will be in.
    def __str__(self):
        return "Author: %s\n" \
               "Title: %s\n" \
               "Genre: %s\n" \
               "Genre2: %s\n" \
               "Date Read: %s\n" \
               "Grade: %s\nComments: %s\n" \
               % (self.author, self.title, self.genre, self.genre2, self.date_read, self.grade, self.comments)