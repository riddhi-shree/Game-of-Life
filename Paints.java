package gameOfLife;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Map;

import javax.swing.JPanel;

public class Paints extends JPanel{
	Map<Integer, Map<Integer, String>> populationMap;
	
	public Paints(Map<Integer, Map<Integer, String>> myPopulation){
		populationMap = myPopulation;
	}
	
	  // Override paintComponent to perform your own painting
    @Override
    public void paintComponent(Graphics g) {
       super.paintComponent(g);     // paint parent's background
       
       for(int i=0; i<populationMap.size(); i++){
			for(int j=0; j<populationMap.get(i).size(); j++){
				if("*".equals(populationMap.get(i).get(j))){
					g.setColor(Color.BLUE);
				}else{
					g.setColor(Color.WHITE);
				}
				g.fillOval(50+(j*10), 50+(i*10), 10, 10);
			}
		}
    }
}
