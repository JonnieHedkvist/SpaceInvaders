

/**
 * The unit that represents the players ship
 */
public class PlayerUnit extends Unit {

	/**
	 * Create a new unit to represent the players ship
	 * Load the imagefile, then x and y sets the ships location on screen, and the unit is given a unique id
	 */
	public PlayerUnit(String spriteImage, int x, int y, long id) {
		super(spriteImage, x, y);

		this.id = id;
	}

	/**
	 * Move unit based on how much time has passed since last movement and its horizontal and vertical speed
	 */
	public void move(long timePassed) {
		// if ship is moving left and reached the left side of the screen, don't move
		if ((dx < 0) && (x < 10)) {
			return;
		}
		// if ship is moving right and reached the right side of the screen, don't move
		if ((dx > 0) && (x > 530)) {
			return;
		}

		super.move(timePassed);
	}


}