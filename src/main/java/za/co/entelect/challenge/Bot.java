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
    private MyPlayer currPlayer;

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gameState = gameState;
        this.opponent = gameState.opponents[0];
        this.currentWorm = getCurrentWorm(gameState);
        this.currPlayer = gameState.myPlayer;
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
        // Worm enemyWorm;
        // Direction direction;
        // List<Cell> surroundingBlocks;
        // int cellIdx;
        // Cell block;
        
        Worm enemyWorm,tempEnemy;
        Direction direction, direction1,direction2,direction3;
        List<Cell> surroundingBlocks;
        int cellIdx;
        Cell block;
        int tempJarak;
        int jarak = 99999;
        MyWorm allWorm;
        allWorm = null;
        if(currPlayer.remainingWormSelections > 0 ){
            enemyWorm = getFirstWormInRange(currentWorm);
            for (MyWorm curWorm : currPlayer.worms){
                if(curWorm.health > 0 && curWorm != currentWorm){
                    tempEnemy =  getFirstWormInRange(curWorm);
                    if(tempEnemy!=null){
                        tempJarak = euclideanDistance(curWorm.position.x, curWorm.position.y, tempEnemy.position.x, tempEnemy.position.y);
                        if(tempJarak < jarak){
                            jarak = tempJarak;
                            allWorm = curWorm;
                            
    
                        }
                    }
                }
            }
            if(allWorm!=null){
            if(allWorm.id == 3){
                enemyWorm = getFirstWormInSnowballRange(allWorm);
                if (enemyWorm != null && allWorm.snowballs.count > 0 /*&& enemyWorm.roundsUntilUnfrozen == 0*/) { // Kalo musuh di range snowballsb dan count snowballs > 0 berarti do command snowballs
                    return new SelectCommand(3, "snowball", enemyWorm.position.x, enemyWorm.position.y, Direction.NE);
                    // SelectCommand(int id, String cmd,int x,int y,Direction direction)
                }
                enemyWorm = getFirstWormInRange(allWorm);
                if (enemyWorm != null) {  // Musuh di range shoot general weapon
                    direction3 = resolveDirection(allWorm.position, enemyWorm.position);
                    return new SelectCommand(3, "shoot", enemyWorm.position.x, enemyWorm.position.y, direction3);
                }                  
            }
            else if(allWorm.id == 2){
                enemyWorm = getFirstWormInBananaRange(allWorm);
                if (enemyWorm != null && allWorm.bananaBombs.count > 0) { // Kalo musuh di range banana bomb dan count bomb > 0 berarti do command bananabomb
                    return new SelectCommand(2, "banana", enemyWorm.position.x, enemyWorm.position.y, Direction.NE);
                }
                enemyWorm = getFirstWormInRange(allWorm);
                if (enemyWorm != null) {  // Musuh di range shoot general weapon
                    direction2 = resolveDirection(allWorm.position, enemyWorm.position);
                    return new SelectCommand(2, "shoot", enemyWorm.position.x, enemyWorm.position.y, direction2);
                }
            }
            else if(allWorm.id == 1){
                enemyWorm = getFirstWormInRange(allWorm);
                if (enemyWorm != null) {
                    direction1 = resolveDirection(allWorm.position, enemyWorm.position);
                    return new SelectCommand(1, "shoot", enemyWorm.position.x, enemyWorm.position.y, direction1);
                }
            }
        }
                
            
        }

        // Run case tergantung jenis worm
        else if(currentWorm.id == 1){  // Commando
            enemyWorm = getFirstWormInRange(currentWorm);
            if (enemyWorm != null) {
                direction = resolveDirection(currentWorm.position, enemyWorm.position);
                return new  ShootCommand(direction);
            }

        }
        else if(currentWorm.id == 2){ //Agent
            enemyWorm = getFirstWormInBananaRange(currentWorm);
            if (enemyWorm != null && currentWorm.bananaBombs.count > 0) { // Kalo musuh di range banana bomb dan count bomb > 0 berarti do command bananabomb
                return new BananaCommand(enemyWorm.position.x,enemyWorm.position.y);
            }
            enemyWorm = getFirstWormInRange(currentWorm); 
            if (enemyWorm != null) {  // Musuh di range shoot general weapon
                direction = resolveDirection(currentWorm.position, enemyWorm.position);
                return new ShootCommand(direction);
            }

        }

        else if(currentWorm.id == 3){ // Technologist
            enemyWorm = getFirstWormInSnowballRange(currentWorm);
            if (enemyWorm != null && currentWorm.snowballs.count > 0 && enemyWorm.roundsUntilUnfrozen == 0) { // Kalo musuh di range snowballsb dan count snowballs > 0 berarti do command snowballs
                return new SnowballCommand(enemyWorm.position.x,enemyWorm.position.y);
            }
            enemyWorm = getFirstWormInRange(currentWorm);
            if (enemyWorm != null) {  // Musuh di range shoot general weapon
                direction = resolveDirection(currentWorm.position, enemyWorm.position);
                return new ShootCommand(direction);
            }

        }


        block = getCell(currentWorm);
        if(block!=null){
            if (block.type == CellType.DIRT) {  // Kalo block air maka move kesana Kalo dirt berarti didestroy
                return new DigCommand(block.x, block.y);
            }      
            else if (block.type == CellType.AIR) { 
                return new MoveCommand(block.x, block.y);
            }
        }



        return new DoNothingCommand();

    }


    // Cek Ada nearby enemy worm dalam shooting range, mereturn detail worm enemy
    private Worm getFirstWormInRange(MyWorm thisWorm) {

        Set<String> cells = constructFireDirectionLines(thisWorm,thisWorm.weapon.range)
                .stream()
                .flatMap(Collection::stream)
                .map(cell -> String.format("%d_%d", cell.x, cell.y))
                .collect(Collectors.toSet());

        ArrayList<Worm> arrWorm = new ArrayList<Worm>();
        for (Worm enemyWorm : opponent.worms) {
            if(enemyWorm.health > 0){
                String enemyPosition = String.format("%d_%d", enemyWorm.position.x, enemyWorm.position.y);
                if (cells.contains(enemyPosition)) {
                    arrWorm.add(enemyWorm);
                }
            }
        }
        if(arrWorm.size() > 0){
            Worm tempWorm = arrWorm.get(0);
            for (int i = 1; i < arrWorm.size(); i++) {
                if(arrWorm.get(i).health < tempWorm.health) tempWorm = arrWorm.get(i);
            }
            
            return tempWorm;
        }
        else return null;
    }

    // Shooting line, nentuin direction shooting line dari weapon
    private List<List<Cell>> constructFireDirectionLines(MyWorm thisWorm,int range) {
        List<List<Cell>> directionLines = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            List<Cell> directionLine = new ArrayList<>();
            for (int directionMultiplier = 1; directionMultiplier <= range; directionMultiplier++) {

                int coordinateX = thisWorm.position.x + (directionMultiplier * direction.x);
                int coordinateY = thisWorm.position.y + (directionMultiplier * direction.y);

                if (!isValidCoordinate(coordinateX, coordinateY)) {
                    break;
                }

                if (euclideanDistance(thisWorm.position.x, thisWorm.position.y, coordinateX, coordinateY) > range) {
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

    // Buat array of cell2 apa aja yg ada disekitar, dari info gamestate. Bakal dipakai buat nentuin jenis cell disekitar (di method run)
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

    // Converter Posisi antara 2 koordinat menjadi suatu arah (mata angin) , useful buat nentuin shooting direction buat weapon
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
    private Worm getFirstWormInSnowballRange(MyWorm thisWorm) {
        int distance;
        for (Worm enemyWorm : opponent.worms) {
            if(enemyWorm.health > 0){
                distance = euclideanDistance(thisWorm.position.x, thisWorm.position.y, enemyWorm.position.x, enemyWorm.position.y);
                if(distance <= thisWorm.snowballs.range && distance > thisWorm.snowballs.freezeRadius*Math.sqrt(2)){
                    return enemyWorm;
                }
            }
        }

        return null;
    }

    // Cek Ada nearby enemy worm dalam banana throwing range, mereturn detail worm enemy
    private Worm getFirstWormInBananaRange(MyWorm thisWorm) {
        int distance;
        for (Worm enemyWorm : opponent.worms) {
            if(enemyWorm.health > 0){
                distance = euclideanDistance(thisWorm.position.x, thisWorm.position.y, enemyWorm.position.x, enemyWorm.position.y);
                if(distance <= thisWorm.bananaBombs.range && distance > thisWorm.bananaBombs.damageRadius*0.75){
                    return enemyWorm;
                }
            }
        }

        return null;
    }
    


    /* Strategi : cari worm musuh yg dapat diserang semua worm yang player miliki */
    // private Command greedyByEnemyLocation(){
    //     Worm enemyWorm;
    //     Direction direction;
    //     List<Cell> surroundingBlocks;
    //     int cellIdx;
    //     Cell block;
    //     Direction direction1,direction2,direction3;
    //     boolean found3 = false;
    //     boolean found2 = false;
    //     for (MyWorm allWorm : currPlayer.wo  rms){
    //         if(allWorm.health > 0 && allWorm != currentWorm){
    //             if(allWorm.id == 3){
    //                 enemyWorm = getFirstWormInSnowballRange(allWorm);
    //                 if (enemyWorm != null && allWorm.snowballs.count > 0 && enemyWorm.roundsUntilUnfrozen == 0) { // Kalo musuh di range snowballsb dan count snowballs > 0 berarti do command snowballs
    //                     found3 = true;
    //                     return new SelectCommand(3, "snowball", enemyWorm.position.x, enemyWorm.position.y, Direction.NE);
    //                     // SelectCommand(int id, String cmd,int x,int y,Direction direction)
    //                 }
    //                 enemyWorm = getFirstWormInRange(allWorm);
    //                 if (enemyWorm != null) {  // Musuh di range shoot general weapon
    //                     direction3 = resolveDirection(allWorm.position, enemyWorm.position);

    //                 }                  
    //             }
    //             else if(allWorm.id == 2){
    //                 enemyWorm = getFirstWormInBananaRange(allWorm);
    //                 if (enemyWorm != null && allWorm.bananaBombs.count > 0) { // Kalo musuh di range banana bomb dan count bomb > 0 berarti do command bananabomb
    //                     found2 = true;
    //                 }
    //                 enemyWorm = getFirstWormInRange(allWorm); 
    //                 if (enemyWorm != null) {  // Musuh di range shoot general weapon
    //                     direction2 = resolveDirection(allWorm.position, enemyWorm.position);
                    
    //                 }
    //             }
    //             else if(allWorm.id == 1){
    //                 enemyWorm = getFirstWormInRange(allWorm);
    //                 if (enemyWorm != null) {
    //                     direction1 = resolveDirection(allWorm.position, enemyWorm.position);
    //                 }
    //             }
                
    //         }
    //     }

    //     if(!found3 && found2) return new SelectCommand(2, "banana", enemyWorm.position.x, enemyWorm.position.y, Direction.NE);
    //     return null;


    // }
    /*if(currPlayer.remainingWormSelections > 0){
        Direction direction1,direction2,direction3;
        boolean found3 = false;
        boolean found2 = false;
        enemyWorm = getFirstWormInRange(currentWorm);
        for (MyWorm allWorm : currPlayer.worms){
            if(allWorm.health > 0 && allWorm != currentWorm){
                if(allWorm.id == 3){
                    enemyWorm = getFirstWormInSnowballRange(allWorm);
                    if (enemyWorm != null && allWorm.snowballs.count > 0 && enemyWorm.roundsUntilUnfrozen == 0) { // Kalo musuh di range snowballsb dan count snowballs > 0 berarti do command snowballs
                        found3 = true;
                        return new SelectCommand(3, "snowball", enemyWorm.position.x, enemyWorm.position.y, Direction.NE);
                        // SelectCommand(int id, String cmd,int x,int y,Direction direction)
                    }
                    enemyWorm = getFirstWormInRange(allWorm);
                    if (enemyWorm != null) {  // Musuh di range shoot general weapon
                        direction3 = resolveDirection(allWorm.position, enemyWorm.position);

                    }                  
                }
                else if(allWorm.id == 2){
                    enemyWorm = getFirstWormInBananaRange(allWorm);
                    if (enemyWorm != null && allWorm.bananaBombs.count > 0) { // Kalo musuh di range banana bomb dan count bomb > 0 berarti do command bananabomb
                        found2 = true;
                    }
                    enemyWorm = getFirstWormInRange(allWorm); 
                    if (enemyWorm != null) {  // Musuh di range shoot general weapon
                        direction2 = resolveDirection(allWorm.position, enemyWorm.position);
                    
                    }
                }
                else if(allWorm.id == 1){
                    enemyWorm = getFirstWormInRange(allWorm);
                    if (enemyWorm != null) {
                        direction1 = resolveDirection(allWorm.position, enemyWorm.position);
                    }
                }
                
            }
        }

        if(!found3 && found2) return new SelectCommand(2, "banana", enemyWorm.position.x, enemyWorm.position.y, Direction.NE);
    }*/
    
    
    private Cell getCell(MyWorm thisWorm){ // get Cell dideket worm , not random. Berdasarkan poin (dig dirt didahulukan dibanding move)
        List<Cell> surroundingBlocks;
        int cellIdx;
        Cell block;
        int countE = 0;
        int countF = 0;
        int countEAlive = countEnemyAlive();
        int CountFAlive = countFriendAlive();
        ArrayList<Position> enemyPos = new ArrayList<Position>();
        ArrayList<Position> friendPos = new ArrayList<Position>();
        enemyPos = getEnemyPosition();
        friendPos = getFriendPosition();
        // enemyPos.get(i)= (x,y)
        surroundingBlocks = getSurroundingCells(thisWorm.position.x, thisWorm.position.y);
        //cellIdx = random.nextInt(surroundingBlocks.size()); // Move nya masih random
        //block = surroundingBlocks.get(cellIdx);
        for (int i = 0; i < surroundingBlocks.size(); i++) {
            block = surroundingBlocks.get(i);
            if(block.type == CellType.DIRT){
                return block;
            }
        }
        for (int k = 0; k < surroundingBlocks.size(); k++) {
            block = surroundingBlocks.get(k);
            if (block.type == CellType.AIR){
                for (int j = 0; j < enemyPos.size(); j++) {
                    if(block.x != enemyPos.get(j).x && block.y != enemyPos.get(j).y){
                        countE++; // mayat msh kehitung            
                    }
                }
                for (int l = 0; l < friendPos.size(); l++) {
                    if(block.x != friendPos.get(l).x && block.y != friendPos.get(l).y){
                        countF++;
                    }                    
                }
                if(countE >= countEAlive && countF >= CountFAlive) {
                    return block;
                }
            }
        }
            return null;
        } 


    private ArrayList<Position> getEnemyPosition(){ // get posisi semua musuh (berbentuk arraylist)
        ArrayList<Position> arrWorm = new ArrayList<Position>();
        for (Worm enemyWorm : opponent.worms) {
            arrWorm.add(enemyWorm.position);
        }
        return arrWorm;
    }

    private ArrayList<Position> getFriendPosition(){ // get posisi semua friends (berbentuk arraylist)
        ArrayList<Position> arrWorm = new ArrayList<Position>();
        for (MyWorm myWorm : currPlayer.worms) {
            arrWorm.add(myWorm.position);
        }
        return arrWorm;
    }

    
    // Mengembalikan jumlah worms enemy yang masih hidup
    private int countEnemyAlive(){
        int count = 0;
        for (Worm enemyWorm : opponent.worms) {
            if(enemyWorm.health > 0) count++;
        }
        return count;
    }
    // Mengembalikan jumlah worms friends yang masih hidup
    private int countFriendAlive(){
        int count = 0;
        for (MyWorm myWorm : currPlayer.worms) {
            if(myWorm.health > 0) count++;
        }
        return count;
    }

    // /*private Asal(){
    //     Worm enemyWorm,tempEnemy;
    //     Direction direction, direction1,direction2,direction3;
    //     List<Cell> surroundingBlocks;
    //     int cellIdx;
    //     Cell block;
    //     int tempJarak;
    //     int jarak = 99999;

    //     if(currPlayer.remainingWormSelections > 0 ){
    //         enemyWorm = getFirstWormInRange(currentWorm);
    //         for (MyWorm allWorm : currPlayer.worms){
    //             if(allWorm.health > 0 && allWorm != currentWorm){
    //                 tempEnemy =  getFirstWormInRange(allWorm);
    //                 if(tempEnemy!=null){
    //                     tempJarak = euclideanDistance(allWorm.position.x, allWorm.position.y, tempEnemy.position.x, tempEnemy.position.y);
    //                     if(tempJarak < jarak){
    //                         jarak = tempJarak;
    //                         enemyWorm = tempEnemy;
    
    //                     }
    //                 }
    //             }
    //         }
    //         if(allWorm.id == 3){
    //             enemyWorm = getFirstWormInSnowballRange(allWorm);
    //             if (enemyWorm != null && allWorm.snowballs.count > 0 /*&& enemyWorm.roundsUntilUnfrozen == 0*/) { // Kalo musuh di range snowballsb dan count snowballs > 0 berarti do command snowballs
    //                 return new SelectCommand(3, "snowball", enemyWorm.position.x, enemyWorm.position.y, Direction.NE);
    //                 // SelectCommand(int id, String cmd,int x,int y,Direction direction)
    //             }
    //             if (enemyWorm != null) {  // Musuh di range shoot general weapon
    //                 direction3 = resolveDirection(allWorm.position, enemyWorm.position);
    //                 return new SelectCommand(3, "shoot", enemyWorm.position.x, enemyWorm.position.y, direction3);
    //             }                  
    //         }
    //         else if(allWorm.id == 2){
    //             enemyWorm = getFirstWormInBananaRange(allWorm);
    //             if (enemyWorm != null && allWorm.bananaBombs.count > 0) { // Kalo musuh di range banana bomb dan count bomb > 0 berarti do command bananabomb
    //                 return new SelectCommand(2, "banana", enemyWorm.position.x, enemyWorm.position.y, Direction.NE);
    //             }
    //             if (enemyWorm != null) {  // Musuh di range shoot general weapon
    //                 direction2 = resolveDirection(allWorm.position, enemyWorm.position);
    //                 return new SelectCommand(2, "shoot", enemyWorm.position.x, enemyWorm.position.y, direction2);
    //             }
    //         }
    //         else if(allWorm.id == 1){
    //             if (enemyWorm != null) {
    //                 direction1 = resolveDirection(allWorm.position, enemyWorm.position);
    //                 return new SelectCommand(1, "shoot", enemyWorm.position.x, enemyWorm.position.y, direction1);
    //             }
    //         }
                
            
    //     }
        

    // }





}

