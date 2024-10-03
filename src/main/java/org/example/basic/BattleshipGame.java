package org.example.basic;

import java.util.*;

class BattleshipGame {
    /*
     * class 1: BattleshipGame
     * - Input: Attack coordinates(A0, B4)
     *
     * Method
     * - void initializeBoard: Initialize the board with empty space
     * - void placeAllShips: Place ships on the board
     *   - void placeShip: Place a singe ship
     *     - boolean canPlaceShip: Check the ship can be place at the position
     * - String attack
     *   - Ship getShipByChar: To count the hitCount
     * - boolean isGameOver
     * - void displayBoard
     * - main: Main Method to run the game
     * - static int[] getValidCoordinates
     *
     * class2: Ship
     * - name, length, hitCount(-> isSunk?)
     * */
    private static final int BOARD_SIZE = 10; // The size of the board
    private static final int MAX_MISSES = 2; // Maximum allowed misses
    private char[][] board; // 2D array representing  the game board
    private List<Ship> ships; // List to store the ships
    private int misses = 0; // Count of misses
    private int sunkCnt = 0;

    // Constructor
    public BattleshipGame() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        ships = new ArrayList<>();
        initializeBoard();
        placeAllShips();
    }

    // Initialize the board with empty spaces
    private void initializeBoard() {
        for(int i = 0; i < BOARD_SIZE; i++) { // Rows
            for(int j = 0; j < BOARD_SIZE; j++) { // Columns
                board[i][j] = '.'; // use '.' to represent empty cells
            }
        }
    }

    // Place ships on the board(Auto) - Add Ships: Carrier(5), Battleship(4), Destroyer(3)
    private void placeAllShips() {
        placeShip(new Ship("Carrier", 1));
        placeShip(new Ship("Battleship", 1));
        placeShip(new Ship("Destroyer", 1));
    }

    // Place a single ship on the board
    private void placeShip(Ship ship) {
        Random random = new Random();
        boolean placed = false;

        while(!placed) { // when place = false
            int x  = random.nextInt(BOARD_SIZE); // Random x-coordinate
            int y = random.nextInt(BOARD_SIZE); // Random y-coordinate
            boolean isHorizontal = random.nextBoolean(); // Random orientation(방향)

            if(canPlaceShip(ship, x, y, isHorizontal)) { // If the ship can be placed at the random coordinates
                if(isHorizontal) {
                    for(int i = 0; i < ship.length; i++) {
                        board[x][y + i] = ship.name.charAt(0);

                    }
                } else { // isVertical
                    for(int i = 0; i < ship.length; i++) {
                        board[x + i][y] = ship.name.charAt(0);
                    }
                }
                ships.add(ship); // Add the ship to the list
                placed = true; // Mark that the ship has been placed
            }
        }
    }

    // Check if the ship can be placed at the given position
    private boolean canPlaceShip(Ship ship, int x, int y, boolean isHorizontal) {
        if(isHorizontal) {
            if(y + ship.length > BOARD_SIZE) return false;
            for(int i = 0; i < ship.length; i++) {
                if (board[x][y + i] != '.') return false;
            }
        } else { // isVertical
            if(x + ship.length > BOARD_SIZE) return false;
            for(int i = 0; i < ship.length; i++) {
                if(board[x + i][y] != '.') return false;
            }
        }
        return true;
    }

    public String attack(int x, int y) {
        if(x < 0 || x > BOARD_SIZE || y < 0 || y > BOARD_SIZE) return "Invalid coordinates!";
        if(board[x][y] == '.') {
            board[x][y] = 'M';
            misses++;
            return "Miss";
        } else if(board[x][y] == 'M' || board[x][y] == 'X') {
            return "Already attacked";
        } else {
            char shipChar = board[x][y]; //
            board[x][y] = 'X';
            Ship hitShip = getShipByChar(shipChar);
            hitShip.hitCount++;

            if(hitShip.isSunk()) {
                sunkCnt++;
                return "Sunk " + hitShip.name;
            }
            return "Hit " + hitShip.name;
        }
    }

    // Find the ship corresponding to a character
    private Ship getShipByChar(char shipChar) {
        for(Ship ship : ships) {
            if(ship.name.charAt(0) == shipChar) return ship;
        }
        return null;
    }

    // Check if the game is over = All ships are sunk
    public boolean isGameOver() {
        if(misses >= MAX_MISSES){
            System.out.println("You have exhausted all your guesses. Game Over");
            return true;
        }

        // If the number of ships increases, the time complexity worsens
//        for(Ship ship : ships) {
//            if(!ship.isSunk()) {
//                return false;
//            }
//        }
        if(sunkCnt < ships.toArray().length) {
            return false;
        }
        System.out.println("You Win. Game over!");
        return true;
    }

    public void displayBoard() {
        // Basic DisplayBoard
//        for(int i = 0; i < BOARD_SIZE; i++) {
//            for(int j = 0; j < BOARD_SIZE; j++) {
//                System.out.print(board[i][j] + " ");
//            }
//            System.out.println();
//        }

        // Print column headers
        System.out.print("  "); // Space for row headers
        for (int j = 0; j < BOARD_SIZE; j++) {
            System.out.print(j + " "); // Print column numbers
        }
        System.out.println(); // Move to the next line

        // Print each row
        for (int i = 0; i < BOARD_SIZE; i++) {
            // Print row header
            System.out.print((char) ('A' + i) + " "); // Convert row index to corresponding letter

            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board[i][j] + " "); // Print board cell
            }
            System.out.println(); // Move to the next line after finishing a row
        }
    }

    // Main method to run the game
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BattleshipGame game = new BattleshipGame();
        System.out.println("*** Battleship Game ***");

        while(!game.isGameOver()) {
            game.displayBoard();
            /*System.out.println("Enter coordinates to attack(x y) : ");
            int x = scanner.nextInt();
            int y = scanner.nextInt();*/

            int[] coordinates = getValidCoordinates(scanner);
            int x = coordinates[0];
            int y = coordinates[1];

            String result = game.attack(x, y);
            System.out.println(result);
        }
        scanner.close();
    }

    private static int[] getValidCoordinates(Scanner scanner) {
        int x = -1; // Initialize to -1
        int y = -1;
        boolean validInput = false; // Flag to track if input is valid

        // input을 모두 숫자로 받을 때
//        while(!validInput) {
//            System.out.println("Enter coordinates to attack (x y) :");
//            String input = scanner.nextLine().trim();
//
//            String[] str = input.split("\\s+"); // Split input by spaces
//
//            if(str.length != 2) {
//                System.out.println("Invalid input. Please enter two integers separated by a space.");
//                continue;
//            }
//
//            try {
//                x = Integer.parseInt(str[0]);
//                y = Integer.parseInt(str[1]);
//
//                if(x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE) {
//                    System.out.println("Coordinates out of bounds");
//                } else {
//                    validInput = true; // Valid input received
//                }
//            } catch(NumberFormatException e) {
//                System.out.println("Invalid input. Please enter valid integers");
//            }
//        }

        // Loop until valid input is received
        while (!validInput) {
            System.out.println("Enter coordinates to attack (e.g., A1, B3) :");
            String input = scanner.nextLine().trim().toUpperCase(); // Read a trim input, convert to uppercase

            // Check if input length is valid(e.g., A1, A10)
            if(input.length() < 2 || input.length() > 3) {
                System.out.println("Invalid input. Please enter a letter followed by a number");
                continue; // Ask input again
            }

            // 1st character should be a letter representing the coulumn
            char letter = input.charAt(0);
            int maxLetter = 'A' + (BOARD_SIZE - 1); // Calculate the maximum letter allowed
            if(letter < 'A' || letter > maxLetter) {
                System.out.println("Invalid input. Letter should be between A and " + + (char)maxLetter);
                continue;
            }

            // The remaning part should be a number respresenting the row
            try {
                y = Integer.parseInt(input.substring(1)); // Convert the number to part of Integer
                if(y < 0 || y >= BOARD_SIZE) {
                    System.out.println("Coordinates out of bounds. Number should be between 0 and " + (BOARD_SIZE - 1));
                    continue;
                }

                // Convert letter to column index
                x = letter - 'A';
                validInput = true; // Mark the input as valid
            } catch(NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number after the letter.");
            }
        }
        // return the numeric coordinates
        return new int[]{x, y};
    }
}

class Ship {
    String name;
    int length;
    int hitCount;

    public Ship(String name, int length) {
        this.name = name;
        this.length = length;
        this.hitCount = 0;
    }

    public boolean isSunk() {
        return hitCount == length;
    }
}
