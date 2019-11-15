/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;
import Controlador.GamePanel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

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
  
  private int score;
  private int ATKLevel;
  private int ATK;
  private int[] requieredATK = {
    1, 2, 3, 4, 5
  };
  
  public Player() throws IOException {
    x = GamePanel.width/2;
    y = GamePanel.height/2;
    r = 25;
    dx = 0;
    dy = 0;
    speed = 5;
    
    hp = 3;
    recovering = false;
    recoveryTimer = 0;
    
    nave = ImageIO.read(new File("src/Imagenes/Nave/nave.png"));
    naveDerecha = ImageIO.read(new File("src/Imagenes/Nave/naveDerecha.png"));
    naveIzquierda = ImageIO.read(new File("src/Imagenes/Nave/naveIzquierda.png"));
    
    naveHit = ImageIO.read(new File("src/Imagenes/Nave/naveHit.png"));
    naveDerechaHit = ImageIO.read(new File("src/Imagenes/Nave/naveDerechaHit.png"));
    naveIzquierdaHit = ImageIO.read(new File("src/Imagenes/Nave/naveIzquierdaHit.png"));
  
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
  public int getRequieredATK() {return requieredATK[ATKLevel];}
  
  public boolean isRecovering() {return recovering;}

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
  }
  
  public void draw(Graphics2D g) {
    if(recovering) {
      if(right){
      g.drawImage(naveDerechaHit, x - 55, y-39, null);
      }
      else if(left){
        g.drawImage(naveIzquierdaHit, x - 55, y-39, null);
      }

      else {
        g.drawImage(naveHit, x - 55, y - 39, null);
      }
    }
    else {
      if(right){
      g.drawImage(naveDerecha, x - 55, y-39, null);
      }
      else if(left){
        g.drawImage(naveIzquierda, x - 55, y-39, null);
      }
      else {
        g.drawImage(nave, x - 55, y - 39, null);
      }
    }
  }
}
