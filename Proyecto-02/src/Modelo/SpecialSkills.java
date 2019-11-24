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
public class SpecialSkills {
  double x;
  double y;
  int r;
  
  private int type;
  private BufferedImage skillImagen;
  
  //Poderes:
  /*
  1. +1 Drone
  2. +1 Damage
  3. +1 Vida
  4. +1 Shield
  */
  
  public SpecialSkills(int type, int x, int y) throws IOException {
    this.type = type;
    this.x = x;
    this.y = y;
    this.r = 12;
    
    if(type == 1) {
      this.skillImagen = ImageIO.read(new File("src/Imagenes/Herramientas/skillDrone.png"));
    }
    if(type == 2) {
      this.skillImagen = ImageIO.read(new File("src/Imagenes/Herramientas/skillAtkUp.png"));
    }
    if(type == 3) {
      this.skillImagen = ImageIO.read(new File("src/Imagenes/Herramientas/skillHpUp.png"));
    }
    if(type == 4) {
      this.skillImagen = ImageIO.read(new File("src/Imagenes/Herramientas/skillShield.png"));
    }
    if(type == 5) {
      this.skillImagen = ImageIO.read(new File("src/Imagenes/Herramientas/skillShield.png"));
    }
  }

  public double getX() {return x;}
  public double getY() {return y;}
  public int getR() {return r;}

  public int getType() {return type;}
  
  public boolean update() {
    y += 2;
    
    if(y > GamePanel.height + r) {
      return true;
    }
    return false;
  }
  
  public void draw(Graphics2D g) {
    g.drawImage(skillImagen, (int)(x - r), (int)(y - r), null);
  }
  
}
