import java.awt.*;
import java.awt.image.BufferStrategy;

/**
 * Class to control every other class
 * @see java.awt.Canvas
 * @see java.awt.Component
 * @see java.lang.Runnable
 */
public class TicTacToe extends Canvas implements Runnable{

    /**
     * General Information
     */
    public static final int WIDTH = 800, HEIGHT = 608;
    String title = "Tic Tac Toe";

    /**
     * Thread List Below
     */
    Thread t = new Thread(this);
    boolean isRunning;
//    Runnable initializeRunner = this::initialize;
//    Thread init = new Thread(initializeRunner);

    /**
     * Enum list below
     */
    enum GAMESTATE{
        Menu,
        Game
    }

    /**
     * Object List Below
     */
    public GameDirector gd;
    Menu menu;

    public static GAMESTATE state;

    public TicTacToe(){
        new Window(WIDTH, HEIGHT, title, this);
        state = GAMESTATE.Menu;
        initialize();
        start();
    }

    public synchronized void start(){
        if(isRunning) return;
        t.start();
        isRunning = true;
    }

    public synchronized void stop(){
        if(!isRunning) return;
        try {
            t.join();
        }catch(InterruptedException e){
            System.out.println("Error stopping thread");
        }
        isRunning = false;
    }

    private synchronized void initialize(){
        gd = new GameDirector();
        gd.setNumberOfPlayers(2);
        this.addMouseListener(gd);
        menu = new Menu(this, gd);
        this.addMouseListener(menu);
    }

    @Override
    public void run() {
        while(isRunning){
            this.requestFocus();
            long lastTime = System.nanoTime();
            double amountOfTicks = 60.0;
            double ns = 1000000000 / amountOfTicks;
            double delta = 0;
            long timer = System.currentTimeMillis();
            int frames = 0;
            while(isRunning) {
                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;
                while(delta >= 1) {
                    tick();
                    frames++;
                    delta--;
                }
                render();
                frames++;
                if(System.currentTimeMillis() - timer > 1000) {
                    timer += 1000;
//                    System.out.println(frames);
                    frames = 0;
                }
            }
            stop();
        }
    }

    public void tick(){
        if(state.equals(GAMESTATE.Game)) {
            gd.tick();
        }else if(state.equals(GAMESTATE.Menu)){
            menu.tick();
        }else{
            throw new IllegalStateException("Not valid state");
        }
    }

    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        try {
            if (bs == null) {
                this.createBufferStrategy(3);
                bs = this.getBufferStrategy();
            }
        }catch(IllegalStateException ignored){
            return;
        }

        assert bs != null;
        Graphics g = bs.getDrawGraphics();
        //RENDER BETWEEN HERE
        g.setColor(Color.gray);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        if(gd == null || menu == null){
            return;
        }
        if(state.equals(GAMESTATE.Game)) {
            gd.render(g);
        }else if(state.equals(GAMESTATE.Menu)){
            menu.render(g);
        }else{
            throw new IllegalStateException("Not valid state");
        }

        //AND HERE

        bs.show();
        g.dispose();
    }
}
