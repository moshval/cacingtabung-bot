/* Kelompok Cacing Tabung 
    13519018 - Mohammad Sheva Almeyda Sofjan (K1)
    13519069 - Muhammad Fikri N. (K2)
    13519091 - Mohammad Yahya Ibrahim (K2)

    Tugas Besar 1 IF2211 Strategi Algoritma
    Penerapan Algoritma Greedy - Entelect Challenge 2019 "Worms"

*/

package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.CellType;
import za.co.entelect.challenge.enums.Direction;

import java.util.*;
import java.util.stream.Collectors;

public class Bot {

    private Random random;
    private GameState gameState;
    private Opponent opponent;
    private MyWorm currentWorm;

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gameState = gameState;
        this.opponent = gameState.opponents[0];
        this.currentWorm = getCurrentWorm(gameState);
    }

    // Getter Worm
    private MyWorm getCurrentWorm(GameState gameState) {
        return Arrays.stream(gameState.myPlayer.worms)
                .filter(myWorm -> myWorm.id == gameState.currentWormId)
                .findFirst()
                .get();
    }

    // Method buat run
    public Command run() {
        Worm enemyWorm;
        Direction direction;
        List<Cell> surroundingBlocks;
        int cellIdx;
        Cell block;

        // Run case tergantung jenis worm
        if(currentWorm.id == 1){  // 
            enemyWorm = getFirstWormInRange();
            if (enemyWorm != null) {
                direction = resolveDirection(currentWorm.position, enemyWorm.position);
                return new ShootCommand(direction);
            }

        }
        else if(currentWorm.id == 2){
            enemyWorm = getFirstWormInBananaRange();
            if (enemyWorm != null && currentWorm.bananaBombs.count > 0) { // Kalo musuh di range banana bomb dan count bomb > 0 berarti do command bananabomb
                return new BananaCommand(enemyWorm.position.x,enemyWorm.position.y);
            }
            enemyWorm = getFirstWormInRange();
            if (enemyWorm != null) {  // Musuh di range shoot general weapon
                direction = resolveDirection(currentWorm.position, enemyWorm.position);
                return new ShootCommand(direction);
            }

        }

        else if(currentWorm.id == 3){
            enemyWorm = getFirstWormInSnowballRange();
            if (enemyWorm != null && currentWorm.snowballs.count > 0 && enemyWorm.roundsUntilUnfrozen == 0) { // Kalo musuh di range snowballsb dan count snowballs > 0 berarti do command snowballs
                return new SnowballCommand(enemyWorm.position.x,enemyWorm.position.y);
            }
            enemyWorm = getFirstWormInRange();
            if (enemyWorm != null) {  // Musuh di range shoot general weapon
                direction = resolveDirection(currentWorm.position, enemyWorm.position);
                return new ShootCommand(direction);
            }

        }

        surroundingBlocks = getSurroundingCells(currentWorm.position.x, currentWorm.position.y);
        cellIdx = random.nextInt(surroundingBlocks.size());

        block = surroundingBlocks.get(cellIdx);
        if (block.type == CellType.AIR) {
            return new MoveCommand(block.x, block.y);
        } else if (block.type == CellType.DIRT) {
            return new DigCommand(block.x, block.y);
        }

        return new DoNothingCommand();

    }


    // Cek Ada nearby enemy worm dalam shooting range, mereturn detail worm enemy
    private Worm getFirstWormInRange() {

        Set<String> cells = constructFireDirectionLines(currentWorm.weapon.range)
                .stream()
                .flatMap(Collection::stream)
                .map(cell -> String.format("%d_%d", cell.x, cell.y))
                .collect(Collectors.toSet());

        for (Worm enemyWorm : opponent.worms) {
            String enemyPosition = String.format("%d_%d", enemyWorm.position.x, enemyWorm.position.y);
            if (cells.contains(enemyPosition)) {
                return enemyWorm;
            }
        }

        return null;
    }

    // Shooting line
    private List<List<Cell>> constructFireDirectionLines(int range) {
        List<List<Cell>> directionLines = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            List<Cell> directionLine = new ArrayList<>();
            for (int directionMultiplier = 1; directionMultiplier <= range; directionMultiplier++) {

                int coordinateX = currentWorm.position.x + (directionMultiplier * direction.x);
                int coordinateY = currentWorm.position.y + (directionMultiplier * direction.y);

                if (!isValidCoordinate(coordinateX, coordinateY)) {
                    break;
                }

                if (euclideanDistance(currentWorm.position.x, currentWorm.position.y, coordinateX, coordinateY) > range) {
                    break;
                }

                Cell cell = gameState.map[coordinateY][coordinateX];
                if (cell.type != CellType.AIR) {
                    break;
                }

                directionLine.add(cell);
            }
            directionLines.add(directionLine);
        }

        return directionLines;
    }

    // Buat tau cell2 apa aja yg ada disekitar, dari info gamestate
    private List<Cell> getSurroundingCells(int x, int y) {
        ArrayList<Cell> cells = new ArrayList<>();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                // Gak include current position
                if (i != x && j != y && isValidCoordinate(i, j)) {
                    cells.add(gameState.map[j][i]);
                }
            }
        }

        return cells;
    }

    // Calculate Distance (euclidean)
    private int euclideanDistance(int aX, int aY, int bX, int bY) {
        return (int) (Math.sqrt(Math.pow(aX - bX, 2) + Math.pow(aY - bY, 2)));
    }
    // Checker validity koordinat
    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < gameState.mapSize
                && y >= 0 && y < gameState.mapSize;
    }

    // Arah
    private Direction resolveDirection(Position a, Position b) {
        StringBuilder builder = new StringBuilder();

        int verticalComponent = b.y - a.y;
        int horizontalComponent = b.x - a.x;

        if (verticalComponent < 0) {
            builder.append('N');
        } else if (verticalComponent > 0) {
            builder.append('S');
        }

        if (horizontalComponent < 0) {
            builder.append('W');
        } else if (horizontalComponent > 0) {
            builder.append('E');
        }

        return Direction.valueOf(builder.toString());
    }

    // Cek Ada nearby enemy worm dalam snowball throwing range, mereturn detail worm enemy
    private Worm getFirstWormInSnowballRange() {
        int distance;
        for (Worm enemyWorm : opponent.worms) {
            distance = euclideanDistance(currentWorm.position.x, currentWorm.position.y, enemyWorm.position.x, enemyWorm.position.y);
            if(distance <= currentWorm.snowballs.range && distance > currentWorm.snowballs.freezeRadius*Math.sqrt(2)){
                return enemyWorm;
            }
        }

        return null;
    }

    // Cek Ada nearby enemy worm dalam banana throwing range, mereturn detail worm enemy
    private Worm getFirstWormInBananaRange() {
        int distance;
        for (Worm enemyWorm : opponent.worms) {
            distance = euclideanDistance(currentWorm.position.x, currentWorm.position.y, enemyWorm.position.x, enemyWorm.position.y);
            if(distance <= currentWorm.bananaBombs.range && distance > currentWorm.bananaBombs.damageRadius*0.75){
                return enemyWorm;
            }
        }

        return null;
    }
    
    
}
