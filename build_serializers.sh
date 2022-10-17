#!/bin/bash

# deliberate order as generators seem to be using some ancient pyyaml

cd catbuffer-generators
pip install -r requirements.txt
cd ..

cd catbuffer-parser
pip install -r requirements.txt
cd ..

cd catbuffer-generators

PYTHONPATH=.  python3 ../catbuffer-parser/main.py \
	--schema ../catbuffer-schemas/schemas/all.cats \
	--include ../catbuffer-schemas/schemas/ \
	--generator java \
	--copyright HEADER.inc
