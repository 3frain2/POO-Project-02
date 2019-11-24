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
public class EnemyPhantom extends Enemy {
  private long recoveryTimer;
  private BufferedImage enemigoInvisible;
  
  public EnemyPhantom() throws IOException {
    speed = 2;
    r = 40;
    hp = 1;
    scoreDead = 10;
    enemigoImagen = ImageIO.read(new File("src/Imagenes/Enemigo/Phantom.png"));
    enemigoInvisible = ImageIO.read(new File("src/Imagenes/Enemigo/PhantomInvisible.png"));
    
    double angle = Math.random() * 140 + 20;
    rad = Math.toRadians(angle);
    
    dx = Math.cos(rad) * speed;
    dy = Math.sin(rad) * speed;
    
    firing = false;
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
    if(slow) {
      x += dx * 0.0;
      y += dy * 0.0;
    }
    else {
      x += dx;
      y += dy;
    }
    
    if(!ready) {
      if(x > r && x < GamePanel.width - r &&
         y > r && y < GamePanel.height - r) {
        ready = true;
      }
    }
    
    //rebotar en la pantalla
    if(x < r && dx < 0) dx = -dx;
    if(y < r && dy < 0) dy = -dy;
    if(x > GamePanel.width - r && dx > 0) dx = -dx;
    if(y > GamePanel.height+100 - r && dy > 0) { dead = true; y+=200; scoreDead = 0;}
    
    //random bullets
    int numRandom = random.nextInt(200)+1;
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
    
    //Recovery Damage
    long elapsed = (System.nanoTime() - recoveryTimer) / 1000000;
    if(elapsed > 4000) {
      recovering = false;
      recoveryTimer = 0;
    }
  }
  
  @Override
  public void draw(Graphics2D g) {
    if(recovering) {
      g.drawImage(enemigoInvisible, (int)x - 45, (int)y - 36, null);
    }
    else {
      g.drawImage(enemigoImagen, (int)x - 45, (int)y - 36, null);
    }
  }
}
