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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

/**
 *
 * @author efrai
 */
public class BossGenesis extends Enemy {
  boolean firingCostados;
  private long recoveryTimer;
  private BufferedImage enemigoInvisible;
  
  public BossGenesis() throws IOException {
    speed = 3;
    r = 40;
    hp = 100000;
    scoreDead = 10;
    enemigoImagen = ImageIO.read(new File("src/Imagenes/Bosses/Genesis.png"));
    enemigoInvisible = ImageIO.read(new File("src/Imagenes/Bosses/GenesisInvisible.png"));
    
    double angle = Math.random() * 140 + 20;
    rad = Math.toRadians(angle);
    
    x2 = x - 110;
    y2 = -r;
    x3 = x + 110;
    y3 = -r;
    
    dx = Math.cos(rad) * speed + 2;
    dy = Math.sin(rad) * speed;
    
    firing = false;
    firingCostados = false;
    firingTimer = System.nanoTime();
    firingDeley = 200;
    
    recovering = false;
    recoveryTimer = 0;
  }
  
  public boolean isRecovering() {return recovering;}

  @Override
  public boolean isDead() {
    return dead;
  }
  
  @Override
  public void hit() {
    hp--;
    if(hp <= 0) {
      dead = true;
    }
  }
  
  public void gainInvisible() {
    recovering = true;
    recoveryTimer = System.nanoTime();
  }
  
  @Override
  public void update() throws IOException {
    x += dx;
    x2 += dx;
    x3 += dx;
    
    //Aparicion en hacia abajo
    if(y > 90.0) {
      y -= dy;
      y2 -= dy;
      y3 -= dy;
    }
    y += dy;
    y2 += dy;
    y3 += dy;
    
    if(!ready) {
      if(x > r && x < GamePanel.width - r &&
         y > r && y < GamePanel.height - r) {
        ready = true;
      }
    }
    
    //rebotar en la pantalla
    if(x2 < (r+30) && dx < 0) dx = -dx;
    if(y < r && dy < 0) dy = -dy;
    if(x3 > GamePanel.width - (r+30) && dx > 0) dx = -dx;
    if(y > GamePanel.height+100 - r && dy > 0) {dead = true; y+=200; scoreDead = 0;}
    
    //random bulletsCentro
    int numRandom = random.nextInt(9)+1;
    if(numRandom == 1) {
      firing = true;
    }
    
    if(firing && !slow) {
      long elapsed = (System.nanoTime() - firingTimer) / 1000000;
      if(elapsed > firingDeley) {
        DisparoEnemy bala = new DisparoEnemy(270, x, y);
        GamePanel.enemigosDisparos.add(bala);
        firingTimer = System.nanoTime();
      }
    }
    
    //random bulletsCostados
    int numRandom2 = random.nextInt(13)+1;
    if(numRandom2 == 1) {
      firingCostados = true;
    }
    
    if(firingCostados && !slow) {
      long elapsed = (System.nanoTime() - firingTimer) / 1000000;
      if(elapsed > firingDeley) {
        DisparoEnemy balaIzquierda = new DisparoEnemy(270, x-72, y+50);
        DisparoEnemy balaDerecha = new DisparoEnemy(270, x+72, y+50);
        GamePanel.enemigosDisparos.add(balaIzquierda);
        GamePanel.enemigosDisparos.add(balaDerecha);
        firingTimer = System.nanoTime();
      }
    }
    
    //Recovery Damage
    long elapsed = (System.nanoTime() - recoveryTimer) / 1000000;
    if(elapsed > 4000) {
      recovering = false;
      recoveryTimer = 0;
    }
    
    firing = false;
    firingCostados = false;
  }
  
  @Override
  public void draw(Graphics2D g) {
    if(recovering) {
      g.drawImage(enemigoInvisible, (int)x - 190, (int)y - 62, null);
    }
    else {
      g.drawImage(enemigoImagen, (int)x - 190, (int)y - 62, null);
    }
  }
}
