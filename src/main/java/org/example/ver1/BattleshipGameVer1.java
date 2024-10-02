package org.example.ver1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BattleshipGameVer1 {
    /*
    * class 1: BattleshipGameVer1
    * - Input1: Ship coordinates(Latter + Number(e.g., A9, f0)),
    * - input2: Orientation(H = Horizontal, V = Vertical)
    * - Input3: Attack coordinates(A0, B4)
    *
    * Method
    * - void initializeBoard: Initialize the board with empty space
    * - void placeAllShips: Place ships on the board
    *   - void placeShip: Place a singe ship/ Scanner for Input1, 2
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
    private static final int BOARD_SIZE = 10;
    private char[][] board; // 2D array representing the game board
    private List<Ship> ships; // List to store the ships

    // Constructor
    public BattleshipGameVer1() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        ships = new ArrayList<>();
        initializeBoard(); // Initialize the board with empty space
        placeAllShips(); // Place ships on the board(User Input)
    }

    private void initializeBoard() {
        for(int i = 0; i < BOARD_SIZE; i++) { // Row
            for(int j = 0; j < BOARD_SIZE; j++) { // Column
                board[i][j] = '.'; // . = empty
            }
        }
    }

    private void placeAllShips() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Place your ships on the board: ");
        placeShip(new Ship("Carrier", 5), scanner);
        placeShip(new Ship("Battleship", 4), scanner);
        placeShip(new Ship("Destroyer", 3), scanner);
    }

    private void placeShip(Ship ship, Scanner scanner) {
        boolean placed = false; // flag

        while(!placed) { // Keep trying until the ship is placed
            System.out.println("Placeing " + ship.name + " (length: " + ship.length + ")");
            System.out.println("Enter starting coordinates: ");

            int[] coordinates = getValidCoordinates(scanner);
            int x = coordinates[0];
            int y = coordinates[1];

            boolean vaildOrientation = false;
            String orientation = "";
            while(!vaildOrientation) {
                System.out.println("Enter orientation(H: Horizontal, V: Vertical): ");
                orientation = scanner.nextLine().trim().toUpperCase();
                if(!orientation.equals("H") && !orientation.equals("V")){
                    System.out.println("Invalid input(H: Horizontal, V: Vertical)");
                    continue;
                }
                vaildOrientation = true;
            }

            boolean isHorizontal = orientation.equals("H");

            if(canPlaceShip(ship, x, y, isHorizontal)) { // If the ship can be placed
                if(isHorizontal) {
                    for(int i = 0; i < ship.length; i++) {
                        board[x][y + i] = ship.name.charAt(0);
                    }
                } else { //isVertical
                    for(int i = 0; i < ship.length; i++) {
                        board[x + i][y] = ship.name.charAt(0);
                    }
                }
                ships.add(ship);
                placed = true;
            } else {
                System.out.println("Invalid position or orientation.");
            }
        }
    }

    // Check is the ship can be placed at that position
    private boolean canPlaceShip(Ship ship, int x, int y, boolean isHorizontal) {
        if(isHorizontal) {
            if(y + ship.length > BOARD_SIZE) return false;
            for(int i = 0; i < ship.length; i++) {
                if(board[x][y+i] != '.') return false;
            }
        } else { //isVertical
            if(x + ship.length > BOARD_SIZE) return false;
            for(int i = 0; i < ship.length; i++) {
                if(board[x +i][y] != '.') return false;
            }
        }
        return true;
    }

    public String attack(int x, int y) {
        if(x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE) return "Invalid coordinates";
        if(board[x][y] == '.') {
            board[x][y] = 'M';
            return "Miss";
        } else if(board[x][y] == 'M' || board[x][y] == 'X') {
            return "Already attack!";
        } else {
            char shipChar = board[x][y];
            board[x][y] = 'X';
            Ship hitShip = getShipByChar(shipChar);
            hitShip.hitCount++;

            if(hitShip.isSunk()) {
                return "Sunk " + hitShip.name;
            }
            return "Hit " + hitShip.name;
        }
    }
    private Ship getShipByChar(char shipChar) {
        for(int i = 0; i < ships.size(); i++) {
            if(ships.get(i).name.charAt(0) == shipChar) {
                return ships.get(i);
            }
        }
        return null;
    }

    public boolean isGameOver() {
        for(Ship ship: ships) {
            if(!ship.isSunk()) return false;
        }
        return true;
    }

    public void displayBoard() {
        for(int i = 0; i<BOARD_SIZE; i++) {
            for(int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Main Method to run the game
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BattleshipGameVer1 game = new BattleshipGameVer1();
        System.out.println("GAME START");

        while(!game.isGameOver()) {
            game.displayBoard();

            System.out.println("Enter coordinates bto attack(e.g., A0, B3): ");

            int[] coordinates = getValidCoordinates(scanner);
            int x = coordinates[0];
            int y = coordinates[1];

            String result = game.attack(x, y);
            System.out.println(result);
        }

        System.out.println("Game OVER");
        scanner.close();
    }

    private static int[] getValidCoordinates(Scanner scanner) {
        int x = -1;
        int y = -1;
        boolean validInput = false;

        while(!validInput) {
            String input = scanner.nextLine().trim().toUpperCase();

            if(input.length() < 2 || input.length() > 3) {
                System.out.println("Invalid input. Please enter a latter followed by number");
                continue;
            }

            char letter = input.charAt(0);
            int maxLetter = 'A' + (BOARD_SIZE - 1); // Calculate the maximum letter allowed
            if(letter < 'A' || letter > maxLetter) {
                System.out.println("Invalid input. Please enter a latter between A and " + (char)maxLetter);
                continue;
            }

            try {
                y = Integer.parseInt(input.substring(1));
                if(y < 0 || y >= BOARD_SIZE) {
                    System.out.println("Coordinates out of bound. Number should be between 0 and " + (BOARD_SIZE-1));
                    continue;
                }
                x = letter - 'A'; // Convert letter to index
                validInput = true;
            } catch (NumberFormatException e){
                System.out.println("Invalid input. Please enter a valid input");
            }
        }
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
