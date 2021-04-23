
public class Player {

    //Objects
    GameDirector gd;
    GameDirector.PlayerType p;
    GameDirector.SpaceType st;

    boolean isTurn = false;

    /**
     * Player constructor
     * @param type 0 for human, 1 for AI
     */
    public Player(int type, GameDirector gd, GameDirector.SpaceType st){
        this.gd = gd;
        assert type == 0 || type == 1;
        if(type == 0) {
            p = GameDirector.PlayerType.human;
        }else{
            p = GameDirector.PlayerType.robot;
        }
        assert st != GameDirector.SpaceType.blank;
        this.st = st;
    }

    public void playHuman(GameDirector.SpaceType[][] board, int boardRow, int boardCol){
        if(board[boardRow][boardCol].equals(GameDirector.SpaceType.blank)){
            board[boardRow][boardCol] = this.st;
            this.setIsTurn(false);
        }
    }

    public void playAI(){

    }

    public boolean getIsTurn(){
        return isTurn;
    }

    public void setIsTurn(boolean val){
        isTurn = val;
    }
}
