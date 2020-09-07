import java.awt.Graphics;

public class Asteroid extends VectorSprite{
	double asteroidSize = 100;
	int numPoints = 20;
	public double rotationSpeed;
    double currentSpeed = 3;
	double sizeMultiple = 1.2; 
	double speedMultiple = 1.5;
	public int sizeLevel = 0;
	final int MAXIMUM_LEVEL = 2;  
	int damageCount;
	static int EXPANSION_COUNT = 1;
	public AudioPlayer crashPlayer;
	public AudioPlayer breakPlayer;
	int pointValue;
	
	public Asteroid(double x, double y,int sizeLevel) {
		super (x, y, randomColor());
		this.sizeLevel = sizeLevel;
		damageCount = 3 - sizeLevel;
		if(sizeLevel != 0) {
			asteroidSize = asteroidSize / (sizeMultiple * (sizeLevel));
			currentSpeed = currentSpeed * (speedMultiple * sizeLevel);
		}
		if(sizeLevel == 0) {
			crashPlayer = new AudioPlayer("bigCrash.wav");
			pointValue = 20;
		}
		else if(sizeLevel == 1) {
			crashPlayer = new AudioPlayer("mediumCrash.wav");
			pointValue = 30;
		}
		else {
			crashPlayer = new AudioPlayer("smallCrash.wav");
			pointValue = 50;
		}
		breakPlayer = new AudioPlayer("rockBreak.wav");
		
		createAsteroid();
		
		rotationSpeed = (Math.random()*0.04 + 0.03);
		int sign = (int)Math.round(Math.random());
		if(sign == 1) rotationSpeed = - rotationSpeed;
		
		angle = Math.random()*2*Math.PI;
		double randSpeed = Math.random()*(currentSpeed - 1) + 1;
		this.xSpeed = Math.cos(angle)*randSpeed;
		this.ySpeed = Math.sin(angle)*randSpeed;
	}
	public void createAsteroid() {
		this.centerX = (int)(asteroidSize / 2);
		this.centerY = (int)(asteroidSize / 2);
		double interval = 2*Math.PI / numPoints;
		double radius = asteroidSize / 2;
	
		for(int i = 0; i < numPoints; i ++) {
			double randRad = radius + (Math.random()*radius/4 - radius/8);
			double randAngle = i*interval + (Math.random()*interval/2 - radius/4);
			int x = centerX + (int)Math.round(Math.cos(randAngle)*randRad);
			int y = centerY + (int)Math.round(Math.sin(randAngle)*randRad);
			this.addPoint(x, y);
		}
	
							
	}
	public void update() {
		super.update();
		this.angle += rotationSpeed;
		
	}
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillPolygon(drawShape);
	}
}
