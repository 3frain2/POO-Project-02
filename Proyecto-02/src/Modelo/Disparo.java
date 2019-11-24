/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author efrai
 */
public abstract class Disparo {
  protected double x;
  protected double y;
  protected int r;
  
  protected double dx;
  protected double dy;
  protected double rad;
  protected double speed;
  
  protected BufferedImage disparo;
  
  public double getX() {return x;}
  public double getY() {return y;}
  public int getR() {return r;}
  
  public abstract boolean update();
  public abstract void draw(Graphics2D g);
  
}
