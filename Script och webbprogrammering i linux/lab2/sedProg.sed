#!/bin/sed -f
/:$/ {
h
s,\.:,,
s,[^/:]*[/:], ,g
x
}
G
s:\(.*\)\n\(.*\):\2\1:
