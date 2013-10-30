

/**
 * The unit that represents the alien mothership
 */

public class MotherShipUnit extends Unit {


	/**
	 * Create a new unit to represent the ship
	 * Load the imagefile, then x and y sets the ships location on screen, and the unit is given a unique id
	 */
	public MotherShipUnit(String spriteImage, int x, int y, long id) {

		super(spriteImage, x, y);
		this.id = id;
		//Set mothership speed
		dx = -80;
	}

	public void move(long timePassed) {
		// if the mothership passes over the screen and exits left, it's destroyed
		if (x < -100) {
			this.destroy();
		}

		super.move(timePassed);
	}

}