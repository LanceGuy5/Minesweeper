import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable{

    public static final int REAL_WIDTH = 816, REAL_HEIGHT = 839;
    public static final int WIDTH = 800, HEIGHT = 800;
    public String name = "Minesweeper";

    //Thread information
    boolean isRunning = false;
    Thread running;

    //Objects
    Minesweeper m;

    public Game(){
        new Window(REAL_WIDTH, REAL_HEIGHT, name, this);
        running = new Thread(this);
        start();
    }

    public synchronized void start(){
        if(isRunning) return;
        running.start();
        isRunning = true;
    }

    public synchronized void stop(){
        if(!isRunning) return;
        try{
            running.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        isRunning = false;
    }

    public void init(){
        m = new Minesweeper();

        this.addMouseListener(m);
    }

    @Override
    public void run() {
        Thread init = new Thread(this::init);
        init.start();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int updates = 0;
        int frames = 0;
        while(isRunning){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1){
                tick();
                updates++;
                delta--;
            }
            render();
            frames++;

            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                frames = 0;
                updates = 0;
            }
        }
    }

    public void tick(){
        m.tick();
    }

    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            bs = this.getBufferStrategy();
        }
        Graphics g = bs.getDrawGraphics();

        /*RENDER BETWEEN HERE*/

        //background
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        m.render(g);
        /*AND HERE*/

        bs.show();
        g.dispose();
    }
}
