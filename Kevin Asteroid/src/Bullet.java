import java.awt.Color;

public class Bullet extends VectorSprite{
	public int bulletRadius = 6;
	int bulletPoints = 8;
	public int bulletSpeed = 13;
	public final int MAXIMUM_DISTANCE = 500;
	public double xDist = 0;
	public double yDist = 0;
	public Bullet (double x, double y, double angle, double shipSpeedX, double shipSpeedY) {
		super (x, y, Color.GREEN);
		
		this.angle = angle;
		this.xSpeed = bulletSpeed*Math.cos(angle - (Math.PI / 2)) + shipSpeedX;
		this.ySpeed = bulletSpeed*Math.sin(angle - (Math.PI / 2)) + shipSpeedY; 
		
		double currentAngle = 0;
		for(int i = 0; i < bulletPoints; i++) {
			currentAngle = i*(2*Math.PI / bulletPoints);
			addPoint((int)(bulletRadius*Math.cos(currentAngle)),(int)(bulletRadius*Math.sin(currentAngle)));
		}
		
		
	}
	public void update() {
		super.update();
		xDist += xSpeed;
		yDist += ySpeed;
	}
	public boolean circleCollision(Asteroid rock) {
		double xDist = (xLocation + centerX) - (rock.xLocation + rock.centerX);
		double yDist = (yLocation + centerY) - (rock.yLocation + rock.centerY);
		if(xDist*xDist + yDist*yDist < (bulletRadius + (rock.asteroidSize / 2))*(bulletRadius + (rock.asteroidSize / 2))) {
			return true;
		}
		return false;
	}
}
