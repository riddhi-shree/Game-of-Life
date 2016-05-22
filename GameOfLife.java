package gameOfLife;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;


public class GameOfLife {
	Graphics myGraphics;
	static JFrame f;
	static int numOfAliveCells=0;
	static int genCount=0;
	static Map<Integer,Map<Integer, String>> previousPopulation= new HashMap<Integer,Map<Integer, String>>();
	static int fadingRed = 255;
	
	public static void main(String[] args) throws IOException {
		f = new JFrame();
		f.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		f.setSize(1000, 1000);
		f.setBackground(Color.WHITE);
		
		Scanner scan= new Scanner(System.in);
		System.out.println("Enter a number (keep it less than 50): \n");
		startGame(scan.nextInt(), 500);
	}
	
	public static void startGame(int seed, int numOfGenerations) throws IOException{
		Map<Integer,Map<Integer, String>> myPopulation = createMatrix(seed);
		boolean stableFlag=false;
		
		//Initial Population
		Paints newPopulation = new Paints(myPopulation);
		f.setContentPane(newPopulation);
		f.setVisible(true);
		
		genCount=0;
		numOfAliveCells = getLivingCount(myPopulation);
		while(++genCount<=numOfGenerations && numOfGenerations > 0 && numOfAliveCells>0 && !stableFlag){
			previousPopulation = myPopulation;
			
			try{
			for(int i=1; i<=myPopulation.size(); i++){
				for(int j=1; j<=myPopulation.get(i-1).size(); j++){
					boolean isCellAlive = checkIfAlive(i,j, myPopulation);
					
					if(isCellAlive){
						int myNeighbors = findNeighbors(i, j, myPopulation);
						if(myNeighbors < 2){
							//Set * to ""
							myPopulation = shouldDie(i,j, myPopulation, true);
						}else if(myNeighbors > 3){
							//Set * to ""
							myPopulation = shouldDie(i,j, myPopulation, true);
						}
					}else{
	
						int myNeighbors = findNeighbors(i, j, myPopulation);
						if(myNeighbors == 3){
							//Set * to ""
							myPopulation = shouldDie(i,j, myPopulation, false);
						}
					}
					
					if(myPopulation.isEmpty()){
						break;
					}
				}
				
				if(myPopulation.isEmpty()){
					break;
				}
			}
			
			if(myPopulation.isEmpty()){
				break;
			}
			
			if(myPopulation.equals(previousPopulation)){
				JLabel finalMessage = new JLabel("This civilization has reached a STABLE state after " + genCount + " generations.");
				finalMessage.setFont(new Font("Monospaced", Font.CENTER_BASELINE, 20));
				finalMessage.setBorder(new LineBorder(Color.BLACK));
				finalMessage.setHorizontalAlignment(SwingConstants.CENTER);
				finalMessage.setVerticalAlignment(SwingConstants.CENTER);
				f.setLayout(new BorderLayout());
				f.add(finalMessage, BorderLayout.CENTER);
				f.setVisible(true);
				stableFlag=true;
			}
			
			//Change background color for every new generation
	        Color color = new Color(fadingRed, 255, 255);
			f.setBackground(color);
			fadingRed -= 5;
			if(fadingRed <0){
				fadingRed = 255;
			}
			
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	public static Integer getLivingCount(Map<Integer,Map<Integer, String>> newPopulation){
		int countLiving = 0;
		
		for(int i: newPopulation.keySet()){
			for(int j: newPopulation.get(i).keySet()){
				if("*".equals(newPopulation.get(i).get(j))){
					countLiving++;
				}
			}
		}
		
		return countLiving;
	}
	
	public static Map<Integer,Map<Integer, String>> shouldDie(int rowNum, int colNum, Map<Integer,Map<Integer, String>> myPopulation, boolean shouldDie) throws IOException{
		Map<Integer, String> myRow = new HashMap<Integer, String>();
		Map<Integer,Map<Integer, String>> myNewPopulation = new HashMap<Integer, Map<Integer,String>>();
		
		for(int i: myPopulation.keySet()){
			for(int j: myPopulation.get(i).keySet()){
				if(i==(rowNum-1) && j==(colNum-1)){
					if(shouldDie){
						myRow.put(j, ".");
					}else{
						myRow.put(j, "*");
						numOfAliveCells++;
					}
				}else{
					myRow.put(j, myPopulation.get(i).get(j));
				}
			}
			myNewPopulation.put(i, myRow);
			myRow = new HashMap<Integer, String>();
		}
		
		//Paint new population
		Paints newPopulation = new Paints(myNewPopulation);
		f.setContentPane(newPopulation);
		f.setVisible(true);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Check if civilization still exists		
		numOfAliveCells = getLivingCount(myNewPopulation);
		if(numOfAliveCells==0){
			//JPanel endOfCivilization = new JPanel();
			JLabel finalMessage = new JLabel("This civilization has ended after " + genCount + " generations.");
			finalMessage.setFont(new Font("Monospaced", Font.CENTER_BASELINE, 20));
			finalMessage.setBorder(new LineBorder(Color.BLACK));
			finalMessage.setHorizontalAlignment(SwingConstants.CENTER);
			finalMessage.setVerticalAlignment(SwingConstants.CENTER);
			//endOfCivilization.setLayout(new BorderLayout());
			//endOfCivilization.add(finalMessage, BorderLayout.CENTER);
			f.setLayout(new BorderLayout());
			f.add(finalMessage, BorderLayout.CENTER);
			//f.setContentPane(endOfCivilization);
			f.setVisible(true);
			return(new HashMap<Integer,Map<Integer, String>>());
		}
		
		return myNewPopulation;
	}
	
	public static boolean checkIfAlive(int rowNum, int colNum, Map<Integer,Map<Integer, String>> myPopulation){
		if(myPopulation.get(rowNum-1).get(colNum-1)=="*"){
			return true;
		}else{
			return false;
		}
		
	}
	
	public static Integer findNeighbors(int rowNum, int colNum, Map<Integer,Map<Integer, String>> myPopulation){
		//Any cell could have at max 8 neighbors
		//Get the currentRow, currentRow-1, currentRow+1
		//Get the currentCell, currentCell-1, currentCell+1
		//Ignore (currentCell, currentRow) combination
		int neighborCount = 0;
		
		
		
		//Max 8 neighbors are possible
		for(int i=-1; i<=1; i++){
			int myRow = rowNum-1+i;
			if(myRow >= 0 && myRow < myPopulation.size()){
				for(int j=-1; j<=1; j++){
					int myCol = colNum-1+j;
					if(myRow == (rowNum-1) && myCol == (colNum-1)){
						continue;
					}
					if(myCol >= 0 && myCol < myPopulation.get(myRow).size()){
						if("*".equals(myPopulation.get(myRow).get(myCol))){
							neighborCount++;
						}
					}
				}
			}
		}
		
		return neighborCount;
	}
	
	public static Integer checkActiveCellsInRow(int rowNum, int currentColNum, Map<Integer,Map<Integer, String>> myPopulation){
		int count=0;
		int myRow = rowNum-1;
		int myCol = currentColNum-1;
		
		if(myRow>=0 && myRow<myPopulation.size()){
			if((myCol-1)>=0 && myCol<myPopulation.get(myRow).size()){
				if(myPopulation.get(myRow).get(myCol-1)=="*"){
					count++;
				}
			}
			
			if(myCol>=0 && myCol<myPopulation.get(myRow).size()){
				if(myPopulation.get(myRow).get(myCol)=="*"){
					count++;
				}
			}
			
			if((myCol+1)>=0 && myCol<myPopulation.get(myRow).size()){
				if(myPopulation.get(myRow).get(myCol+1)=="*"){
					count++;
				}
			}
		}
		
		return count;
	}
	
	public static Map<Integer,Map<Integer, String>> createMatrix(int n){
		Map<Integer,Map<Integer, String>> myPopulation = new HashMap<Integer,Map<Integer, String>>();
		Map<Integer, String> aliveCells = new HashMap<Integer, String>();
		
		for(int i=0; i<n; i++){
			for(int j=0; j<n; j++){
				aliveCells.put(j, "*");
			}
		myPopulation.put(i, aliveCells);
		}
		
		return myPopulation;
	}

}
