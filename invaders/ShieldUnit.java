
/**
 * The units that the shields are made of
 *
 */
public class ShieldUnit extends Unit {

	/**
	 * Create a new unit to represent the players ship
	 * Load the imagefile, then x and y sets the shields location on screen, and the shield unit is given a unique id
	 */
	public ShieldUnit(String spriteImage, int x, int y, long id) {
		super(spriteImage, x, y);
		this.id = id;

		//The shields never move, so we set the vertical and horizontal movement to 0.
		this.dy = 0;
		this.dx = 0;
	}


}