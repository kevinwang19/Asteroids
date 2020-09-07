import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class VectorSprite {
	public double xLocation;
	public Polygon baseShape;
	public Polygon drawShape;
	public double yLocation;
	int centerX;
	int centerY;
	double xSpeed = 0;
	double ySpeed = 0;
	public double angle = 0;
	public Color color;
	
	public VectorSprite (double x, double y, Color color) {
		baseShape = new Polygon();
		drawShape = new Polygon();
		this.xLocation = x;
		this.yLocation = y;
		this.color = color;
	}
	/**
	 * gets random color of minimum brightness
	 * @return
	 */
	public static Color randomColor() {
		int r = (int)(Math.random()*255);
		int g = (int)(Math.random()*255);
		int b = (int)(Math.random()*255);
		while(r + g + b < 255) {
			r = (int)(Math.random()*255);
		    g = (int)(Math.random()*255);
			b = (int)(Math.random()*255);
		}
		return new Color (r, g, b);
	}
	
	public void update() {
		move();
		for(int i = 0; i < baseShape.npoints; i ++) {
			double xFromCenter = baseShape.xpoints[i] - centerX;
			double yFromCenter = baseShape.ypoints[i] - centerY;
			drawShape.xpoints[i] = (int)((xFromCenter*Math.cos(angle) - yFromCenter*Math.sin(angle)) + centerX + xLocation);
			drawShape.ypoints[i] = (int)((xFromCenter*Math.sin(angle) + yFromCenter*Math.cos(angle)) + centerY + yLocation);
		}
		drawShape.invalidate();
		
	}
	public void addPoint(int x, int y) {
		baseShape.addPoint(x, y);
		drawShape.addPoint(x, y);
		
	}
	public void draw(Graphics g) {
		g.setColor(color);
		g.drawPolygon(drawShape);
	}
	public void move() {
		
		this.xLocation += xSpeed;
		this.yLocation += ySpeed;
		
		if(xLocation > GamePanel.screenWidth) {
			xLocation = 0;
		}
		else if(xLocation < 0) {
			xLocation = GamePanel.screenWidth;
		}
		if(yLocation < 0) {
			yLocation = GamePanel.screenHeight;
		}
		else if(yLocation > GamePanel.screenHeight) {
			yLocation = 0;
		}
	}
	
}
