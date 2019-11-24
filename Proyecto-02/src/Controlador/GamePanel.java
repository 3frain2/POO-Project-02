/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.BossGenesis;
import Modelo.Disparo;
import Modelo.DisparoEnemy;
import Modelo.DisparoPlayer;
import Modelo.Enemy;
import Modelo.EnemyPhantom;
import Modelo.EnemySalyut;
import Modelo.EnemySkylab;
import Modelo.Explosion;
import Modelo.Player;
import Modelo.SpecialSkills;
import Modelo.Text;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
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
  public static ArrayList<Explosion> explosions;
  public static ArrayList<Text> texts;
  
  private long waveStartTimer;
  private long waveStartTimerDiff;
  private int waveNumber;
  private boolean waveStart;
  private int waveDelay = 2000;
  
  private long slowDownTimer;
  private long slowDownTimerDiff;
  private long slowDownLength = 6000;
  
  private long coolDownTimer;
  private long coolDownTimerAnterior;
  private long coolDownTimerDiff;
  private long coolDownLength = 2;
  
  private boolean pause = false;
  
  private ArrayList<Image> listaBackground = new ArrayList<Image>();
  private int contadorBackground = 0;
  
  private ArrayList<Image> listaSkillCoolDown = new ArrayList<Image>();
  private BufferedImage marcoSkillCoolDown;
  private BufferedImage skillCoolDownOn;
  private int contadorSkillCoolDown = 0;
  
  
  public GamePanel() throws IOException {
    super();
    setPreferredSize(new Dimension(width, height));
    setFocusable(true);
    requestFocus();
    
    //Rotacion de Imagenes del background
    for(int i=0; i < 54; i++) {
      listaBackground.add(ImageIO.read(new File("src/Imagenes/Fondo/"+String.valueOf(i)+".gif")));
    }
    
    //Rotacion de Imagenes del coolDown
    for(int i=0; i < 15; i++) {
      listaSkillCoolDown.add(ImageIO.read(new File("src/Imagenes/skillCoolDown/coolDown"+String.valueOf(i)+".png")));
    }
    
    marcoSkillCoolDown = ImageIO.read(new File("src/Imagenes/skillCoolDown/MarcoCoolDown.png"));
    skillCoolDownOn = ImageIO.read(new File("src/Imagenes/skillCoolDown/coolDownOn.png"));
    corazon = ImageIO.read(new File("src/Imagenes/Herramientas/corazon.png"));
    ataque = ImageIO.read(new File("src/Imagenes/Herramientas/ataque.png"));
    player = new Player();
    
    disparos = new ArrayList<DisparoPlayer>();
    enemigosDisparos = new ArrayList<DisparoEnemy>();
    enemies = new ArrayList<Enemy>();
    skills = new ArrayList<SpecialSkills>();
    explosions = new ArrayList<Explosion>();
    texts = new ArrayList<Text>();
    
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
    coolDownTimer = System.nanoTime();
    
    long startTime;
    long URDTimeMillis;
    long waitTime;
    
    int frameCount = 0;
    int maxFrameCount = 30;
    
    long targetTime = 1000/FPS;
         
    //Game LOOP
    while(running) {
      System.out.println();
      if(pause==false) {
        try {
          startTime = System.nanoTime();
          gameUpdate();
          try {
            gameRender();
          } 
          catch (InterruptedException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
          }
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
    
    //Pantalla de GameOver
    g.setFont(new Font("Century Gothic", Font.PLAIN, 24));
    String gameOver = "- G A M E  O V E R -";
    int length = (int) g.getFontMetrics().getStringBounds(gameOver, g).getWidth(); 
    g.drawString(gameOver, (width-length)/2, height/2);
    
    String deadScore = "- SCORE: "+player.getScore()+" -";
    int lengthScore = (int) g.getFontMetrics().getStringBounds(deadScore, g).getWidth(); 
    g.drawString(deadScore, (width-lengthScore)/2, height/2+40);
    
    gameDraw();
  }
  
  private void gameUpdate() throws IOException {
    //crear wave
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
    
    //player update
    player.update();
    
    //disparo del player update
    for(int i=0; i < disparos.size(); i++) {
      boolean remove = disparos.get(i).update();
      if(remove) {
        disparos.remove(i);
        i--;
      }
    }
    
    //enemigo update
    for(int i=0; i < enemies.size(); i++) {
      enemies.get(i).update();
    }
    
    //disparo del enemigo update
    for(int i=0; i < enemigosDisparos.size(); i++) {
      boolean remove = enemigosDisparos.get(i).update();
      if(remove) {
        enemigosDisparos.remove(i);
        i--;
      }
    }
    
    //skills update
    for(int i=0; i < skills.size(); i++) {
      boolean remove = skills.get(i).update();
      if(remove) {
        skills.remove(i);
        i--;
      }
    }
    
    //explosion update
    for(int i=0; i < explosions.size(); i++) {
      boolean remove = explosions.get(i).update();
      if(remove) {
        explosions.remove(i);
        i--;
      }
    }
    
    //text update
    for(int i=0; i < texts.size(); i++) {
      boolean remove = texts.get(i).update();
      if(remove) {
        texts.remove(i);
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
        double ex2 = e.getX2();
        double ey2 = e.getY2();
        double ex3 = e.getX3();
        double ey3 = e.getY3();
        double er = e.getR();
        
        double dx = bx - ex;
        double dy = by - ey;
        double dx2 = bx - ex2;
        double dy2 = by - ey2;
        double dx3 = bx - ex3;
        double dy3 = by - ey3;
        
        double dist = Math.sqrt(dx * dx + dy * dy);
        double dist2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);
        double dist3 = Math.sqrt(dx3 * dx3 + dy3 * dy3);
        if(dist < br + er || dist2 < br + er || dist3 < br + er && !enemies.get(j).isRecovering()) {
          e.hit();
          disparos.remove(i);
          i--;
          break;
        }
      }
    }
    
    //bullet-player collision
    for(int i=0; i < enemigosDisparos.size(); i++) {
      Disparo b = enemigosDisparos.get(i);
      double bx = b.getX();
      double by = b.getY();
      double br = b.getR();
      
      double ex = player.getX();
      double ey = player.getY();
      double er = player.getR();

      double dx = bx - ex;
      double dy = by - ey;
      double dist = Math.sqrt(dx * dx + dy * dy);

      if(dist < br + er && !player.isRecovering() && !player.isShielded()) {
        player.loseLife();
        enemigosDisparos.remove(i);
        i--;
        break;
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
        double ex2 = e.getX2();
        double ey2 = e.getY2();
        double ex3 = e.getX3();
        double ey3 = e.getY3();
        double er = e.getR();

        double dx = px - ex;
        double dy = py - ey;
        double dx2 = px - ex2;
        double dy2 = py - ey2;
        double dx3 = px - ex3;
        double dy3 = py - ey3;
        
        double dist = Math.sqrt(dx * dx + dy * dy);
        double dist2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);
        double dist3 = Math.sqrt(dx3 * dx3 + dy3 * dy3);
        if(dist < pr + er || dist2 < pr + er || dist3 < pr + er) {
          if(!player.isShielded()) {
            player.loseLife();
          }
        }
      }
    }
    
    //chech Game Over
    if(player.isDead()) {
      running = false;
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
            player.gainDrone();
            texts.add(new Text(player.getX(), player.getY(), 2000, "Drone[ON]"));
          }
          if(type == 2) {
            player.gainATK();
            texts.add(new Text(player.getX(), player.getY(), 2000, "ATK[UP]"));
          }
          if(type == 3) {
            player.gainLife();
            texts.add(new Text(player.getX(), player.getY(), 2000, "HP[UP]"));
          }
          if(type == 4) {
            try {
              AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Sonidos/skillShield.wav"));
              AudioFormat format = audioInputStream.getFormat();
              DataLine.Info info = new DataLine.Info(Clip.class, format);
              Clip sound = (Clip)AudioSystem.getLine(info);
              sound.open(audioInputStream);
              sound.stop();
              sound.setFramePosition(0);
              sound.start();
            } catch (Exception  ex) {
              System.out.println("Sonido shield no encontrado.");
            }
            player.gainShield();
            texts.add(new Text(player.getX(), player.getY(), 2000, "SHIELD[ON]"));
          }
          skills.remove(i);
          i--;
        }
      }
      
      //SlowDown update
      if(slowDownTimer != 0) {
        slowDownTimerDiff = (System.nanoTime() - slowDownTimer) / 1000000;
        if(slowDownTimerDiff > slowDownLength) {
          slowDownTimer = 0;
          for(int j=0; j<enemies.size(); j++) {
            enemies.get(j).setSlow(false);
          }
          for(int j=0; j<enemigosDisparos.size(); j++) {
          enemigosDisparos.get(j).setSlow(false);
        }
        }
      }
    }
      
    //Check enemies dead
    for(int i=0; i < enemies.size(); i++) {
      if(enemies.get(i).isDead()) {
        Enemy e = enemies.get(i);
        
        //Drop SpecialSkills
        double rand = Math.random();
        if(rand < 0.200) {
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
        else if(rand < 0.120) {
          //Shield
          skills.add(new SpecialSkills(4, (int)e.getX(), (int)e.getY()));
        }
        else if(rand < 0.120) {
          //Slow Time
          skills.add(new SpecialSkills(5, (int)e.getX(), (int)e.getY()));
        }
        else {
          skills.add(new SpecialSkills(4, (int)e.getX(), (int)e.getY()));
        }
        
        player.addScore(e.getScoreDead());
        enemies.remove(i);
        i--;
        
        explosions.add(new Explosion(e.getX(), e.getY()));
      }
    }
    
    //enemy invisible
    for(int i=0; i < enemies.size(); i++) {
      Random r = new Random();
      int random = r.nextInt(2000)+1;;
      Enemy e = enemies.get(i);

      if(random == 1) {
        e.gainInvisible();
      }
    }
    
  }
  
  private void gameRender() throws InterruptedException {
    //draw loop background
    if(contadorBackground>53) {
      contadorBackground=0;
    }
 
    //draw slowDown background
    g.drawImage(listaBackground.get(contadorBackground), 0, 0, null);
    if(slowDownTimer == 0) {
      contadorBackground++;
    }
    
    //draw player
    player.draw(g);
    
    //draw disparo player
    for(int i=0; i < disparos.size(); i++) {
      disparos.get(i).draw(g);
    }
    
    //draw enemy
    for(int i=0; i < enemies.size(); i++) {
      enemies.get(i).draw(g);
    }
    
    //draw disparo enemy
    for(int i=0; i < enemigosDisparos.size(); i++) {
      enemigosDisparos.get(i).draw(g);
    }
    
    //draw skills
    for(int i=0; i < skills.size(); i++) {
      skills.get(i).draw(g);
    }
    
    //draw explosions
    for(int i=0; i < explosions.size(); i++) {
      explosions.get(i).draw(g);
    }
    
    //draw text
    for(int i=0; i < texts.size(); i++) {
      texts.get(i).draw(g);
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
    
    //draw slowDown meter
    if(slowDownTimer != 0) {
      g.setColor(Color.white);
      g.drawRect(26, 440, 100, 8);
      g.fillRect(26, 440, 
              (int)(100 - 100.0 * slowDownTimerDiff / slowDownLength), 8);
    }
      
    //draw coolDown timer
    if(coolDownLength != 0) {
      coolDownTimerDiff = (System.nanoTime() - coolDownTimer) / 1000000000;
      
      if(coolDownTimerDiff != coolDownTimerAnterior) {
        texts.add(new Text(125, 580, 1000, "CoolDown["+ coolDownLength+"]"));
        coolDownLength--;
      }
      coolDownTimerAnterior = coolDownTimerDiff;
    }
    else if(coolDownLength == 0.000000) {
      texts.add(new Text(125, 580, 1000, "CoolDown[0]"));
    }
    
    //draw loop coolDown
    if(contadorSkillCoolDown>14) {
      contadorSkillCoolDown=0;
    }
    
    //draw coolDown Icon
    g.drawImage(marcoSkillCoolDown, 26, 460, null);
    if(coolDownLength!=0) {
      g.drawImage(listaSkillCoolDown.get(contadorSkillCoolDown), 36, 470, null);
      if(slowDownTimer == 0) {
        contadorSkillCoolDown++;
      }
    }
    else {
      g.drawImage(skillCoolDownOn, 44, 470, null);
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
          naveEnemiga = new EnemyPhantom();
          //naveEnemiga = new EnemySalyut();
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
    if(waveNumber == 3) {
        naveEnemiga = new BossGenesis();
        enemies.add(naveEnemiga);
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
    if(keyCode == KeyEvent.VK_P || keyCode == KeyEvent.VK_ESCAPE) {
      if(pause == false) {
        pause = true;
      }
      else if(pause == true) {
        pause = false;
      }
    }
    if(keyCode == KeyEvent.VK_A) {
      if(coolDownLength == 0) {
        coolDownLength = 60;
        slowDownTimer = System.nanoTime();
        for(int i=0; i<enemies.size(); i++) {
          enemies.get(i).setSlow(true);
        }
        for(int j=0; j<enemigosDisparos.size(); j++) {
          enemigosDisparos.get(j).setSlow(true);
        }
      }
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
