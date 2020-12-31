package com.bananatofu.astar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Neal SHU
 */

public final class Program {
    /**
     * initial state
     */
    static ArrayList<ArrayList<Integer>> initialState = new ArrayList<>();
    /**
     * goal state
     */
    static ArrayList<ArrayList<Integer>> goalState = new ArrayList<>();
    /**
     * HashMap of states and com.bananatofu.astar.TreeNode used to check state repetition
     */
    static HashMap<ArrayList<ArrayList<Integer>>, TreeNode> checkDup = new HashMap<>(32);
    /**
     * com.bananatofu.astar.TreeNode counter
     */
    static Integer nodeCount = 0;
    /**
     * number of rows in the game board
     */
    public static final int ROWS = 3;
    /**
     * number of columns in the game board
     */
    public static final int COLS = 4;
    /**
     * top row number
     */
    public static final int TOP_ROW = 0;
    /**
     * bottom row number
     */
    public static final int BOTTOM_ROW = 2;
    /**
     * leftmost column number
     */
    public static final int LEFTMOST_COL = 0;
    /**
     * rightmost column number
     */
    public static final int RIGHTMOST_COL = 3;
    /**
     * move up
     */
    public static final String UP = "U";
    /**
     * move down
     */
    public static final String DOWN = "D";
    /**
     * move left
     */
    public static final String LEFT = "L";
    /**
     * move right
     */
    public static final String RIGHT = "R";
    /**
     * directory of the folder that contains all input files
     */
    public static String dir = "D:\\AStar\\INPUTS";
    /**
     * output file number
     */
    public static int outputFileNum = 1;

    /**
     * MAIN FUNCTION
     *
     * The main function walks over all the input files in the given directory
     * and execute the A* search algorithm.
     *
     * @param   args           array of arguments
     * @throws  IOException    IOException exception
     */
    public static void main(String[] args) throws IOException {
        /*
            walk over all the regular files in dir and execute the solver function on each file
         */
        Files.walk(Paths.get(dir)).filter(Files::isRegularFile).forEach(Program::executeFromFile);

    }

    /**
     * This function reads an input file with a given Path, executes the A* search algorithm,
     * ,prints the solution, and writes the solution to a output file.
     *
     * @param   path    a Path of an input file
     */
    public static void executeFromFile(Path path) {

        try {
            /*
                convert a Path object to File object
             */
            File file = path.toFile();
            System.out.printf("File name: %s%n", file.getName());
            /*
                read the file
             */
            Scanner scanner = new Scanner(file);
            /*
                read the initial state into static variable initialState
             */
            for (int row = 0; row < ROWS; row++) {
                ArrayList<Integer> local = new ArrayList<>();
                for (int col = 0; col < COLS; col++) {
                    if (scanner.hasNext()) {
                        local.add(scanner.nextInt());
                    }
                }
                initialState.add(local);
            }

            scanner.nextLine();

            /*
                read the goal state into static variable goalState
             */
            for (int row = 0; row < ROWS; row++) {
                ArrayList<Integer> local = new ArrayList<>();
                for (int col = 0; col < COLS; col++) {
                    if (scanner.hasNext()) {
                        local.add(scanner.nextInt());
                    }
                }
                goalState.add(local);
            }

            /*
                execute the A* search algorithm and store the solution
             */
            LinkedList<TreeNode> solution = aStarSearchAlgorithm(initialState, goalState);
            /*
                print the solution
             */
            printSolution(solution);
            /*
                write the solution to Output%d.txt
             */
            writeSolution(solution);
            /*
                reset all the static variables
             */
            initialState.clear();
            goalState.clear();
            nodeCount = 0;
            checkDup.clear();

        } catch (FileNotFoundException ex) {
            System.out.println("File not found");
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns an integer that represents the current h(n) value of the given state.
     *
     * The heuristic function used here is the SUM of Manhattan Distances of each tile from its
     * current position to its goal position. the local variable sumOfManDis is initialized to 0, and
     * in each iteration of the for loop, Manhattan Distance is calculated by adding the difference of
     * row value and column value from current state to goal state,the Manhattan Distance of a tile is
     * then added to the local variable sumOfManDis.
     *
     * @param   currState   a two-dimensional ArrayList that represents the current state of the game
     * @param   goalState   a two-dimensional ArrayList that represents the goal state of the game
     * @return  an integer that is the heuristic function value of the current state
     */
    public static Integer heuristic(ArrayList<ArrayList<Integer>> currState, ArrayList<ArrayList<Integer>> goalState) {
        /*
            countMap stores a tile's tile position
         */
        HashMap<Integer, TileLocation> countMap = new HashMap<>(16);
        /*
            sum of Manhattan Distances
         */
        int sumOfManDis = 0;
        /*
            store all tile positions in the current state
         */
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (currState.get(row).get(col) != 0) {
                    countMap.put(currState.get(row).get(col), new TileLocation(row, col));
                }
            }
        }

        /*
            subtract each tile's position in the current state with its goal state position
            to get the Manhattan Distance, then add it to sumOfManDis
            for example: if tile A is at (0, 0) in currState and at (1, 2) in goalState,
            then its Manhattan Distance is |1 - 0| + |2 - 0| = 3
         */
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (goalState.get(row).get(col) != 0) {
                    Integer cur = goalState.get(row).get(col);
                    TileLocation tileLocation = countMap.get(cur);
                    int manDis = Math.abs(tileLocation.row - row) + Math.abs(tileLocation.col - col);
                    sumOfManDis += manDis;
                }
            }
        }

        return sumOfManDis;
    }

    /**
     * Returns the current location of a certain tile.
     *
     * If the tile number given is not found on the board then return null.
     *
     * @param   state     a two-dimensional ArrayList that represents a state of the game
     * @param   tileNum   an integer that represents the tile number on the game board
     * @return  a representation of a tile's position of type com.bananatofu.astar.TileLocation on the game board
     */
    public static TileLocation getTileLoc(ArrayList<ArrayList<Integer>> state, Integer tileNum) {

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (tileNum.equals(state.get(row).get(col))) {
                    return new TileLocation(row, col);
                }
            }
        }
        return null;
    }

    /**
     * Returns an ArrayList of Strings that represents the available next actions of the given state
     *
     * The static function takes a state and checks what next actions from {"U","D","L","R"} can be performed
     * with the given state, and the next moves are determined by the position of the zero tile on the
     * game board. For example if the zero tile is at the top left corner of the game board i.e.(0,0),
     * then only "D" and "R" are the next possible moves, and the actions will be added to the ArrayList.
     *
     * @param   state   a two-dimensional ArrayList that represents a state of the game
     * @return  an ArrayList of Strings that contains the available next moves of the given state
     */
    public static ArrayList<String> nextActions(ArrayList<ArrayList<Integer>> state) {

        ArrayList<String> availableActions = new ArrayList<>();
        /*
            obtain the position of tile 0
         */
        TileLocation zeroLoc = getTileLoc(state, 0);

        if (zeroLoc != null) {
            /*
                check if the 0 tile can move UP
             */
            if (!zeroLoc.row.equals(TOP_ROW)) {
                availableActions.add("U");
            }
            /*
                check if the 0 tile can move DOWN
             */
            if (!zeroLoc.row.equals(BOTTOM_ROW)) {
                availableActions.add("D");
            }
            /*
                check if the 0 tile can move LEFT
             */
            if (!zeroLoc.col.equals(LEFTMOST_COL)) {
                availableActions.add("L");
            }
            /*
                check if the 0 tile can move RIGHT
             */
            if (!zeroLoc.col.equals(RIGHTMOST_COL)) {
                availableActions.add("R");
            }
        } else {
            System.out.println("zeroLoc is null");
        }

        return availableActions;
    }

    /**
     * Returns a new state of the game after performing the given action on the old state.
     *
     * The static function first copies the current state into a local variable resultState, it then uses the
     * position of the zero tile to perform swapping on resultState depending on the action given.
     * If the action passed in is null or is not any of the four basic actions {"U","D","L","R"} then prints a
     * warning message.
     *
     * @param   currentState    a two-dimensional ArrayList that represents the current state of the game
     * @param   action          a String that represents the action to be performed
     * @return  a two-dimensional ArrayList that represents the state of the game after an action is performed
     */
    public static ArrayList<ArrayList<Integer>> performAction(ArrayList<ArrayList<Integer>> currentState, String action) {

        ArrayList<ArrayList<Integer>> resultState = new ArrayList<>();
        /*
            copy currentState into resultState
         */
        for (int row = 0; row < ROWS; row++) {
            ArrayList<Integer> local = new ArrayList<>();
            for (int col = 0; col < COLS; col++) {
                local.add(currentState.get(row).get(col));
            }
            resultState.add(local);
        }
        /*
            obtain the position of tile 0
         */
        TileLocation zeroLoc = getTileLoc(currentState, 0);

        switch (action) {

            case UP:
                /*
                    check if moving the 0 tile up is possible
                 */
                if (zeroLoc != null && !zeroLoc.row.equals(TOP_ROW)) {
                    /*
                        swap the 0 tile at (x, y) with the tile at (x - 1, y),
                        where x is row number, and y is col number
                     */
                    Integer temp = resultState.get(zeroLoc.row - 1).get(zeroLoc.col);
                    resultState.get(zeroLoc.row).set(zeroLoc.col, temp);
                    resultState.get(zeroLoc.row - 1).set(zeroLoc.col, 0);
                }
                break;

            case DOWN:
                /*
                    check if moving the 0 tile down is possible
                 */
                if (zeroLoc != null && !zeroLoc.row.equals(BOTTOM_ROW)) {
                    /*
                        swap the 0 tile at (x, y) with the tile at (x + 1, y),
                        where x is row number, and y is col number
                     */
                    Integer temp = resultState.get(zeroLoc.row + 1).get(zeroLoc.col);
                    resultState.get(zeroLoc.row).set(zeroLoc.col, temp);
                    resultState.get(zeroLoc.row + 1).set(zeroLoc.col, 0);
                }
                break;

            case LEFT:
                /*
                    check if moving the 0 tile left is possible
                 */
                if (zeroLoc != null && !zeroLoc.col.equals(LEFTMOST_COL)) {
                    /*
                        swap the 0 tile at (x, y) with the tile at (x, y - 1),
                        where x is row number, and y is col number
                     */
                    Integer temp = resultState.get(zeroLoc.row).get(zeroLoc.col - 1);
                    resultState.get(zeroLoc.row).set(zeroLoc.col, temp);
                    resultState.get(zeroLoc.row).set(zeroLoc.col - 1, 0);
                }
                break;

            case RIGHT:
                /*
                    check if moving the 0 tile right is possible
                 */
                if (zeroLoc != null && !zeroLoc.col.equals(RIGHTMOST_COL)) {
                    /*
                        swap the 0 tile at (x, y) with the tile at (x, y + 1),
                        where x is row number, and y is col number
                     */
                    Integer temp = resultState.get(zeroLoc.row).get(zeroLoc.col + 1);
                    resultState.get(zeroLoc.row).set(zeroLoc.col, temp);
                    resultState.get(zeroLoc.row).set(zeroLoc.col + 1, 0);
                }
                break;

            default:
                /*
                    the move is invalid
                 */
                System.out.println("action is null or not defined");
                break;
        }

        return resultState;
    }

    /**
     * Returns an ArrayList of TreeNodes that are generated by expanding a com.bananatofu.astar.TreeNode
     *
     * This static function generates all possible children from expanding a com.bananatofu.astar.TreeNode based on
     * all possible next actions of this com.bananatofu.astar.TreeNode. By iterating through the ArrayList of all
     * possible next actions, this for loop creates and appends a new child com.bananatofu.astar.TreeNode with
     * a child state by calling performAction to the ArrayList of com.bananatofu.astar.TreeNode local variable children.
     * The static variable nodeCount is also incremented in each iteration. the parent com.bananatofu.astar.TreeNode
     * is also updated with its newly generated children.
     *
     * @param   root    a com.bananatofu.astar.TreeNode to be expanded
     * @return  an ArrayList of com.bananatofu.astar.TreeNode that is obtained by expanding a com.bananatofu.astar.TreeNode
     */
    public static ArrayList<TreeNode> expand(TreeNode root) {

        ArrayList<TreeNode> children = new ArrayList<>();
        /*
            obtain available next actions by calling nextAction
         */
        ArrayList<String> moves = nextActions(root.state);
        for (String move : moves) {
            /*
                obtain child state by calling performAction
             */
            ArrayList<ArrayList<Integer>> childState = performAction(root.state, move);
            TreeNode child = new TreeNode(childState, root, move, null, 1 + root.pathCost);
            children.add(child);
            /*
                increment com.bananatofu.astar.TreeNode counter
             */
            nodeCount++;
        }
        /*
            add all children to its parent com.bananatofu.astar.TreeNode's childArray if the ArrayList children is not empty
         */
        if (children.size() > 0) {
            root.childArray = new ArrayList<>();
            root.childArray.addAll(children);
        }
        return children;
    }

    /**
     * This static function prints out the solution in the following format:
     * ***************************
     * n   n   n   n
     * n   n   n   n
     * n   n   n   n
     *
     * m   m   m   m
     * m   m   m   m
     * m   m   m   m
     *
     * d
     * N
     * A   A   A   A   A    ...
     * f   f   f   f   f   f    ...
     *
     * @param   solution    a LinkedList of TreeNodes that represents the solution path of a problem
     */
    public static void printSolution(LinkedList<TreeNode> solution) {

        System.out.println("***************************");
        /*
            print initial state
         */
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                System.out.print(initialState.get(row).get(col) + "\t");
            }
            System.out.println();
        }

        System.out.println();
        /*
            print goal state
         */
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                System.out.print(goalState.get(row).get(col) + "\t");
            }
            System.out.println();
        }

        System.out.println();
        /*
            print level
         */
        System.out.println(solution.size() - 1);
        /*
            print the number of TreeNodes in the search tree
         */
        System.out.println(nodeCount);
        /*
            print action
         */
        for (int i = 1; i < solution.size(); i++) {
            System.out.print(solution.get(i).action+"\t");
        }

        System.out.println();
        /*
            print f(n) value
         */
        for (TreeNode treeNode : solution) {
            System.out.print(treeNode.f + "\t");
        }

        System.out.println();
        System.out.println();
    }

    /**
     *
     * @param   solution       a LinkedList of TreeNodes that represents the solution path of a problem
     * @throws  IOException    IOException exception
     */
    public static void writeSolution(LinkedList<TreeNode> solution) throws IOException {
        /*
            create a output file in the same dir where the input files are located
         */
        File output = new File(String.format(dir + "\\Output%d.txt", outputFileNum));
        /*
            increment the output file number
         */
        outputFileNum++;
        FileWriter writer = new FileWriter(output);
        /*
            write initial state
         */
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                writer.write(initialState.get(row).get(col) + " ");
            }
            writer.write("\n");
        }

        writer.write("\n");
        /*
            write goal state
         */
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                writer.write(goalState.get(row).get(col) + " ");
            }
            writer.write("\n");
        }

        writer.write("\n");
        /*
            write level
         */
        writer.write(solution.size() - 1 + "\n");
        /*
            write the number of TreeNodes in the search tree
         */
        writer.write(nodeCount + "\n");

        /*
            write action
         */
        for (int i = 1; i < solution.size(); i++) {
            writer.write(solution.get(i).action+" ");
        }

        writer.write("\n");
        /*
            write f(n) value
         */
        for (TreeNode treeNode : solution) {
            writer.write(treeNode.f + " ");
        }

        writer.close();
    }


    /**
     * Returns a LinkedList of com.bananatofu.astar.TreeNode that contains all the TreeNodes on the optimal solution path
     *
     * The static function adds the leaf com.bananatofu.astar.TreeNode and all its parent TreeNodes at the head of a
     * LinkedList and then returns the List.
     *
     * @param   node    a com.bananatofu.astar.TreeNode that contains the gaol state and is the leaf com.bananatofu.astar.TreeNode in the A* search tree
     * @return  a LinkedList of TreeNodes that represents the solution path
     */
    public static LinkedList<TreeNode> reconstructPath(TreeNode node) {

        LinkedList<TreeNode> solution = new LinkedList<>();
        TreeNode curr = node;
        while (curr != null) {
            solution.addFirst(curr);
            curr = curr.parent;
        }
        return solution;
    }

    /**
     * A* ALGORITHM MAIN SOLVER
     *
     * Returns a LinkedList of com.bananatofu.astar.TreeNode that contains all the TreeNodes on the optimal solution path
     *
     * This static function is the main A* problem solver. The function uses a PriorityQueue that orders
     * TreeNodes by their f(n) value. At the beginning, a root com.bananatofu.astar.TreeNode containing the initial state
     * is pushed to the PriorityQueue, and in every iteration in the while loop, the top com.bananatofu.astar.TreeNode with
     * the minimum f(n) value will be popped and expanded.
     *
     * State repetition is checked for each com.bananatofu.astar.TreeNode generated using the checkDup HashMap;
     * if a duplicated com.bananatofu.astar.TreeNode has a greater value of f(n) compared to the old one,
     * then this com.bananatofu.astar.TreeNode is deleted and removed from its parent com.bananatofu.astar.TreeNode's childArray;
     * if a duplicated com.bananatofu.astar.TreeNode has a lower f(n) value compared to the old one in checkDup, then the old one is
     * deleted and removed from its parent com.bananatofu.astar.TreeNode's childArray. At the end of each iteration in the while loop,
     * the valid newly generated com.bananatofu.astar.TreeNode are pushed to the PriorityQueue, and the while loop continues.
     *
     * nodeCount will be decremented for each child com.bananatofu.astar.TreeNode deleted.
     *
     * @param   initialState    a two-dimensional ArrayList that represents the initial state of the game
     * @param   goalState       a two-dimensional ArrayList that represents the goal state of the game
     * @return  a LinkedList of TreeNodes that contains all the TreeNodes on the optimal solution path
     */
    public static LinkedList<TreeNode> aStarSearchAlgorithm(ArrayList<ArrayList<Integer>> initialState, ArrayList<ArrayList<Integer>> goalState) {
        /*
            a Priority queue that orders com.bananatofu.astar.TreeNode by comparing their f(n) value;
            com.bananatofu.astar.TreeNode with smallest f(n) value is at the top of this PriorityQueue;
            Initial capacity is set to 16.
         */
        PriorityQueue<TreeNode> frontier = new PriorityQueue<>(16, Comparator.comparingInt(o -> o.f));
        /*
            creating the root com.bananatofu.astar.TreeNode that contains the initial state
         */
        TreeNode root = new TreeNode(initialState, null, null, null, 0);
        /*
            adding the initial state to checkDup HashMap
         */
        checkDup.put(initialState, root);
        /*
            push the root into the PriorityQueue frontier
         */
        frontier.add(root);
        /*
            increment com.bananatofu.astar.TreeNode counter
         */
        nodeCount++;

        while (!frontier.isEmpty()) {
            /*
                pop the top com.bananatofu.astar.TreeNode in frontier and store it in the local variable current
             */
            TreeNode current = frontier.poll();
            /*
                check if the current com.bananatofu.astar.TreeNode contains the goal state
             */
            if (current.state.equals(goalState)) {
                /*
                    the current node has the goal state; reconstruct the solution path and return
                 */
                return reconstructPath(current);
            }
            /*
                expand the current com.bananatofu.astar.TreeNode (duplicates may exist)
             */
            ArrayList<TreeNode> arrayFromExpansion = expand(current);
            /*
                after deleting all repeated TreeNodes, validate TreeNodes are added to the goodChild ArrayList
             */
            ArrayList<TreeNode> goodChild = new ArrayList<>();
            /*
                for each generated child com.bananatofu.astar.TreeNode after expanding the current Node, check state repetition
             */
            for (TreeNode childNode : arrayFromExpansion) {
                /*
                    current child state
                 */
                ArrayList<ArrayList<Integer>> childState = childNode.state;
                /*
                    check if the child state repeats a state in checkDup
                 */
                if (checkDup.containsKey(childState)) {
                    /*
                        repetition confirmed;
                        check if the new duplicate has smaller f(n) value, if so delete the old com.bananatofu.astar.TreeNode
                        decrement com.bananatofu.astar.TreeNode counter
                     */
                    if (childNode.f < checkDup.get(childState).f) {
                        /*
                            delete old com.bananatofu.astar.TreeNode
                         */
                        TreeNode nodeToDelete = checkDup.get(childState);
                        TreeNode parent = nodeToDelete.parent;
                        parent.childArray.remove(nodeToDelete);
                        nodeToDelete.parent = null;
                        /*
                            decrement com.bananatofu.astar.TreeNode counter
                         */
                        nodeCount--;
                    }
                    /*
                        repetition confirmed;
                        the new com.bananatofu.astar.TreeNode has a equivalent or higher f(n) value, so delete this one
                        decrement com.bananatofu.astar.TreeNode counter
                     */
                    else {
                        /*
                            delete new com.bananatofu.astar.TreeNode
                         */
                        TreeNode parent = childNode.parent;
                        parent.childArray.remove(childNode);
                        childNode.parent = null;
                        /*
                            decrement com.bananatofu.astar.TreeNode counter
                         */
                        nodeCount--;
                        /*
                            no need to add the com.bananatofu.astar.TreeNode to goodChild, so continue to next iteration
                         */
                        continue;
                    }
                }
                /*
                    update checkDup with the new child com.bananatofu.astar.TreeNode
                 */
                checkDup.put(childState, childNode);
                /*
                    add the validate child com.bananatofu.astar.TreeNode to goodChild
                 */
                goodChild.add(childNode);
            }

            /*
                add all validate child com.bananatofu.astar.TreeNode to the frontier
             */
            for (TreeNode treeNode : goodChild) {
                if (!frontier.contains(treeNode)) {
                    frontier.add(treeNode);
                }
            }

        }

        /*
            return a empty LinkedList since solution does not exist
         */
        return new LinkedList<>();
    }

}

/**
 * This class represents the 2-dimensional posiiton of a tile on the game board
 */
class TileLocation {
    /**
     * Constructor of com.bananatofu.astar.TileLocation
     *
     * @param   row   an integer that represents a tile's row number (starts from 0)
     * @param   col   an integer that represents a tile's column number (starts from 0)
     */
    public TileLocation(Integer row, Integer col) {
        this.row = row;
        this.col = col;
    }

    /**
     * row number
     */
    Integer row;
    /**
     * column number
     */
    Integer col;
}

/**
 * This class represents the node generated in the A* search tree
 */
class TreeNode {
    /**
     * Constructor of com.bananatofu.astar.TreeNode
     *
     * @param   state         a two-dimensional ArrayList that represents a state of the game
     * @param   parent        a com.bananatofu.astar.TreeNode which is the parent of the current com.bananatofu.astar.TreeNode
     * @param   action        a String that represents the action taken from its parent com.bananatofu.astar.TreeNode's state
     * @param   childArray    a ArrayList of com.bananatofu.astar.TreeNode that represents the children of this com.bananatofu.astar.TreeNode
     * @param   pathCost      an int that represents the path cost g(n)
     */
    public TreeNode(ArrayList<ArrayList<Integer>> state, TreeNode parent, String action, ArrayList<TreeNode> childArray, int pathCost) {
        /*
            copy the state passed in into this.state
         */
        for (int row = 0; row < Program.ROWS; row++) {
            ArrayList<Integer> local = new ArrayList<>();
            for (int col = 0; col < Program.COLS; col++) {
                local.add(state.get(row).get(col));
            }
            this.state.add(local);
        }
        this.parent = parent;
        this.action = action;
        /*
            copy all children passed in into this.childArray
         */
        if (childArray != null) {
            this.childArray = new ArrayList<>();
            this.childArray.addAll(childArray);
        }
        this.heuristic = Program.heuristic(this.state, Program.goalState);
        this.pathCost = pathCost;
        this.f = pathCost + heuristic;
    }

    /**
     * the state of the game
     */
    public ArrayList<ArrayList<Integer>> state = new ArrayList<>();
    /**
     * parent com.bananatofu.astar.TreeNode
     */
    public TreeNode parent;
    /**
     * action performed from its parent com.bananatofu.astar.TreeNode's state
     */
    public String action;
    /**
     * children com.bananatofu.astar.TreeNode
     */
    public ArrayList<TreeNode> childArray;
    /**
     * heuristic function h(n) value of the current state
     */
    public int heuristic;
    /**
     * path cost g(n) from initial state to the current state
     */
    public int pathCost;
    /**
     * f(n) = h(n) + g(n)
     */
    public int f;
}