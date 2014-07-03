NucleotideFinder
================

Nucleotide Finder uses the GRAL graphing library to display concentrations of nucleotides across a fasta file.

When given a fasta file, the program will count the number of <nucleotide> in groups of <chunk>. 

E.g. 

AAAAAGGGGTTTCC : Chunk = 4
Counting Gs
Chunk 1: AAAA : 0
Chunk 2: AGGG : 3
Chunk 3: GTTT : 1
Chunk 4: CC   : 0

Peaks in the data (chunks that are a single letter), can suggest the locations of certain motifs. 

This data is displayed in graphs 200 nucleotides at a time. 
