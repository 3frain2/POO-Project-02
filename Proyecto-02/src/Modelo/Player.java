/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;
import Controlador.GamePanel;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

/**
 *
 * @author efrai
 */

public class Player {
  private int x;
  private int y;
  int r;
  int naveAncho = 55;
  int naveLargo = 40;
  
  private int dx;
  private int dy;
  private int speed;
  
  private boolean up;
  private boolean down;
  private boolean left;
  private boolean right;
  
  private boolean firing;
  private long firingTimer;
  private long firingDeley;
  
  private boolean recovering;
  private long recoveryTimer;
  private int hp;
  
  private BufferedImage nave;
  private BufferedImage naveDerecha;
  private BufferedImage naveIzquierda;
  
  private BufferedImage naveHit;
  private BufferedImage naveDerechaHit;
  private BufferedImage naveIzquierdaHit;
  
  private ArrayList<Image> listaDrone = new ArrayList<Image>();
  private int contadorDrone;
  
  private ArrayList<Image> listaShield = new ArrayList<Image>();
  private int contadorShield;
  
  private int score;
  private int ATKLevel;
  private int ATK;

  private int drone;
  private boolean shielded;
  private long shieldTimer;
  
  public Player() throws IOException {
    x = GamePanel.width/2;
    y = GamePanel.height/2;
    r = 25;
    dx = 0;
    dy = 0;
    speed = 5;
    
    hp = 3;
    drone = 0;
   
    recovering = false;
    recoveryTimer = 0;
    
    shielded = false;
    shieldTimer = 0;
    
    nave = ImageIO.read(new File("src/Imagenes/Nave/nave.png"));
    naveDerecha = ImageIO.read(new File("src/Imagenes/Nave/naveDerecha.png"));
    naveIzquierda = ImageIO.read(new File("src/Imagenes/Nave/naveIzquierda.png"));
    
    naveHit = ImageIO.read(new File("src/Imagenes/Nave/naveHit.png"));
    naveDerechaHit = ImageIO.read(new File("src/Imagenes/Nave/naveDerechaHit.png"));
    naveIzquierdaHit = ImageIO.read(new File("src/Imagenes/Nave/naveIzquierdaHit.png"));
    
    for(int i=0; i < 6; i++) {
      listaDrone.add(ImageIO.read(new File("src/Imagenes/Drone/drone"+String.valueOf(i)+".png")));
    }
    contadorDrone = 0;
    
    for(int i=0; i < 6; i++) {
      listaShield.add(ImageIO.read(new File("src/Imagenes/Shield/shield"+String.valueOf(i)+".png")));
    }
    contadorShield = 0;

    score = 0;
    
    firing = false;
    firingTimer = System.nanoTime();
    firingDeley = 200;
  }
  
  public double getX() {return x;}
  public double getY() {return y;}
  public int getR() {return r;}
  public int getScore() {return score;}
  
  public int getHp() {return hp;}
  public int getATKLevel() {return ATKLevel;}
  public int getATK() {return ATK;}
  
  public boolean isRecovering() {return recovering;}
  public boolean isShielded() {return shielded;}
  public boolean isDead() {return hp<=0;}
  public int getContadorDrone() {return contadorDrone;}

  public void setLeft(boolean b) {left = b;}
  public void setRight(boolean b) {right = b;}
  public void setUp(boolean b) {up = b;}
  public void setDown(boolean b) {down = b;}

  public void addScore(int score) {this.score += score;}
  public void setFiring(boolean b) {firing = b;}
  
  public void gainATK() {
    if(ATK < 5) {
      ATK += 1;
    }
  }
  
  public void gainLife() {
    if(hp < 5){
      hp++;
    }
  }
  
  public void gainDrone() {
    if(drone < 2){
      drone++;
    }
  }
  
  public void gainShield() {
    shielded = true;
    shieldTimer = System.nanoTime();
  }
  
  public void loseLife() {
    hp--;
    recovering = true;
    recoveryTimer = System.nanoTime();
  }
  
  public void update() throws IOException {
    if(left) {
      dx = -speed;
    }
    if(right) {
      dx = speed;
    }
    if(up) {
      dy = -speed;
    }
    if(down) {
      dy = speed;
    }
    x += dx;
    y += dy;
    
    if(x < naveAncho) x = naveAncho;
    if(y < naveLargo) y = naveLargo;
    if(x > GamePanel.width - naveAncho) x = GamePanel.width - naveAncho;
    if(y > GamePanel.height - naveLargo) y = GamePanel.height - naveLargo;
    
    dx = 0;
    dy = 0;
    
    //Firing Bullet
    if(firing) {
      long elapsed = (System.nanoTime() - firingTimer) / 1000000;
      
      if(elapsed > firingDeley) {
        firingTimer = System.nanoTime();
        try {
          AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Sonidos/bulletPlayer.wav"));
          AudioFormat format = audioInputStream.getFormat();
          DataLine.Info info = new DataLine.Info(Clip.class, format);
          Clip sound = (Clip)AudioSystem.getLine(info);
          sound.open(audioInputStream);
          sound.stop();
          sound.setFramePosition(0);
          sound.start();
        } catch (Exception  ex) {
          System.out.println("Sonido bulletPlayer no encontrado.");
        }
        
        if (drone!=0) {
          try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Sonidos/bulletDrone.wav"));
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip sound = (Clip)AudioSystem.getLine(info);
            sound.open(audioInputStream);
            sound.stop();
            sound.setFramePosition(0);
            sound.start();
          } catch (Exception  ex) {
            System.out.println("Sonido bulletDrone no encontrado.");
          }
          
          if(drone > 1) {
            GamePanel.disparos.add(new DisparoPlayer(270, x-70, y+40));
            GamePanel.disparos.add(new DisparoPlayer(270, x+70, y+40));
          }
          else if(drone > 0) {
            GamePanel.disparos.add(new DisparoPlayer(270, x, y+40));
          }
        }
        
        
        if(ATK < 1) {
          GamePanel.disparos.add(new DisparoPlayer(270, x, y));
        }
        else if(ATK < 3) {
          GamePanel.disparos.add(new DisparoPlayer(270, x, y));
          GamePanel.disparos.add(new DisparoPlayer(270, x, y));
        }
        else {
          GamePanel.disparos.add(new DisparoPlayer(270, x, y));
          GamePanel.disparos.add(new DisparoPlayer(270, x, y));
          GamePanel.disparos.add(new DisparoPlayer(270, x, y));
        }
      }
    }
    
    //Recovery Damage
    long elapsed = (System.nanoTime() - recoveryTimer) / 1000000;
    if(elapsed > 2000) {
      recovering = false;
      recoveryTimer = 0;
    }
    
    //Shield Duration
    long elapsed2 = (System.nanoTime() - shieldTimer) / 1000000;
    if(elapsed2 > 2400) {
      shielded = false;
      shieldTimer = 0;
    }
  
  }
  
  public void draw(Graphics2D g) throws InterruptedException {
    if(drone>1) {
      if(contadorDrone>5) {
        contadorDrone=0;
      }
      g.drawImage(listaDrone.get(contadorDrone), x-17+70, y+40, null);
      g.drawImage(listaDrone.get(contadorDrone), x-17-70, y+40, null);
      contadorDrone++;
    }
    else if(drone>0) {
      if(contadorDrone>5) {
        contadorDrone=0;
      }
      g.drawImage(listaDrone.get(contadorDrone), x-17, y+40, null);
      contadorDrone++;
      
    }
    
    if(recovering && !shielded) {
      if(right){
        g.drawImage(naveDerechaHit, x-55, y-39, null);
      }
      else if(left){
        g.drawImage(naveIzquierdaHit, x-55, y-39, null);
      }
      else {
        g.drawImage(naveHit, x-55, y-39, null);
      }
    }
    
    else {
      if(shielded) {
        long shieldBreak = (System.nanoTime() - shieldTimer) / 1000000;
        if(shieldBreak<400) {
          g.drawImage(listaShield.get(0), x-76, y-77, null);
        }
        if(shieldBreak<800) {
          g.drawImage(listaShield.get(1), x-76, y-77, null);
        }
        if(shieldBreak<1200) {
          g.drawImage(listaShield.get(2), x-76, y-77, null);
        }
        if(shieldBreak<1600) {
          g.drawImage(listaShield.get(3), x-76, y-77, null);
        }
        if(shieldBreak<2000) {
          g.drawImage(listaShield.get(4), x-76, y-77, null);
        }
        if(shieldBreak<2400)
          g.drawImage(listaShield.get(5), x-76, y-77, null);
        }
      if(right){
        g.drawImage(naveDerecha, x-55, y-39, null);
      }
      else if(left){
        g.drawImage(naveIzquierda, x-55, y-39, null);
      }
      else {
        g.drawImage(nave, x-55, y-39, null);
      }
    }
  }
}
