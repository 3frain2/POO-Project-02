/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author efrai
 */
public class Explosion {
  private double x;
  private double y;
  private int r;
  private int maxRadius;
  private ArrayList<Image> listaExplosiones = new ArrayList<Image>();

  public Explosion(double x, double y) throws IOException {
    this.x = x;
    this.y = y;
    this.r = 0;
    this.maxRadius = 30;
    for(int i=0; i < 3; i++) {
      listaExplosiones.add(ImageIO.read(new File("src/Imagenes/Explosion/explosion"+String.valueOf(i)+".png")));
    }
  }
  
  public boolean update() {
    r++;
    if(r >= maxRadius) {
      return true;
    }
    return false;
  }
    
  public void draw(Graphics2D g) {
    if(r<10) {
      g.drawImage(listaExplosiones.get(0), (int)x-47, (int)y-44, null);
    }
    else if(r<20) {
      g.drawImage(listaExplosiones.get(1), (int)x-47, (int)y-44, null);
    }
    else if(r<30) {
      g.drawImage(listaExplosiones.get(2), (int)x-47, (int)y-44, null);
    }
  }
}
