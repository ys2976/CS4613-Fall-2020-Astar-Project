# CS4613-Fall-2020-Astar-Project
## Project Description
Implement the A* algorithm with graph search for solving the 11-puzzle 
problem as described below. Use sum of Manhattan distances of tiles from their goal positions as 
heuristic function. [Reminder: graph search does not allow repeated states.]
11-puzzle problem: On a 3 x 4 board there are 11 tiles numbered from 1 to 11 and a blank 
position. A tile can slide into the blank position if it is horizontally or vertically adjacent to the 
blank position. Given a start board configuration and a goal board configuration, find a move
sequence with a minimum number of moves to reach the goal configuration from the start 
configuration. (Note: the 3 x 4 board has 3 rows and 4 columns.)

## Input and ouput format
Your program will read in the initial and goal states from a text
file that contains 7 lines as shown in Figure 1 below. Lines 1 to 3 contain the tile pattern for the 
initial state and lines 5 to 7 contain the tile pattern for the goal state. Line 4 is a blank line. n and 
m are integers that range from 0 to 11. Integer 0 represents the blank position and integers 1 to 11
represent tile numbers. Your program will produce an output text file that contains 12 lines as
shown in Figure 2 below. Lines 1 to 3 and lines 5 to 7 contain the tile patterns for the initial and 
goal states as given in the input file. Lines 4 and 8 are blank lines. Line 9 is the depth level d of
the shallowest goal node as found by your search algorithm (assume the root node is at level 0.) 
Line 10 is the total number of nodes N generated in your tree (including the root node.) Line 11
contains the solution that you have found. The solution is a sequence of actions (from root node 
to goal node) represented by the A’s in line 11, separated by blanks. Each A is a character from 
the set {L, R, U, D}, representing the left, right, up and down movements of the blank position. 
Line 12 contains the f(n) values of the nodes along the solution path from the root node to the 
goal node, separated by blanks. There should be d number of A values in line 11 and d+1 number 
of f values in line 12.
