import java.awt.Color;
import java.awt.Graphics;

public class HealthPack extends VectorSprite{
	int radius = 15;
	int numPoints = 50;
	public int healthCount = 2;
	public AudioPlayer healthPlayer;
	public HealthPack(int x, int y) {
		super(x, y, Color.BLUE);
		healthPlayer = new AudioPlayer("healthPack.wav");
		healthPlayer.setGain(0);
		for(int i = 0; i < numPoints; i++) {
			int pointX = (int) (radius*Math.cos(((2*Math.PI)/numPoints)*i));
			int pointY = (int) (radius*Math.sin(((2*Math.PI)/numPoints)*i));
			this.addPoint(pointX, pointY);
		}
		
		xSpeed = 2;
		ySpeed = 2;
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		g.setColor(Color.RED);
		g.fillRect((int)(xLocation-(0.4/3 * radius)), (int)(yLocation - radius*(1.8/3)), (int)(radius*(1.5/3)), (int)(radius*(4.0/3)));
		g.fillRect((int)(xLocation-(1.61/3 * radius)), (int)(yLocation - radius*(0.55/3)), (int)(radius*(4.0/3)), (int)(radius*(1.5/3)));
	}
}
