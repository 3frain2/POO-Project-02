/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Disparo;
import Modelo.DisparoEnemy;
import Modelo.DisparoPlayer;
import Modelo.Enemy;
import Modelo.EnemySalyut;
import Modelo.EnemySkylab;
import Modelo.Player;
import Modelo.SpecialSkills;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author efrai
 */
public class GamePanel extends JPanel implements Runnable, KeyListener {
  public static int width = 600;
  public static int height = 600;
  
  private Thread thread;
  private boolean running;
  
  private BufferedImage image;
  private BufferedImage corazon;
  private BufferedImage ataque;
  
  private Graphics2D g;
  
  private int FPS = 30;
  
  public static Player player;
  public static ArrayList<DisparoPlayer> disparos;
  public static ArrayList<DisparoEnemy> enemigosDisparos;
  public static ArrayList<Enemy> enemies;
  public static ArrayList<SpecialSkills> skills;
  
  private long waveStartTimer;
  private long waveStartTimerDiff;
  private int waveNumber;
  private boolean waveStart;
  private int waveDelay = 2000;
  
  private ArrayList<Image> listaImagen = new ArrayList<Image>();
  private int contadorImagenes = 0;
  
  public GamePanel() throws IOException {
    super();
    setPreferredSize(new Dimension(width, height));
    setFocusable(true);
    requestFocus();
    
    //Rotacion de Imagenes del background
    for(int i=0; i < 54; i++) {
      listaImagen.add(ImageIO.read(new File("src/Imagenes/Fondo/"+String.valueOf(i)+".gif")));
    }
    corazon = ImageIO.read(new File("src/Imagenes/Herramientas/corazon.png"));
    ataque = ImageIO.read(new File("src/Imagenes/Herramientas/ataque.png"));
    player = new Player();
    
    disparos = new ArrayList<DisparoPlayer>();
    enemigosDisparos = new ArrayList<DisparoEnemy>();
    enemies = new ArrayList<Enemy>();
    skills = new ArrayList<SpecialSkills>();

  }
  
  public void addNotify() {
    super.addNotify();
    if(thread == null) {
      thread = new Thread(this);
      thread.start();
    }
    addKeyListener(this);
  }

  @Override
  public void run() {
    running = true;
    
    image = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
    g = (Graphics2D) image.getGraphics();
    
    //Hacer mas suave los movimientos
    g.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING, 
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    
    waveStartTimer = 0;
    waveStartTimerDiff = 0;
    waveStart = true;
    waveNumber = 0;
    
    long startTime;
    long URDTimeMillis;
    long waitTime;
    
    int frameCount = 0;
    int maxFrameCount = 30;
    
    long targetTime = 1000/FPS;
         
    //Game LOOP
    while(running) {
      try {
        startTime = System.nanoTime();
        
        gameUpdate();
        gameRender();
        gameDraw();
        
        URDTimeMillis = ((System.nanoTime() - startTime) / 1000000);
        waitTime = targetTime - URDTimeMillis;
        
        try {
          Thread.sleep(waitTime);
        }
        catch(Exception e) {
        }
        
        frameCount++;
        if(frameCount == maxFrameCount) {
          frameCount = 0;
        }
      }
      catch(IOException ex) {
        Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
  
  private void gameUpdate() throws IOException {
    //crear wake
    if(waveStartTimer == 0 && enemies.size() == 0) {
      waveNumber++;
      waveStart = false;
      waveStartTimer = System.nanoTime();
    }
    else {
      waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000;
      if(waveStartTimerDiff > waveDelay) {
        waveStart = true;
        waveStartTimer = 0;
        waveStartTimerDiff = 0;
      }
    }
    
    //create enemigos
    if(waveStart && enemies.size() == 0) {
      createNewEnemies();
    } 
    
    //jugador update
    player.update();
    
    //disparo del jugador update
    for(int i=0; i < disparos.size(); i++) {
      boolean remove = disparos.get(i).update();
      if(remove) {
        disparos.remove(i);
        i--;
      }
    }
    
    //disparo del enemigo update
    for(int i=0; i < enemigosDisparos.size(); i++) {
      boolean remove = enemigosDisparos.get(i).update();
      if(remove) {
        enemigosDisparos.remove(i);
        i--;
      }
    }
    
    //enemigo update
    for(int i=0; i < enemies.size(); i++) {
      enemies.get(i).update();
    }
    
    //skills update
    for(int i=0; i < skills.size(); i++) {
      boolean remove = skills.get(i).update();
      if(remove) {
        skills.remove(i);
        i--;
      }
    }
    
    //bullet-enemy collision
    for(int i=0; i < disparos.size(); i++) {
      Disparo b = disparos.get(i);
      double bx = b.getX();
      double by = b.getY();
      double br = b.getR();
      
      for(int j=0; j < enemies.size(); j++) {
        Enemy e = enemies.get(j);
        double ex = e.getX();
        double ey = e.getY();
        double er = e.getR();
        
        double dx = bx - ex;
        double dy = by - ey;
        double dist = Math.sqrt(dx * dx + dy * dy);
        
        if(dist < br + er) {
          e.hit();
          disparos.remove(i);
          i--;
          break;
        }
      }
    }
    
    //player-enemy collision
    if(!player.isRecovering()) {
      int px = (int)player.getX();
      int py = (int)player.getY();
      int pr = player.getR();

      for(int i=0; i < enemies.size(); i++) {
        Enemy e = enemies.get(i);
        double ex = e.getX();
        double ey = e.getY();
        double er = e.getR();

        double dx = px - ex;
        double dy = py - ey;
        double dist = Math.sqrt(dx * dx + dy * dy);
        
        if(dist < pr + er) {
          player.loseLife();
        }
      }
    }
    
    //player-skill collision
    if(!player.isRecovering()) {
      int px = (int)player.getX();
      int py = (int)player.getY();
      int pr = player.getR();

      for(int i=0; i < skills.size(); i++) {
        SpecialSkills k = skills.get(i);
        double x = k.getX();
        double y = k.getY();
        double r = k.getR();

        double dx = px - x;
        double dy = py - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        
        if(dist < pr + r) {
          int type = k.getType();
          
          if(type == 1) {
          
          }
          if(type == 2) {
            player.gainATK();
          }
          if(type == 3) {
            player.gainLife();
          }
          
          skills.remove(i);
          i--;
        }
      }
    }
      
    //Check enemies dead
    for(int i=0; i < enemies.size(); i++) {
      if(enemies.get(i).isDead()) {
        Enemy e = enemies.get(i);
        
        //Drop SpecialSkills
        double rand = Math.random();
        if(rand < 0.001) {
          //Drone
          skills.add(new SpecialSkills(1, (int)e.getX(), (int)e.getY()));
        }
        else if(rand < 0.020) {
          //ATK
          skills.add(new SpecialSkills(2, (int)e.getX(), (int)e.getY()));
        }
        else if(rand < 0.120) {
          //HP
          skills.add(new SpecialSkills(3, (int)e.getX(), (int)e.getY()));
        }
        else {skills.add(new SpecialSkills(2, (int)e.getX(), (int)e.getY()));}
        
        player.addScore(e.getScoreDead());
        enemies.remove(i);
        i--;
      }
    }
    
  }
  
  private void gameRender() {
    //draw fondo
    if(contadorImagenes>53) {
      contadorImagenes=0;
    }
    g.drawImage(listaImagen.get(contadorImagenes), 0, 0, null);
    contadorImagenes++;
    
    //draw jugador
    player.draw(g);
    
    //draw disparo Jugador
    for(int i=0; i < disparos.size(); i++) {
      disparos.get(i).draw(g);
    }
    
    //draw enemigo
    for(int i=0; i < enemies.size(); i++) {
      enemies.get(i).draw(g);
    }
    
    //draw skills
    for(int i=0; i < skills.size(); i++) {
      skills.get(i).draw(g);
    }
    
    //draw wave Number
    if(waveStartTimer != 0) {
      g.setFont(new Font("Century Gothic", Font.PLAIN, 22));
      String stageText = "- S T A G E  " + waveNumber + "  -";
      int length = (int) g.getFontMetrics().getStringBounds(stageText, g).getWidth();
      int alpha = (int)(255 * Math.sin(3.14 * waveStartTimerDiff / waveDelay));
      if (alpha > 255) alpha = 255;
      g.setColor(new Color(255, 255, 255, alpha));
      g.drawString(stageText, width/2 - length/2, height/2);
    }
    
    //draw player HP
    for(int i=0; i < player.getHp(); i++) {
      g.drawImage(corazon, 20 + (40 * i), 20, null);
    }
    
    //draw player ATK
    for(int i=0; i < player.getATK(); i++) {
      g.drawImage(ataque, 20 + (40 * i), 60, null);
    }
    
    //draw score
    g.setColor(Color.WHITE);
    g.setFont(new Font("Century Gothic", Font.PLAIN, 20));
    g.drawString("Score: " + player.getScore(), width - 130, 35);
    
    
    //draw disparo Enemigo
    for(int i=0; i < enemigosDisparos.size(); i++) {
      enemigosDisparos.get(i).draw(g);
    }

  }
  
  private void gameDraw() {
    Graphics g2 = this.getGraphics();
    g2.drawImage(image, 0, 0, null);
    g2.dispose();
  }
  
  //Create enemies
  private void createNewEnemies() throws IOException {
    enemies.clear();
    Enemy naveEnemiga = null;
    if(waveNumber == 1) {
      for (int i=0; i < 25;i++) {
        //int randomNave = new Random().nextInt(2);
        int randomNave = 0;
        if(randomNave == 0) {
          naveEnemiga = new EnemySalyut();
        }
        if(randomNave == 1) {
          naveEnemiga = new EnemySkylab();
        }
        enemies.add(naveEnemiga);
      }
    }
    if(waveNumber == 2) {
      for (int i=0; i < 2;i++) {
        naveEnemiga = new EnemySkylab();
        enemies.add(naveEnemiga);
      }
    }
    
    
  }
  
  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyPressed(KeyEvent e) {
    int keyCode = e.getKeyCode();
    
    if(keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_NUMPAD1) {
      player.setLeft(true);
    }
    if(keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_NUMPAD3) {
      player.setRight(true);
    }
    if(keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_NUMPAD5) {
      player.setUp(true);
    }
    if(keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_NUMPAD2) {
      player.setDown(true);
    }
    if(keyCode == KeyEvent.VK_Z) {
      player.setFiring(true);
    }
    if(keyCode == KeyEvent.VK_A) {
      enemies.get(0).setFiring(true);
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    int keyCode = e.getKeyCode();
    if(keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_NUMPAD1) {
      player.setLeft(false);
    }
    if(keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_NUMPAD3) {
      player.setRight(false);
    }
    if(keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_NUMPAD5) {
      player.setUp(false);
    }
    if(keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_NUMPAD2) {
      player.setDown(false);
    }
    if(keyCode == KeyEvent.VK_Z) {
      player.setFiring(false);
    }
  }
}
