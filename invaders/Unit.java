
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Rectangle;

/**
 * Class for all elements on screen that can interract (Player Ship, Aliens, Shields, Projectiles etc.)
 */

public abstract class Unit {

	protected double x;
	protected double y;
	protected long id;
	protected BufferedImage sprite;
	protected BufferedImage image;
	protected boolean isDestroyed = false;
	protected boolean hitsEdge = false;
	protected double dx;
	protected double dy;


	/**
	 * Construct a unit with path to sprite image and x & y position on the screen.
	 */
	public Unit(String spriteImage, int x, int y) {

		try {
			image = ImageIO.read(new File(spriteImage));
		} catch (IOException ex) {
			System.out.println("Couldn't find file: " + spriteImage);
       	}

		this.sprite = image;
		this.x = x;
		this.y = y;
	}


	/**
	 * Order this unit to move based on time passed since last movement
	 */
	public void move(long timePassed) {
		// update the location of the unit based on move speeds
		x += (timePassed * dx) / 1000;
		y += (timePassed * dy) / 1000;
	}




	/**
	 * Methods for setting and reading units x- and y position and movement
	 *
	 *
	 */
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}


	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}


	public void setHorizontalMovement(double dx) {
		this.dx = dx;
	}

	public void setVerticalMovement(double dy) {
		this.dy = dy;
	}

	public double getHorizontalMovement() {
		return dx;
	}

	public double getVerticalMovement() {
		return dy;
	}




	//Return this units image
	public BufferedImage getSprite() {
		return sprite;
	}

	//Returns units uniqe id
	public long getID()  {
		return id;
	}

	//Set unit to destroyed so it will be removed during the next game cycle
	public void destroy()  {
		isDestroyed = true;
	}

	//Check if unit is destroyed
	public boolean isDestroyed()  {
		return isDestroyed;
	}

	//"Un-destroy" unit
	public void reload()  {
		isDestroyed = false;
	}

	//Check if unit has moved into the edge of the screen
	public boolean hitsEdge()  {
		return hitsEdge;
	}

	/**
	 * Return a rectangle with the same size as the units sprite image.
	 * This is used for checking if two units occupy the same space on the screen and should be destroyed (projektile hitting an alien for example).
	 */
	public Rectangle getPosition() {
		Rectangle r = new Rectangle((int) x, (int) y, image.getWidth(), image.getHeight());
		return r;
	}


}