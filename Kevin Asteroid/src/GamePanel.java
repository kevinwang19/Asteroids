import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Color;
import java.util.ArrayList;

public class GamePanel extends JPanel implements KeyListener{
	JFrame frame;
	static GamePanel panel;
	
    Ship ship;
	long lastRepaintTime = System.currentTimeMillis();
	long currentTime;
	int timeDelay = 64;
	public static int screenWidth;
	public static int screenHeight;
	double framesPerSecond;
	boolean upKey, downKey, leftKey, rightKey, aKey, dKey, wKey;
	boolean spaceKey;
	public ArrayList <Asteroid> rockList;
	public int numRocks = 3;
	public static String newLine = System.getProperty("line.separator");
	public BufferedImage screenImage;
	public MusicPlayer player;
	public ArrayList <String> scoreList;
	public static final String HIGH_SCORES_FILE_NAME = "/asteroidsHighScores.txt";
	public String name;
	public HealthPack healthPack;
	int numStars = 15;
	public Star[] starList = new Star[numStars];
	int numMoons = 3;
	public Moon[] moonList = new Moon[numMoons];
	int fireMode = -1;
	static final int SHOOT = 0;
	static final int MULTI = 1;
	static final int SPRAY = 2;
	
	
	

	
	
	public static void main(String[] args) {
		JOptionPane.showMessageDialog(null, "WELCOME TO ASTEROIDS!"
				+ newLine + "Avoid and Destroy All the Asteroids" 
				+ newLine + "To Move in the Direction Your Ship is Facing, Press the UP or W Key."
				+ newLine + "To Turn Right, Press the RIGHT or D Key."
				+ newLine + "To Turn Left, Press the LEFT or A Key."
				+ newLine +	"To Fire, Press the SPACEKEY."
				+ newLine + "Collect Health Packs to Gain Health"
				+ newLine + "HAVE FUN!");
		
		panel = new GamePanel();
		panel.name = JOptionPane.showInputDialog("Please Enter Your Name.");
		try {
		panel.fireMode = Integer.parseInt(JOptionPane.showInputDialog("How Would You Like to Fire?" + newLine +
				"1 for Regular." + newLine + 
				"2 for Multi." + newLine + 
				"3 for Spray." + newLine));
		panel.fireMode = Math.abs((panel.fireMode - 1)% 3);
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
			panel.fireMode = MULTI;
		}
		while(true) {
			panel.updateGame();
		}
       
	}
	public void updateGame() {
		currentTime = System.currentTimeMillis() - lastRepaintTime;
		if(currentTime < (1000.0 / timeDelay))
			{
			return;
			}
		
		framesPerSecond = 1000.0 / currentTime;
		update();
		panel.repaint();
		lastRepaintTime = System.currentTimeMillis();
	}
	public void update() {
		screenWidth = this.getSize().width;
		screenHeight = this.getSize().height;
		updateKeys();
		ship.update(rockList);
		if(healthPack != null) {
			healthPack.update();
			if(ship.hitAsteroid(healthPack)) {
				if(ship.health < ship.MAX_HEALTH) {
					ship.health += healthPack.healthCount;
					healthPack.healthPlayer.play();
					healthPack = null;
				}
				
			} 
		}
		if(healthPack == null) {
			int rand = (int)(Math.random()*1000);
			if(rand == 150) {
				healthPack = new HealthPack((int)(Math.random()*screenWidth), (int)(Math.random()*screenHeight));
			}
		}
		for(Asteroid rock: rockList) {
			rock.update();
		}
		drawScreenImage(screenImage.getGraphics());
		
		if(ship.health <= 0) {
			JOptionPane.showMessageDialog(null, "OH NO!" + newLine + "Your Ship Has Been Destroyed!");
			int result = JOptionPane.showConfirmDialog(null, "Would You Like to Try Again?");
			writeHighScores();
			printHighScores();
			if(result == JOptionPane.YES_OPTION) {
				try {
					this.fireMode = Integer.parseInt(JOptionPane.showInputDialog("How Would You Like to Fire?" + newLine +
							"1 for Regular." + newLine + 
							"2 for Multi." + newLine + 
							"3 for Spray." + newLine));
					this.fireMode = Math.abs((this.fireMode - 1)% 3);
				}
				catch(NumberFormatException e) {
					e.printStackTrace();
				    this.fireMode = MULTI;
				}
				//setting expansion count back to default
				Asteroid.EXPANSION_COUNT = 1;
				resetGame();
			}
			else if(result == JOptionPane.NO_OPTION) {
				//we can stop a Java program by doing this
				System.exit(0);
			}
		}
		
		if(rockList.size() == 0) {
			drawScreenImage(screenImage.getGraphics());
			repaint();
			JOptionPane.showMessageDialog(null, "YOU WIN!" + newLine);
			int result = JOptionPane.showConfirmDialog(null, "Would You Like to Play the Next Level?");
			if(result == JOptionPane.YES_OPTION) {
				//Storing the points before the game resets.
				int pointsBeforeReset = ship.points;
				Asteroid.EXPANSION_COUNT++;
				resetGame();
				ship.points += pointsBeforeReset;
			}
			else if(result == JOptionPane.NO_OPTION) {
				writeHighScores();
				//we can stop a Java program by doing this
				System.exit(0);
			}
		}
		for(int i = 0; i < starList.length; i++) {
			starList[i].update(ship.xSpeed,ship.ySpeed);
		}
		for(int i = 0; i < moonList.length; i++) {
			moonList[i].update(ship.xSpeed,ship.ySpeed);
		}
	}
	public void updateKeys() {
		if (upKey | wKey) {
			ship.xSpeed += ship.ENGINE_THRUST*Math.cos(ship.angle - Math.PI / 2);
			ship.ySpeed += ship.ENGINE_THRUST*Math.sin(ship.angle - Math.PI / 2);
			ship.engineOn = true;
		}
		else {
			ship.engineOn = false;
		}
		if (leftKey | aKey) {
			ship.angle -= 0.05;
		}
		if (rightKey | dKey) {
			ship.angle += 0.05;
		}
		if (spaceKey) {
			if(fireMode == SHOOT) {
				ship.shoot();
			}
			else if(fireMode == MULTI) {
				ship.multiShot();
			}
			else if(fireMode == SPRAY) {
				ship.spray();
			}
		}
	}
	public void resetGame() {
		healthPack = null;
		
		
		rockList = new ArrayList<Asteroid>();
		for(int i = 0; i < numRocks; i++) {
			int x = (int)(Math.random()*600 + 100);
			int y = (int)(Math.random()*400 + 100);
			rockList.add(new Asteroid(x,y,0));
		}
		
		int randX =-1;
		int randY =-1;
		
		boolean positionGood = false;
		while(!positionGood) {
			positionGood = true;
			randX =(int)(Math.random()*600 + 100);
			randY = (int)(Math.random()*400 + 100);
			for(Asteroid rock:rockList) {
				double xDistance = randX - rock.xLocation;
				double yDistance = randY - rock.yLocation;
				if((xDistance*xDistance) + (yDistance*yDistance) < 10000) {
					positionGood = false;
					break;
				}
			}
		}
		ship = new Ship(randX, randY);
		
		aKey = false;
		wKey = false;
		dKey = false;
		leftKey = false;
		upKey = false;
		rightKey = false;
		spaceKey = false;
		
	}
	public GamePanel() {
		screenImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Graphics2D g2d = (Graphics2D)screenImage.getGraphics();
		g2d.setRenderingHints(renderingHints);
		frame = new JFrame("Kevin's Game");
		frame.setSize(800,600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.addKeyListener(this);
		
		frame.setContentPane(this);
		resetGame();
		player = new MusicPlayer("bgm.wav");
		player.setMode(MusicPlayer.LOOP);
		player.play();
		readHighScores();
	    printHighScores();
	    for(int i = 0; i < starList.length; i++) {
			starList[i] = new Star((int)(i*(Math.random()*800)/numStars), (int)(i*(Math.random()*600))/numStars);
		}
	    for(int i = 0; i < moonList.length; i++) {
			moonList[i] = new Moon((int)(i*(Math.random()*800)/numMoons), (int)(i*(Math.random()*600))/numMoons);
		}
	}
	public void printHighScores() {
		 String highScores = ""; 
		 for(int i = 0; i < 5 & i < scoreList.size(); i++) {
		    	highScores += scoreList.get(i) + newLine;
		 }
		 JOptionPane.showMessageDialog(null, "HIGH SCORES: " + newLine + highScores);
	}
	public void drawScreenImage(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 800, 600);
	    for(int i = 0; i < starList.length; i++) {
	    	starList[i].draw(g);
	    }
	    for(int i = 0; i < moonList.length; i++) {
	    	moonList[i].draw(g);
	    }
		for(Asteroid rock : rockList) {
			rock.draw(g);
		}
		if(healthPack != null) {
			healthPack.draw(g);
		}
		ship.draw(g);
		g.setColor(Color.green);
		String fpsString = "FPS: " + String.format("%.2f",framesPerSecond);
		g.drawString(fpsString,screenWidth - 70, screenHeight - 10);
		String pointsString = "Points:" + ship.points;
		g.drawString(pointsString, screenWidth - 70, screenHeight - 30);
	}
	public void paintComponent(Graphics g) {
		g.drawImage(screenImage,0,0,null);
	}
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			leftKey = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			rightKey = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			downKey = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			upKey = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_SPACE) { 
			spaceKey = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_W) { 
			wKey = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_A) { 
			aKey = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_D) { 
			dKey = true;
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			leftKey = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			rightKey = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			downKey = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			upKey = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			spaceKey = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_W) {
			wKey = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_A) {
			aKey = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_D) {
			dKey = false;
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	

	}
	public void writeHighScores() {
		File homeDir = new File(System.getProperty("user.home"));
		File highScores = new File(homeDir.getPath() + HIGH_SCORES_FILE_NAME);
		try {
			FileWriter fileWriter = new FileWriter(highScores);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			boolean printedScore = false;
			int currentScore;
			for(int i = 0; i < scoreList.size();i++){
				String scoreString = scoreList.get(i);
				currentScore = Integer.parseInt(scoreString.substring(0, scoreString.indexOf("|")));
				if(printedScore == false & ship.points > currentScore) {
					scoreList.add(i,ship.points + "|" + name);
					printedScore = true;
					scoreString = scoreList.get(i);
				}
				writer.write(scoreString);
				writer.newLine();
			}
			if(printedScore == false) {
				scoreList.add(ship.points + "|" + name);
				writer.write(ship.points + "|" + name);
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public void readHighScores() {
		
		scoreList = new ArrayList <String> ();
		
		File homeFile = new File(System.getProperty("user.home"));
		File highScoresFile = new File(homeFile.getPath() + HIGH_SCORES_FILE_NAME);
		FileReader fileReader = null;
		try {
			
			fileReader = new FileReader(highScoresFile);
			BufferedReader reader = new BufferedReader(fileReader);
			
			String scoreLine = reader.readLine();
			while(scoreLine != null) {
				scoreList.add(scoreLine);
				scoreLine = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
}
