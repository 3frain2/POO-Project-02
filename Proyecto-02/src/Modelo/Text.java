/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import static Controlador.GamePanel.height;
import static Controlador.GamePanel.width;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 *
 * @author efrai
 */
public class Text {
  private double x;
  private double y;
  private long time;
  private String text;
  
  private long start;

  public Text(double x, double y, long time, String text) {
    this.x = x;
    this.y = y;
    this.time = time;
    this.text = text;
    start = System.nanoTime();
  }
  
  public boolean update() {
    long elapsed = (System.nanoTime() - start) / 1000000;
    if(elapsed > time) {
      return true;
    }
    return false;
  }
  
  public void draw(Graphics2D g) {
    g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
    
    g.setColor(Color.WHITE);
    int length = (int) g.getFontMetrics().getStringBounds(text, g).getWidth(); 
    g.drawString(text, (int)x-length, (int)y);
  }
}
