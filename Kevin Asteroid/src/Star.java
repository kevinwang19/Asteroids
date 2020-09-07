import java.awt.Color;
import java.awt.Graphics;

public class Star extends VectorSprite{
	int starWidth = 5;
	int vertices = 5;
	public Star(int x, int y) {
		super(x, y, Color.YELLOW);
		for(int i = 0; i < vertices; i++) {
			int pointX = (int) (starWidth*Math.cos(((4*Math.PI)/vertices)*i));
			int pointY = (int) (starWidth*Math.sin(((4*Math.PI)/vertices)*i));
			this.addPoint(pointX, pointY);
		}
		xSpeed = 0.15;
		ySpeed = -0.15;
		
		
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
	public void draw(Graphics g) {
		super.draw(g);
		//g.fillPolygon(drawShape);
	}
}
