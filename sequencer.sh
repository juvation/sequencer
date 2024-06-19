#!/bin/bash

g++ --std=c++11 -I../mrp/midicl sequencer.cpp -framework CoreMIDI -framework CoreFoundation -L ../mrp/midicl/lib/ -lmidicl -o sequencer


