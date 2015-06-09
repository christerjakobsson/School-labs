cmake_minimum_required(VERSION 2.8.4)
project(lab5)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
    linkedlist.c
    linkedlist.h
    makefile
    mfind_p.c)

add_executable(lab5 ${SOURCE_FILES})