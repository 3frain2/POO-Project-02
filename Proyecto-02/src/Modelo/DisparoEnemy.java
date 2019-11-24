/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;
import Controlador.GamePanel;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author efrai
 */

public class DisparoEnemy extends Disparo {
  private boolean slow;
  
  public DisparoEnemy(double angle, double x, double y) throws IOException {
    this.x = x;
    this.y = y;
    r = 11;
    
    rad = Math.toRadians(angle);
    speed = 15;
    dx = Math.cos(rad) * speed;
    dy = Math.sin(rad) * speed;
    
    disparo = ImageIO.read(new File("src/Imagenes/Disparo/disparoEnemigo.png"));
  }

  public void setSlow(boolean slow) {
    this.slow = slow;
  }
  
  
  @Override
  public boolean update() {
    if(slow) {
      x += dx * 0.0;
      y += dy * 0.0;
    }
    else {
      x += -dx;
      y += -dy;
    }
 
    if(x < -r || x > GamePanel.width + r || y < -r || y > GamePanel.height + r) {
      return true;
    }
    
    return false;
  }
  
  @Override
  public void draw(Graphics2D g) {
    g.drawImage(disparo, (int)(x - r), (int)(y - r), null);
  }
  
}
