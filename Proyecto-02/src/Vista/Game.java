/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;
import Controlador.GamePanel;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
/**
 *
 * @author efrai
 */
public class Game {
  private static Image icon;
  
  public static void main(String[] args) throws IOException {
    JFrame window = new JFrame("VocantSpatium");
    
    //Centrar JFrame
    Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
    int height = pantalla.height;
    int width = pantalla.width;
    window.setSize(width/2, height/2 + 200);
    window.setLocationRelativeTo(null);
    
    try {
    //Poner icono\
    icon = ImageIO.read(new File("src/Imagenes/Herramientas/icon.png"));
    window.setIconImage(icon);
    }
    catch(IIOException e) {
      System.out.println("El icono no se encuentra.");
    }
    window.setResizable(false);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setContentPane(new GamePanel());
    window.pack();
    window.setVisible(true);
  }
}
