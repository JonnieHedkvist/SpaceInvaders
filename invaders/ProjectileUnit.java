
/**
 * The unit that represents the players projectiles
 */
public class ProjectileUnit extends Unit {

	private boolean exists;


	/**
	 * Create a new unit to represent the projectile fired by player
	 * Load the imagefile, then x and y sets the projectile location on screen, and the unit is given a unique id
	 */
	public ProjectileUnit(String spriteImage, int x, int y, long id) {
		super(spriteImage, x, y);
		this.id = id;

		//Set vertical speed for projectile
		dy = -400;
	}

	/**
	 * Move unit based on how much time has passed since last movement and its horizontal and vertical speed
	 */
	public void move(long timePassed) {

		super.move(timePassed);

		if (y < 45)  {
			this.destroy();
		}
	}
}