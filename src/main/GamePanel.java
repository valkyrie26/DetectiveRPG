package main;

import entity.Player;
import object.SuperObject;
import tile.Tile;
import tile.TileManager;

import javax.swing.JPanel;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{

    //Screen settings
    final int originalTileSize = 16; //16 by 16 tiles
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; //48 by 48 tile
    public final int maxScreenColumn = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenColumn; //768 pixels
    public final int screenHeight = tileSize * maxScreenRow; //576 pixels

    //World settings
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;

    //FPS to restrict updation
    int FPS =60;

    //System
    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler();
    Sound music = new Sound();
    Sound SE = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    Thread gameThread;

    //Entity and object
    public Player player = new Player(this,keyH);

    //can display up to 10 objects at the same time
    public SuperObject obj[] = new SuperObject[10];

    //constructor
    public GamePanel()
    {
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); //improves rendering performance
        this.addKeyListener(keyH);
        this.setFocusable(true);

    }

    public void setupGame()
    {
        aSetter.setObject();
        playMusic(0);
    }

    public void startGameThread()
    {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run()
    {
        double drawInterval = 1000000000/FPS; //0.016666 seconds
        double nextDrawTime = System.nanoTime() + drawInterval;

        //automatically called by thread on start
        while(gameThread != null)
        {
            //1. Update information like character positions
            update();

            //2. Draw the screen with updated info
            repaint(); //calls paintcomponent method


            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime/1000000; //sleep takes time in mili, so convert nano to mili

                if(remainingTime < 0)
                {
                    remainingTime = 0;
                }

                Thread.sleep((long)remainingTime);

                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void update()
    {
        player.update();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        //debug
        long drawStart = 0;
        if(keyH.checkDrawTime == true)
        {
            drawStart = System.nanoTime();
        }

        //draw tiles
        tileM.draw(g2);

        //draw objects
        for(int i=0; i< obj.length; i++)
        {
            if(obj[i]!= null)
            {
                obj[i].draw(g2, this);
            }
        }

        //draw player
        player.draw(g2);

        //UI
        ui.draw(g2);

        //Debug
        if(keyH.checkDrawTime == true)
        {
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;
            g2.setColor(Color.white);
            g2.drawString("Draw Time: "+passed, 10, 400);
            System.out.println("Draw Time: "+passed);
        }


        g2.dispose();
    }

    public void playMusic(int i)
    {
        music.setFile(i);
        music.play();
        music.loop();
    }
    public void stopMusic(){
        music.stop();
    }
    public void playSE(int i)
    {
        SE.setFile(i);
        SE.play();
    }
}
