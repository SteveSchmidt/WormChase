
/**
*   Worm Chase
*   Steve Schmidt 2012
*   This is a simple worm game.  Player uses the arrow keys
*   to move the worm and collect yellow food blocks while
*   avoiding red obstacle blocks.  Worm grows longer and
*   moves faster with each food block.
*/

import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.util.Random;

public class BaseGame extends Applet implements Runnable, KeyListener {

	private static final long serialVersionUID = 1L;

	// location variables

	int score, segments;
	int segmentAdded;
	int tempx, tempy, counter;
	int moveTick, tempTick;
	int gameState;
	final int GAMEMENU = 0;
	final int GAMEPLAY = 1;
	final int GAMEOVER = 2;
	final int GAMEWINDOWHEIGHT = 480;
	final int GAMEWINDOWWIDTH = 640;
	int frames;
	double unprocessedSeconds;
	long previousTime;
	double secondsPerTick;
	int tickCount;
	boolean ticked;


	// main thread
	Thread gameLoop;
	// used for double buffering
	BufferedImage backBuffer;
	// drawing object for back buffer
	Graphics2D g2d;

	// set up random generator for food placement
	Random rand = new Random();

	// array of worm segments
	Worm[] worm = new Worm[200];

	// array of food
	Block[] food = new Block[50];

	// obstacle block objects
	Block b1 = new Block(520, 80, 30, 30, true);
	Block b2 = new Block(400, 380, 30, 30, true);
	Block b3 = new Block(180, 160, 30, 30, true);

	// window edge
	Block windowBounds = new Block(GAMEWINDOWWIDTH / 2, GAMEWINDOWHEIGHT / 2,
			GAMEWINDOWWIDTH - 6, GAMEWINDOWHEIGHT - 6, true);

	Color bgColor = new Color(139,69,19);
	Color wColor = new Color(245,222,179);
	Color sColor = new Color(210, 180,140);
	Color edgeColor = new Color(200, 140, 20);

	BasicStroke wideStroke = new BasicStroke(8.0f);

	//For later when hiding the cursor
	int[] pixels = new int[16 * 16];
	Image image = Toolkit.getDefaultToolkit().createImage(
    new MemoryImageSource(16, 16, pixels, 0, 16));
	Cursor transparentCursor =
		Toolkit.getDefaultToolkit().createCustomCursor
			(image, new Point(0, 0), "invisibleCursor");



	/****************************************************************
	 * Applet init event
	 ***************************************************************/
	public void init() {
		// create backBuffer
		backBuffer = new BufferedImage(GAMEWINDOWWIDTH, GAMEWINDOWHEIGHT,
				BufferedImage.TYPE_INT_RGB);
		g2d = backBuffer.createGraphics();

		// set up worm
		worm[0] = new Worm(300, 250, 10, 3, 20, 20, true);
		// set all but first worm segment = (0,0)
		for( int w = 1; w < worm.length; w++ ){
			worm[w] = new Worm(0, 0, 10, 3, 20, 20, false);
		}

		segments = 1;
		segmentAdded = 1;
		tempx = 0;
		tempy = 0;
		counter = 0;
		moveTick = 0;
		tempTick = 0;

		// set up food placement
		for( int f = 0; f < food.length; f++ ){
			food[f] = new Block(rand.nextInt(580) + 20,
					rand.nextInt(400) + 20, 15, 15, false);
		}
		food[0].setAlive(true);

		// set Score
		score = 0;

		// set Game State
		gameState = GAMEMENU;

		frames = 0;
		unprocessedSeconds = 0;
		previousTime = System.nanoTime();
		secondsPerTick = 1 / 60.0;
		tickCount = 0;
		ticked = false;

		addKeyListener(this);
	}

	/****************************************************************
	 * Applet update event
	 ***************************************************************/
	public void update(Graphics g) {

		//clear background
		g2d.setPaint(bgColor);
		g2d.fillRect(0, 0, GAMEWINDOWWIDTH, GAMEWINDOWHEIGHT);

		// draw game screen
		if(gameState == GAMEMENU){
			drawMenu();
		}
		else if(gameState == GAMEPLAY){

			// print status
			g2d.setFont(new Font("Ariel", Font.PLAIN, 12));
			g2d.setPaint(Color.WHITE);
			g2d.drawString("Score: " + score + " ", 10, 25);
			g2d.drawString("WormSpeed: " + worm[0].getSpeed(), 10, 40);
			g2d.drawString("Segments: " + segmentAdded, 10, 55);

			// draw objects
			drawWorm();
			drawFood();
			drawObstacle();
		}
		else if(gameState == GAMEOVER){
			drawGameOver();
		}

		// repaint scene to window
		paint(g);
	}

	/****************************************************************
	 * Draw Menu
	 ***************************************************************/

	private void drawMenu() {
		// draw title
		 g2d.setFont(new Font("Verdana", Font.BOLD, 36));
         g2d.setColor(Color.BLACK);
         g2d.drawString("WORM CHASE", (GAMEWINDOWWIDTH / 2) - 100,
         		(GAMEWINDOWHEIGHT / 2) - 50);
         g2d.setColor(Color.YELLOW);
         g2d.drawString("WORM CHASE", (GAMEWINDOWWIDTH / 2) - 150,
         		(GAMEWINDOWHEIGHT / 2) - 70);

         // draw controls
         int x = 270, y = 12;
         g2d.setFont(new Font("Times New Roman", Font.ITALIC | Font.BOLD, 20));
         g2d.setColor(Color.ORANGE);
         g2d.drawString("CONTROLS:", x, ++y*20);
         g2d.drawString("MOVE - Left/Right Arrows", x+20, ++y*20);
         g2d.drawString("     - Up/Down Arrows", x+40, ++y*20);
         g2d.drawString("Don't hit red blocks", x+20, ++y*20);
         g2d.drawString("Eat Yellow food", x+20, ++y*20);

         // draw start control
         g2d.setFont(new Font("Ariel", Font.BOLD, 24));
         g2d.setColor(Color.YELLOW);
         g2d.drawString("Press SPACE to start", (GAMEWINDOWWIDTH / 2) - 100,
         		(int) (GAMEWINDOWHEIGHT * 0.9));

         // draw border
         g2d.setStroke(wideStroke);
         g2d.setColor(edgeColor);
         g2d.draw(windowBounds.getShape());

	}
	/****************************************************************
	 * Draw worm event
	 ***************************************************************/
	private void drawWorm() {
		g2d.setColor(wColor);
		for( int s = 0; s < segments; s++ ){
			// set segments to alive for each active segment, then draw them
			worm[s].setAlive(true);
		}
		for( int w = worm.length - 1; w >= 0; w-- ){
			if( worm[w].isAlive() ){
				if( w % 5 == 0 ){
					g2d.setColor(sColor);
					g2d.fill(worm[w].getShape());
				}
				else{
					g2d.setColor(wColor);
					g2d.fill(worm[w].getShape());
				}
			}
		}
	}

	/****************************************************************
	 * Draw food event
	 ***************************************************************/
	private void drawFood() {
		g2d.setColor(Color.ORANGE);
		for( int f = 1; f < food.length; f++ ){
			// begin with first food displayed
			if( food[0].isAlive() ){
				g2d.fill(food[0].getShape());
			}
			// once the previous food dies add the next
			else if( !food[f - 1].isAlive() && food[f].isAlive() ){
				g2d.fill(food[f].getShape());
			}
		}

	}

	/****************************************************************
	 * Draw obstacle event
	 ***************************************************************/
	private void drawObstacle() {
		g2d.setColor(Color.RED);
		g2d.fill(b1.getShape());
		g2d.fill(b2.getShape());
		g2d.fill(b3.getShape());

		// draw border
		g2d.setStroke(wideStroke);
		g2d.setColor(edgeColor);
		g2d.draw(windowBounds.getShape());
	}

	/****************************************************************
	 * Draw Game Over
	 ***************************************************************/
	private void drawGameOver() {

		g2d.setFont(new Font("Ariel", Font.BOLD, 50));
		g2d.setColor(Color.RED);
		g2d.drawString("GAME OVER", (GAMEWINDOWWIDTH / 2) - 150,
				(int) (GAMEWINDOWHEIGHT / 2));

		g2d.setFont(new Font("Ariel", Font.BOLD, 24));
		g2d.setColor(Color.WHITE);
		g2d.drawString("SCORE: " + score + "!",
				(GAMEWINDOWWIDTH / 2) - 100, (int) (GAMEWINDOWHEIGHT * 0.6));

		g2d.setFont(new Font("Ariel", Font.BOLD, 24));
		g2d.setColor(Color.ORANGE);
		g2d.drawString("Press SPACE to start",
		(GAMEWINDOWWIDTH / 2) - 100, (int) (GAMEWINDOWHEIGHT * 0.9));

		// draw border
		g2d.setStroke(wideStroke);
		g2d.setColor(edgeColor);
		g2d.draw(windowBounds.getShape());
	}

	/****************************************************************
	 * Paint event
	 ***************************************************************/
	public void paint(Graphics g) {
		// draw backBuffer into window
		g.drawImage(backBuffer, 0, 0, this);

	}
	/****************************************************************
	 * Start event
	 ***************************************************************/
	public void start() {
		// create the gameLoop
		gameLoop = new Thread(this);
		gameLoop.start();
	}
	/****************************************************************
	 * Run event
	 ***************************************************************/
	@Override
	public void run() {
		// get current thread
		Thread t = Thread.currentThread();

		// continue while thread is alive
		while (t == gameLoop) {
			repaint();
			try {

				gameUpdate();

				if(gameState == GAMEPLAY || gameState == GAMEMENU){
					setCursor(transparentCursor);
				}

				// adjust for target frame rate at 50 fps
				Thread.sleep(18);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/****************************************************************
	 * Stop event
	 ***************************************************************/
	public void stop() {
		// kill gameLoop thread
		gameLoop = null;
	}
	/****************************************************************
	 * Game Update
	 ***************************************************************/
	private void gameUpdate() {
		updateWorm();
		updateWormHistory();
		if(gameState == GAMEPLAY){
			updateScore();
		}
		checkCollisions();
	}



	/****************************************************************
	 * Update Worm
	 ***************************************************************/
	private void updateWorm() {

		tempTick = moveTick;
		// if arrows are pressed, change worm direction
		if (worm[0].getIsWormLeft() == true){
			worm[0].setX(worm[0].getX() - worm[0].getSpeed());
			moveTick++;
		}
		if (worm[0].getIsWormRight() == true){
			worm[0].setX(worm[0].getX() + worm[0].getSpeed());
			moveTick++;
		}
		if (worm[0].getIsWormUp() == true){
			worm[0].setY(worm[0].getY() - worm[0].getSpeed());
			moveTick++;
		}
		if (worm[0].getIsWormDown() == true){
			worm[0].setY(worm[0].getY() + worm[0].getSpeed());
			moveTick++;
		}

		// loop worm around the screen
		if(worm[0].getX() < 0)
			worm[0].setX(worm[0].getX() + GAMEWINDOWWIDTH);
		if(worm[0].getX() > GAMEWINDOWWIDTH)
			worm[0].setX(worm[0].getX() - GAMEWINDOWWIDTH);
		if(worm[0].getY() < 0)
			worm[0].setY(worm[0].getY() + GAMEWINDOWHEIGHT);
		if(worm[0].getY() > GAMEWINDOWHEIGHT)
			worm[0].setY(worm[0].getY() - GAMEWINDOWHEIGHT);
	}
	/****************************************************************
	 * Update worm history
	 ***************************************************************/
	private void updateWormHistory() {
		// create a list of past worm locations for segment placement
		// only update segments after a couple moves of the worm
		counter++;
		if( counter == 1 ){
			for( int s = worm.length - 1; s > 0; s-- ){
				if( tempTick != moveTick ){
					worm[s].setX(worm[s-1].getX());
					worm[s].setY(worm[s-1].getY());
				}
			}
			counter = 0;
		}
	}

	/****************************************************************
	 * Update Time and Score
	 ***************************************************************/
	private void updateScore() {
		// set up for fps and score calculation

		// fps calculation variables
		long currentTime = System.nanoTime();
		long passedTime = currentTime - previousTime;
		previousTime = currentTime;
		unprocessedSeconds += passedTime / 1000000000.0;

		while( unprocessedSeconds > secondsPerTick){
			unprocessedSeconds -= secondsPerTick;
			ticked = true;
			tickCount++;
			if(tickCount % 60 == 0){
				System.out.println(frames + " fps");
				previousTime += 1000;
				frames = 0;
				score++;
			}
		}
		if(ticked){
			frames++;
		}

	}

	/****************************************************************
	 * Collision check
	 ***************************************************************/
	private void checkCollisions() {
		// check for collisions with obstacle blocks
		if( (worm[0].getBounds().intersects(b1.getBounds()) && b1.isAlive() ) ||
				( worm[0].getBounds().intersects(b2.getBounds()) &&
						b2.isAlive() ) ||
				( worm[0].getBounds().intersects(b3.getBounds()) &&
						b3.isAlive() )) {
			gameState = GAMEOVER;
		}

		// check for collisions with worm segments
		for( int s = 15; s < worm.length; s++ ){
			if( worm[0].getBounds().intersects(worm[s].getBounds()) &&
					worm[s].isAlive() ){
				gameState = GAMEOVER;
			}
		}

		// check for food collisions with blocks
		for( int f = 0; f < food.length; f++ ){
			// if food is placed within a block, replace it
			if( (food[f].getBounds().intersects(b1.getBounds()) &&
					b1.isAlive() ) ||
					( food[f].getBounds().intersects(b2.getBounds()) &&
							b2.isAlive() ) ||
					( food[f].getBounds().intersects(b3.getBounds()) &&
							b2.isAlive() )) {
				food[f].setX(food[f].getX() + 15);

			}
		}

		// check for collisions with food
		for( int f = 0; f < food.length; f++ ){
			// if worm hits food, remove it and place a new one
			if( worm[0].getBounds().intersects(food[f].getBounds()) &&
					food[f].isAlive() ){
				segments += 5;
				segmentAdded++;
				score += 100;
				food[f].setAlive(false);
				if( f < food.length - 1 ){
					food[f + 1].setAlive(true);
				}

				// after collision, check to increase speed
				if( segmentAdded % 5 == 0 && segmentAdded != 0){
					worm[0].setSpeed(worm[0].getSpeed() + 1);
				}
			}
		}
	}

	/****************************************************************
	 * KeyListener
	 ***************************************************************/
	@Override
	public void keyPressed(KeyEvent ke) {
		int keyCode = ke.getKeyCode();

        switch (keyCode) {

        case KeyEvent.VK_LEFT:
        	if(!worm[0].getIsWormRight()){
        		worm[0].setIsWormRight(false);
        		worm[0].setIsWormLeft(true);
        		worm[0].setIsWormUp(false);
        		worm[0].setIsWormDown(false);
        	}
        	ke.consume();
            break;

        case KeyEvent.VK_RIGHT:
        	if(!worm[0].getIsWormLeft()){
        		worm[0].setIsWormRight(true);
        		worm[0].setIsWormLeft(false);
        		worm[0].setIsWormUp(false);
        		worm[0].setIsWormDown(false);
        	}
        	ke.consume();
            break;

        case KeyEvent.VK_UP:
        	if(!worm[0].getIsWormDown()){
        		worm[0].setIsWormRight(false);
        		worm[0].setIsWormLeft(false);
        		worm[0].setIsWormUp(true);
        		worm[0].setIsWormDown(false);
        	}
        	ke.consume();
            break;

        case KeyEvent.VK_DOWN:
        	if(!worm[0].getIsWormUp()){
        		worm[0].setIsWormRight(false);
        		worm[0].setIsWormLeft(false);
        		worm[0].setIsWormUp(false);
        		worm[0].setIsWormDown(true);
        	}
        	ke.consume();
            break;

		case KeyEvent.VK_SPACE:
			if(gameState == GAMEPLAY){
			}
			else if(gameState == GAMEMENU){
				init();
				gameState = GAMEPLAY;
			}
			else if(gameState == GAMEOVER){
				init();
				gameState = GAMEPLAY;

			}
			break;
        }
	}


	@Override
	public void keyReleased(KeyEvent ke) {	}
	@Override
	public void keyTyped(KeyEvent ke) {	}
}
