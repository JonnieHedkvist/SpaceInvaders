
/**
 * The unit that represents the aliens downward projectiles
 */

public class BombUnit extends Unit {

	/**
	 * Create a new unit to represent the bomb fired by aliens
	 * Load the imagefile, then x and y sets the bomb location on screen, and the unit is given a unique id
	 */
	private boolean exists;

	public BombUnit(String spriteImage, int x, int y, long id) {
		super(spriteImage, x, y);
		this.id = id;

		//Set bombs vertical speed
		dy = 200;
	}

	/**
	 * Move unit based on how much time has passed since last movement and its horizontal and vertical speed
	 */
	public void move(long timePassed) {

		super.move(timePassed);

		if (y > 480)  {
			this.destroy();
		}
	}
}