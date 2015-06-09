cmake_minimum_required(VERSION 2.8.4)
project(parallellProg)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
    lab1/code/linkedlist.c
    lab1/code/linkedlist.h
    lab1/code/test.c
    lab1/code/thread-pool.c
    lab1/code/thread-pool.h
    lab1/archive.sh)

add_executable(parallellProg ${SOURCE_FILES})