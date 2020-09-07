import java.awt.Color;
import java.awt.Graphics;

public class Moon extends VectorSprite {
	int moonRadius = 25;
	int numPoints = 8;
	public Moon(int x, int y) {
		super(x, y, Color.WHITE);
		for(int i = 0; i <= numPoints; i++) {
			double theta = i*(Math.PI/numPoints);
			int pointX = (int) (moonRadius*Math.cos(theta));
			int pointY = (int) (moonRadius*Math.sin(theta));
			this.addPoint(pointX, pointY);
		}
		for(int i = 1; i < numPoints; i++) {
			double theta = Math.PI - i*(Math.PI/numPoints);
			double newRadius = moonRadius;
			if(i == 1 | i == 7) {
				newRadius *= 7.0/8;
			}
			else if(i == 2 | i == 6) {
				newRadius *= 6.0/8;
			}
			else if(i == 3 | i == 5) {
				newRadius *= 5.0/8;
			}
			else{
				newRadius *= 1.0/2;
			}
			int newPointX = (int) (newRadius*Math.cos(theta));
			int newPointY = (int) (newRadius*Math.sin(theta));
			this.addPoint(newPointX, newPointY);
		}
		//System.exit(0);
		xSpeed = 0.15;
		ySpeed = -0.15;
		angle = Math.PI/2;
			
			
	}
	public void draw(Graphics g) {
		super.draw(g);
		//g.fillPolygon(drawShape);
	}
	public void update(double shipSpeedX,double shipSpeedY) {
		super.update();
		if(shipSpeedX != 0 || shipSpeedY != 0) {
			this.xSpeed = -3.0/2*shipSpeedX;
			this.ySpeed = -3.0/2*shipSpeedY;
		}
		else {
			xSpeed = 0.15;
			ySpeed = -0.15;
		}
	}
}

