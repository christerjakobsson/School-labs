cmake_minimum_required(VERSION 2.8.4)
project(lab1)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
    code/queue.c
    code/queue.h
    code/test.c
    code/thread-pool.c
    code/thread-pool.h)

add_executable(lab1 ${SOURCE_FILES})