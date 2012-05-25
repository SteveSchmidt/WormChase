
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

public class Worm {

	/**
	* worm character controlled by player.
	* segments added when colliding with food blocks
	*/
	private int X, Y, W, H;
	private boolean isAlive;
	private int radius, speed;
	public Boolean isWormUp, isWormDown, isWormLeft, isWormRight;

	// getters and setters
	public void setX(int x) {	X = x;	}
	public int getX() {	return X;	}

	public void setY(int y) {	Y = y;	}
	public int getY() {	return Y;	}

	public void setW(int w) {	W = w;	}
	public int getW() {	return W;	}

	public void setH(int h) {	H = h;	}
	public int getH() {	return H;	}

	public void setAlive(boolean isAlive) {	this.isAlive = isAlive;	}
	public boolean isAlive(){	return isAlive;	}

	public int getRadius() {	return radius;	}
	public void setRadius(int radius) {	this.radius = radius;	}

	public int getSpeed() {	return speed;	}
	public void setSpeed(int s) {	this.speed =+ s;	}

	public Boolean getIsWormUp() {	return isWormUp;	}
	public void setIsWormUp(Boolean isWormUp) {	this.isWormUp = isWormUp;	}

	public Boolean getIsWormDown() {	return isWormDown;	}
	public void setIsWormDown(Boolean isWormDown) {	this.isWormDown = isWormDown;	}

	public Boolean getIsWormLeft() {	return isWormLeft;	}
	public void setIsWormLeft(Boolean isWormLeft) {	this.isWormLeft = isWormLeft;	}

	public Boolean getIsWormRight() {	return isWormRight;	}
	public void setIsWormRight(Boolean isWormRight) {	this.isWormRight = isWormRight;	}

	public Ellipse2D getShape() {
		Ellipse2D e = new Ellipse2D.Double(this.getX() - this.getRadius(),
				this.getY() - this.getRadius(), this.radius * 2, this.radius * 2);
		return e;
	}

	public Rectangle getBounds() {
		Rectangle r = new Rectangle(this.getX() - this.getW()/2,
				this.getY() - this.getH()/2, this.getW(), this.getH());
		return r;
	}

	public void incX(int X) {	setX(getX() + X);	}
	public void incY(int Y) {	setY(getY() + Y);	}

	// Constructor
	Worm(){
		setX(1);
		setY(1);
		this.radius = 1;
		this.speed = 1;
		setW(1);
		setH(1);
		this.isWormDown = false;
		this.isWormLeft = false;
		this.isWormRight = false;
		this.isWormUp = false;
	}

	// Constructor
	Worm( int x, int y, int rad, int speed, int w, int h, boolean ia){
		setX(x);
		setY(y);
		this.radius = rad;
		this.speed = speed;
		setW(w);
		setH(h);
		this.isWormDown = false;
		this.isWormLeft = false;
		this.isWormRight = false;
		this.isWormUp = false;
		setAlive(ia);
	}

}
