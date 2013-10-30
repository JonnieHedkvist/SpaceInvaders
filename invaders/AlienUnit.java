
/**
 * The unit that represents the aliens
 */
	public class AlienUnit extends Unit {

		private boolean hitsEdge = false;
		private int col;
		private int row;

	/**
		 * Create a new unit to represent the alien
		 * Load the imagefile, then x and y sets the aliens location on screen, and the unit is given row and column and a unique id
	 */
		public AlienUnit(String spriteImage, int x, int y, long id, int rowNr, int colNr) {

			super(spriteImage, x, y);
			this.id = id;

			//Set initial movement speed
			dx = 65;

			//Set the aliens row and column an the alien array
			row = rowNr;
			col = colNr;
		}


		/**
		 * Move unit based on how much time has passed since last movement and its horizontal and vertical speed
	 	 */
		public void move(long timePassed) {
			// if we have reached the left side of the screen and
			// are moving left then the game is notified by setting the aliens hitsEdge to true, so the aliens can speed up, change direction and move down
			if ((dx < 0) && (x < 10)) {
				hitsEdge = true;
			}
			// and vice versa for the right side of the screen
			if ((dx > 0) && (x > 550)) {
				hitsEdge = true;
			}


			super.move(timePassed);
		}


	//Check if alien has hit the edge of the screen
		public boolean hitsEdge()  {
			return hitsEdge;
		}

	//This is called after the aliens have changed direction, to reset the hitsEdge
		public void changeDirection()  {
			hitsEdge = false;
		}



	/**
	 *Get the aliens row and column in the array. This is used ingame to check if alien has a free line of sight to fire its projectiles
	 */
		public int getRow()  {
			return row;
		}

		public int getCol()  {
			return col;
		}


}