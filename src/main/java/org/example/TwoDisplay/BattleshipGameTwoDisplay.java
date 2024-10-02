package org.example.TwoDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/*
* class1: BattleShipGameMiss
* - input: Attack Coordinates(e.g., A1, g9)
* - private static final int BOARD_SIZE
* - private static final int MAX_MISSES
* - char[][] loweBoard;
* - char[][] upperBoard;
* - List<Ship> ships;
* - int misses;
*
* # Constructor
*   - initializedBoard()
*   - placeAllShips()
*
* # Method
*  - void initializedBoard(): Initialize the board with empty space
*  - void placeAllShips: Placed the all ships
*    - void placeShip: Placed a single ship
*       - boolean canPlaceShip: Check the ship can be placed at the position
*  - String attack
*    - Ship getShipByChar: Get the ship object by its first letter -> use for counting the hitCount -> isSunk?
*  - boolean isGameOver
*  - void displayBoard
*  - main : Main method to run game
*  - static int[] getValidCoordinates
*
* class2: Ship
*  - name, length, hitCount
* */
public class BattleshipGameTwoDisplay {
    private static final int BOARD_SIZE = 10;
    private static final int MAX_MISSES = 20; // Maximum allowed misses
    private char[][] lowerBoard; // Computer's ship placement board (lower board)
    private char[][] upperBoard; // Player's attack history board (upper board)
    private List<Ship> ships;
    private int misses = 0; // Count of misses

    // Constructor
    public BattleshipGameTwoDisplay() {
        lowerBoard = new char[BOARD_SIZE][BOARD_SIZE];
        upperBoard = new char[BOARD_SIZE][BOARD_SIZE];
        ships = new ArrayList<>();
        initializedBoard(lowerBoard);
        initializedBoard(upperBoard);
        placeAllShips();
    }

    private void initializedBoard(char[][] board) {
        for(int i = 0; i < BOARD_SIZE; i++) {
            for(int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = '.';
            }
        }
    }

    private void placeAllShips() {
        placeShip(new Ship("Carrier", 5, 'C'));     // Carrier occupies 5 spaces
        placeShip(new Ship("Battleship", 4,'B'));  // Battleship occupies 4 spaces
        placeShip(new Ship("Cruiser", 3, 'R'));     // Cruiser occupies 3 spaces
        placeShip(new Ship("Submarine", 3, 'S'));   // Submarine occupies 3 spaces
        placeShip(new Ship("Destroyer", 2, 'D'));   // Destroyer occupies 2 spaces
    }

    private void placeShip(Ship ship) {
        Random random = new Random();
        boolean placed = false;

        while(!placed) {
            int x = random.nextInt(BOARD_SIZE); // Random x coordinate
            int y = random.nextInt(BOARD_SIZE); // Random y coordinate
            boolean isHorizontal = random.nextBoolean(); // Random orientation

            if(canPlaceShip(ship, x, y, isHorizontal)){
                if(isHorizontal) { // Place ship horizontally
                    for(int i = 0; i < ship.length; i++) {
                        lowerBoard[x][y + i] = ship.symbol;
                    }
                } else { // Place ship vertically
                    for(int i = 0; i < ship.length; i++) {
                        lowerBoard[x + i][y] = ship.symbol;
                    }
                }
                ships.add(ship); // Add ship to the list
                placed = true; // Mark as placed
            }
        }
    }

    private boolean canPlaceShip(Ship ship, int x, int y, boolean isHorizontal) {
        if(isHorizontal){
            if(y + ship.length > BOARD_SIZE) return false;
            for(int i = 0; i < ship.length; i++) {
                if(lowerBoard[x][y+i] != '.') return false;
            }
        } else {
            if(x + ship.length > BOARD_SIZE) return false;
            for(int i = 0; i < ship.length; i++) {
                if(lowerBoard[x+i][y] != '.') return false;
            }
        }
        return true;
    }

    // lower -> placed the ships ||| upper -> attack history
    public String attack(int x, int y) {
        if(x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE) return "Invalid input.";
        if(upperBoard[x][y] != '.') return "Already attacked";

        if(lowerBoard[x][y] == '.') {
            upperBoard[x][y] = 'M';
            misses++;
            return "Miss";
        } else {
            char shipChar = lowerBoard[x][y];
            upperBoard[x][y] = 'H';
            lowerBoard[x][y] = 'X';
            Ship hitShip = getShipByChar(shipChar);
            hitShip.hitCount++;

            if(hitShip.isSunk()) return "Sunk " + hitShip.name;

            return "Hit " + hitShip.name;
        }
    }

    // Get the ship object by its first letter
    private Ship getShipByChar(char shipChar) {
        for(Ship ship : ships){
            if(ship.name.charAt(0) == shipChar) return ship;
        }
        return null;
    }

    public boolean isGameOver() {
        if(misses >= MAX_MISSES){
            System.out.println("You have exhausted all your guesses. Game Over");
            return true;
        }

        for(Ship ship : ships) {
            if(!ship.isSunk()){
                return false;
            }
        }
        System.out.println("Congraz! Game Over. You Win!");
        return true;
    }

    public void displayBoards() {
        System.out.println("Upper Board");
        displayBoard(upperBoard);

        System.out.println("Lower Board");
        displayBoard(lowerBoard);
    }

    private void displayBoard(char[][] board) {
        for(int i = 0; i < BOARD_SIZE; i++) {
            for(int j = 0; j < BOARD_SIZE; j++){
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Main method to run the game
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BattleshipGameTwoDisplay game = new BattleshipGameTwoDisplay();
        System.out.println("*** START GAME ***");

        while(!game.isGameOver()){
            game.displayBoards();
            int[] coordinates = getValidCoordinates(scanner);
            int x = coordinates[0];
            int y = coordinates[1];

            String result = game.attack(x, y);
            System.out.println(result);
        }
        scanner.close();
    }

    // Method to get valid coordinates from the player
    private static int[] getValidCoordinates(Scanner scanner){
        int x = -1;
        int y = -1;
        boolean validInput = false;

        while(!validInput) {
            System.out.println("Enter coordinates to attack (e.g., A1, B3):");
            String input = scanner.nextLine().trim().toUpperCase(); // Convert input to uppercase

            // Check if input format is valid
            if (input.length() < 2 || input.length() > 3) {
                System.out.println("Invalid input. Please enter a letter followed by a number.");
                continue;
            }

            // First character should be a letter representing the column
            char letter = input.charAt(0);
            int maxLetter = 'A' + (BOARD_SIZE - 1); // Calculate the maximum letter allowed
            if (letter < 'A' || letter > 'J') {
                System.out.println("Invalid input. The letter must be between A and " + (char)maxLetter);
                continue;
            }
            x = letter - 'A'; // Convert letter to 0-based index

            // Remaining part should be a number
            try {
                y = Integer.parseInt(input.substring(1));
                if (y < 0 || y >= BOARD_SIZE) {
                    System.out.println("Invalid input. The number must be between 1 and 10.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. The number must be between 1 and 10.");
                continue;
            }

            validInput = true; // Input is valid
        }
        return new int[]{x, y};
    }
}

class Ship {
    String name;
    int length;
    int hitCount;
    char symbol;

    // Constructor
    public Ship(String name, int length, char symbol){
        this.name = name;
        this.length = length;
        this.symbol = symbol;
        this.hitCount = 0;
    }

    // Check if the ship is sunk
    public boolean isSunk() {
        return hitCount >= length;
    }
}
