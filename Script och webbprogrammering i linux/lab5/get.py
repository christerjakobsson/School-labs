#!/usr/bin/python
import urllib2
response = urllib2.urlopen('http://www.shinowa.tk/getAllData.php')
html = response.read()
print html
