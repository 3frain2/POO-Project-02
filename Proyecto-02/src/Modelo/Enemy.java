/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;
import Controlador.GamePanel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 * @author efrai
 */

public abstract class Enemy {
  protected double x;
  protected double y;
  protected int r;

  protected double dx;
  protected double dy;
  protected double rad;
  protected double speed;
  protected int scoreDead;

  protected boolean firing;
  protected long firingTimer;
  protected long firingDeley;
  
  protected int hp;

  protected BufferedImage enemigoImagen;

  protected boolean ready;
  protected boolean dead;
  
  //class for name en java
  public Enemy() throws IOException {
    
    x = Math.random() * GamePanel.width / 2 + GamePanel.width / 4;
    y = -r;
    ready = false;
    dead = false;
    
  }

  public double getX() {return x;}
  public double getY() {return y;}
  public int getR() {return r;}
  public int getScoreDead() {return scoreDead;}
  
  
  public void setFiring(boolean b) {firing = b;}
  
  public boolean isDead() {return false;}
  public void hit() {}    
  public void update() throws IOException {}
  public abstract void draw(Graphics2D g);
}
