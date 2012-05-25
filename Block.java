import java.awt.Rectangle;

public class Block {

	/**
	* Blocks used as obstacles
	*/
	private int X, Y, W, H;
	private boolean isAlive;

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

	public Rectangle getBounds() {
		Rectangle r = new Rectangle(this.getX()- this.getW()/2,
				this.getY()- this.getH()/2, this.getW(), this.getH());
		return r;
	}

	public Rectangle getShape() {
		Rectangle r = new Rectangle(this.getX()- this.getW()/2,
				this.getY()- this.getH()/2, this.getW(), this.getH());
		return r;
	}

	// Constructor
	Block(){
		setX(0);
		setY(0);
		setW(20);
		setH(20);
		setAlive(true);
	}

	// Constructor
	Block(int x, int y, int w, int h, boolean ia){
		setX(x);
		setY(y);
		setW(w);
		setH(h);
		setAlive(ia);
	}
}
