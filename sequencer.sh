#!/bin/bash

g++ --std=c++11 -Imidicl sequencer.cpp -framework CoreMIDI -framework CoreFoundation -L midicl/lib/ -lmidicl -o sequencer


