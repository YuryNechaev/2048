
import java.util.*;

public class Model {

    int maxTile = 0;
    int score = 0;

    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];

    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();

    private boolean isSaveNeeded = true;

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    //  инициализируем поле и в конструкторе заполняем его объектами Tile с помощью метода resetGamesTiles
    public Model() {
        resetGameTiles();
    }

    private void addTile() {
        List<Tile> list = getEmptyTiles();
        if (!list.isEmpty()) {
            int index = (int) (Math.random() * list.size()) % list.size();
            Tile emptyTile = list.get(index);
            emptyTile.value = Math.random() < 0.9 ? 2 : 4;
        }
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> list = new LinkedList();
        for (int i = 0; i < gameTiles.length; i++)
            for (int j = 0; j < gameTiles[i].length; j++)
                if (gameTiles[i][j].isEmpty())
                    list.add(gameTiles[i][j]);
        return list;

    }

    void resetGameTiles() {
        for (int i = 0; i < gameTiles.length; i++)
            for (int j = 0; j < gameTiles[i].length; j++)
                gameTiles[i][j] = new Tile();

        addTile();
        addTile();
    }

    private boolean compressTiles(Tile[] tiles) {
        int pos = 0;
        boolean result = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (!tiles[i].isEmpty()) {
                if (i != pos) {
                    tiles[pos].value = tiles[i].value;
                    tiles[i].value = 0;
                    result = true;
                }
                pos++;
            }
        }
        return result;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean result = false;
        for (int i = 0; i < FIELD_WIDTH - 1; i++) {
            if (tiles[i].value == tiles[i + 1].value && tiles[i].value != 0) {
                tiles[i].value = tiles[i].value * 2;
                tiles[i + 1].value = 0;
                score = score + tiles[i].value;
                if (tiles[i].value > maxTile)
                    maxTile = tiles[i].value;
                compressTiles(tiles);
                result = true;
            }
        }
        return result;
    }

    public void left() {
        if (isSaveNeeded)
            saveState(gameTiles);

        boolean flag = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                flag = true;
            }
        }
        if (flag)
            addTile();
    }

    public void down() {
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        isSaveNeeded = true;
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
    }

    public void right() {
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        isSaveNeeded = true;
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);

    }

    public void up() {
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        isSaveNeeded = true;
        gameTiles = rotateClockwise(gameTiles);
    }

    public Tile[][] rotateClockwise(Tile[][] mas) {
        int SIDE = mas.length;
        Tile[][] result = new Tile[SIDE][SIDE];

        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                result[i][j] = mas[SIDE - j - 1][i];
            }
        }
        return result;
    }

    public boolean canMove() {
        if (getEmptyTiles().size() != 0)
            return true;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                int x = gameTiles[i][j].value;
                if ((i < FIELD_WIDTH - 1 && x == gameTiles[i + 1][j].value) ||
                        (j < FIELD_WIDTH - 1 && x == gameTiles[i][j + 1].value))
                    return true;
            }
        }
        return false;
    }

    public void saveState(Tile[][] tile) {
        Tile[][] savedTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++)
            for (int j = 0; j < FIELD_WIDTH; j++)
                savedTiles[i][j] = new Tile(tile[i][j].value);
        previousStates.push(savedTiles);
        previousScores.push(score);
        isSaveNeeded = false;

    }

    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    public void randomMove() {
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n) {
            case 0:
                left();
                break;
            case 1:
                right();
                break;
            case 2:
                up();
                break;
            case 3:
                down();
                break;
        }
    }

    public boolean hasBoardChanged() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].value != previousStates.peek()[i][j].value)
                return true;
            }
        }
        return false;
    }

    public MoveEfficiency getMoveEfficiency(Move move){

        MoveEfficiency moveEfficiency = new MoveEfficiency(-1,0,move);
        move.move();
        if(hasBoardChanged())
            moveEfficiency = new MoveEfficiency(getEmptyTiles().size(),score,move);
        rollback();
        return moveEfficiency;
    }

    public void autoMove(){
        PriorityQueue<MoveEfficiency> priorityQueue= new PriorityQueue<>(4,Collections.reverseOrder());

        priorityQueue.offer(getMoveEfficiency(this::left));
        priorityQueue.offer(getMoveEfficiency(this::right));
        priorityQueue.offer(getMoveEfficiency(this::up));
        priorityQueue.offer(getMoveEfficiency(this::down));

        priorityQueue.peek().getMove().move();

    }
}
