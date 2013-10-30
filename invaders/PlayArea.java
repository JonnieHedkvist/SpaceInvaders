import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.*;

import javax.swing.JPanel;

	/**
	 * This is the Jpanel on wich the game is drawn and played.
	 * the game engine is based on a loop that updates 100 times/second,
	 * which listens for gamechanges, and calls methods accordingly.
	 */

	@SuppressWarnings("unchecked")
	public class PlayArea extends JPanel  {

		private Dimension d;

		private String message = "PRESS ANY KEY";
		private boolean running = false;
		private boolean gameStarted = false;
		private boolean projectileExists = false;
		private boolean motherExists = false;
		private boolean alienDirectionChanged = false;
		private boolean gameLost = false;
		private boolean lostPlayer = false;
		private ArrayList units = new ArrayList();
		private ArrayList aliens = new ArrayList();
		private ArrayList explosions = new ArrayList();
		private Unit player;
		private Unit laser;
		private Unit alien;
		private Unit shield;
		private Unit bomb;
		private Unit mother;
		private double playerSpeed = 260;
		private long unitID;
		private long prevTime;
		private int nrOfAliens;
		private int nrOfLives = 3;
		private int score;
		private int highscore;
		private int level = 0;
		private int exploDelay = 0;
		private Font smallFont = new Font("Courier", Font.BOLD, 18);
		private Font largeFont = new Font("Courier", Font.BOLD, 28);
		private SoundModule alienPop;
		private SoundModule motherPop;
		private SoundModule motherIncoming;
		private SoundModule fire;
		private SoundModule playerDie;
		private SoundModule alienWin;
		protected BufferedImage exploSprite;

		private boolean leftPressed = false;
		private boolean rightPressed = false;
		private boolean spacePressed = false;


		/**
		 * Creates a new playfield
		 */

			public PlayArea()   {

		        d = new Dimension(600, 500);
		        setBackground(Color.black);

		        setDoubleBuffered(true);
		        setVisible(true);
		        setFocusable(true);

		 	//Load the image used for explosions
		        try {
					exploSprite = ImageIO.read(new File("./grafik/boom.gif"));
				} catch (IOException ex) {
				System.out.println("Couldn't find file");
				}

			//initiate all audio clips that will be used in the game
				initAudio();

		    // add a key input listener
				addKeyListener(new KeyInputHandler());

				highscore = getHighscore();

   			}



		/**
		 * This is called the first time the game is started as soon as the user has
		 * pressed any key, and every time a new level is about to begin
		 */

			private void startGame() {
				// clear out any existing units and intialise new ones
				units.clear();
				aliens.clear();
				explosions.clear();
				nrOfAliens = 0;
				unitID = 0;
				initUnits();
				level++;
				running = true;

				// Reset the keyboard
				leftPressed = false;
				rightPressed = false;
				spacePressed = false;
				projectileExists = false;
				lostPlayer = false;
				motherExists = false;
			}




			/**
			 * Draw player, alien and shield units
			 */
			private void initUnits() {

				initPlayer();

				//Draw aliens
				aliens = new ArrayList();
				nrOfAliens = 0;

				//Path to the image that will be used for the units sprite
				String spriteString = "./grafik/alienSmall.gif";
				for(int row = 0; row<4; row++) {
					for (int col = 0; col<8; col++) {

					//Change alien size depending on how far upp in the array they are
						if(row == 1) {
							spriteString = "./grafik/alienMedium.gif";
						}
						if(row > 1) {
							spriteString = "./grafik/alienLarge.gif";
						}

						alien = new AlienUnit(spriteString, 70+(col*54), 80+(row*38), unitID, row, col);
						alien.setHorizontalMovement(alien.getHorizontalMovement() + 2*level);
						//Add the unit to the arrays to be able to cycle through them later
						aliens.add(alien);
						units.add(alien);
						//Increase unit id to give all units a unique id
						unitID++;
						nrOfAliens++;
					}
        		}

        		//Draw 4 shields that consists of 16 shield units each
        		for(int row = 0; row<3; row++) {
					int xPos = 70;
					for (int col = 0; col<16; col++) {
						shield = new ShieldUnit("./grafik/shield.gif", xPos+(col*17), 340+(row*10), unitID);
						units.add(shield);
						unitID++;

						//Create space between each group of 16 shield units
						if(col == 3 || col == 7 || col == 11)  {
							xPos += 60;
						}
					}
				}


			}





			//Player initiated in seperate method to be able to reload without initiating new aliens
			private void initPlayer()  {
				player = new PlayerUnit("./grafik/ship.gif", 290, 415, unitID);
				units.add(player);
				unitID++;
			}


			/**
			 * Create a separate instance of SoundModule for each individual sound,
			 * so that more than one sound can be played at the same time.
			 */
			private void initAudio()  {

				alienPop = new SoundModule("./audio/pop.wav");
				motherPop = new SoundModule("./audio/motherPop.wav");
				motherIncoming = new SoundModule("./audio/motherIncoming.wav");
				fire = new SoundModule("./audio/fire.wav");
				playerDie = new SoundModule("./audio/die.wav");
				alienWin = new SoundModule("./audio/gameLost.wav");
			}






		/**
		 * The game loop that cycles through all game events at a rate of 100 fps.
		 * If the game is paused (player killed, level up etc), running is set to false and no updates will occur.
		 */
		public void loop() {

			// keep looping until the game ends
			while(true)  {
				if (running) {
					// Check how long its been since last update.
					//Used to calculate how far each unit should move this loop
					long timePassed = System.currentTimeMillis() - prevTime;
					prevTime = System.currentTimeMillis();

					//Move all units
					resolveMovement(timePassed);
					//Check if there are now any units that have collided
					resolveHits();
					//Destroy units that have collided
					destroyUnits();



					//If user has pressed the spacebutton, try to launch a projectile
					if(spacePressed)  {
						shoot();
					}

					//Check if any alien will drop a bomb this cycle
					if(nrOfAliens>0)  {
						alienBombs();
					}

					//If there isnt any alien mothership present on screen, try to launch one
					if(!motherExists)  {
						launchMotherShip();
					}

					//If all aliens and eventual mothership are shot down, level is completed
					if(nrOfAliens == 0 && !motherExists)  {
						levelCleared();
					}

					//If player are out of lives, current game will end
					if(nrOfLives == 0)  {
						gameLost();
					}

					//Delay the loop 10ms for a frame rate of 100fps
					try {
						Thread.sleep(10);
					} catch (Exception e) {}

					//Call paint to update graphics
					repaint();
				}
			}


		}



			/**
			 * Attempt to fire a projectile from the player. If theres allready a projectile on screen, no shot will be fired
			 */
			public void shoot() {

				if (projectileExists) {
					return;
				}
				fire.playSound();
				// Initiate new projectile
				laser = new ProjectileUnit("./grafik/projectile.gif", player.getX()+21, player.getY()-22, unitID);
				units.add(laser);
				unitID++;
				projectileExists = true;
			}




			/**
			 * Check wich alien units have a free line of sight (no other aliens below them in the same column),
			 * and try to launch a bomb on random
			 */
			public void alienBombs()  {

				boolean rangeOfFire = true;
				int r1;
				int r2;
				int c1;
				int c2;
				int xPos;
				int yPos;
				int rnd = (int) (Math.random()*200+1);
				ArrayList alienPosition = new ArrayList();

				//Increase the chance of an alien releasing a bomb based on current game level
				if(rnd>(200-level))  {

				// Cycle through aliens, and check who has a free line of sight
					for(int i=0;i<aliens.size();i++) {
						rangeOfFire = true;
						AlienUnit alien1 = (AlienUnit) aliens.get(i);
						r1 = alien1.getRow();
						c1 = alien1.getCol();

						for(int j=0; j<aliens.size();j++) {
							AlienUnit alien2 = (AlienUnit) aliens.get(j);
							r2 = alien2.getRow();
							c2 = alien2.getCol();

							//check for free range of fire
							if((c2 == c1) && (r2 > r1))  {
								rangeOfFire = false;
								break;
							}
						}

						//Add aliens position to list of availible aliens with free line of sight
						if(rangeOfFire)  {
							alienPosition.add(new Point(alien1.getX(), alien1.getY()));
						}

					}

					//Launch a bomb from one of the aliens at random, that has free line of sight
					if(rangeOfFire)  {
						//Randomize aliens
						Collections.shuffle(alienPosition);
						Point p1 = (Point) alienPosition.get(0);
						bomb = new BombUnit("./grafik/bomb.gif", (int)p1.getX()+16, (int)p1.getY()+35, unitID);
						units.add(bomb);
						unitID++;
						//Clear the list of aliens allowed to fire
						alienPosition.clear();
					}
				}
			}



			// Try to launch an alien mothership based on a random number.
			public void launchMotherShip()  {
				int rnd = (int) (Math.random()*900+1);

				if(rnd>899)  {
					mother = new MotherShipUnit("./grafik/motherShip.gif", 600, 50, unitID);
					motherExists = true;
					unitID++;
					units.add(mother);
					motherIncoming.playSound();

				}


			}




			public void paint(Graphics g)   {
			super.paint(g);

			//Draw playfield
				g.setColor(Color.black);
				g.fillRect(0,0,600,500);
				g.setColor(Color.white);
				g.setFont(smallFont);
				g.drawString(message, 230, 150);
				g.drawString("SCORE: " + score, 15, 25);
				g.drawLine(10, 40, 590, 40);


				if(gameStarted)  {

					g.drawString("LEVEL " + level, 255, 25);

					//Draw Units
					for (int i=0; i<units.size(); i++) {
						Unit tempUnit = (Unit) units.get(i);
						g.drawImage(tempUnit.getSprite(), tempUnit.getX(), tempUnit.getY(), this);
					}
					//Draw explosions
					for (int i=0; i<explosions.size(); i++) {
						Point tempPoint = (Point) explosions.get(i);
						g.drawImage(exploSprite, (int)tempPoint.getX(), (int)tempPoint.getY(), this);
					}
					if(explosions.size() > 0)  {
						exploDelay++;
					}
					if(exploDelay > 3 && explosions.size() > 0)  {
						explosions.remove(0);
						exploDelay = 0;
					}

					//Draw remaining lives
					for(int i=1; i<nrOfLives; i++)  {
						g.drawImage(player.getSprite(), 590 - (i*55), 5, this);
					}
				}


				if(gameLost)  {
					g.setColor(Color.black);
					g.fillRect(0,0,600,500);
					g.drawImage(mother.getSprite(), 260, 140, this);
					g.setColor(Color.white);
					g.setFont(largeFont);
					g.drawString("ALL YOUR BASES ARE BELONG TO US!", 30, 220);
					g.drawString("SCORE: " + score, 25, 60);
					g.drawString("HIGHSCORE: " + highscore, 25, 30);
					g.setFont(smallFont);
					g.drawString(message, 150, 250);
				}
				else if(lostPlayer)  {
					g.setColor(Color.black);
					g.fillRect(0,0,600,500);
					g.setColor(Color.white);
					g.drawString("SCORE: " + score, 15, 25);
					g.drawLine(10, 40, 590, 40);
					g.drawString(nrOfLives + "x", 260, 215);
					g.drawImage(player.getSprite(), 285, 190, this);
				}


				Toolkit.getDefaultToolkit().sync();
				g.dispose();

			}




		//Move all units
			public void resolveMovement(long timePassed)  {

				//Cycle through all units and move them
					for (int i=0;i<units.size();i++) {
						Unit tempUnit = (Unit) units.get(i);

						//Check if any alien have hit the edge and should move down and change direction
						if(!alienDirectionChanged)  {
							if(tempUnit instanceof AlienUnit)  {
								if(tempUnit.hitsEdge())  {
									//Change direction for all aliens, move down and speed up
									for (int j=0; j<aliens.size(); j++) {
										AlienUnit tempAlien = (AlienUnit) aliens.get(j);
										tempAlien.changeDirection();
										tempAlien.setY(tempAlien.getY()+10);
										tempAlien.setHorizontalMovement(-(tempAlien.getHorizontalMovement() * 1.07));
									}

									//set to true, because its enough that one alien has hit the edge of the screen.
									//no need to move them all for every alien in the column that has hit the edge.
									alienDirectionChanged = true;
								}
								//Check if aliens reached bottom of screen
								if(tempUnit.getY() > 430)  {
									nrOfLives = 0;
								}
							}
						}
						//move unit according to its speed and direction
						tempUnit.move(timePassed);
					}
				//reset directionChanged
				alienDirectionChanged = false;



			//Check to see if any direction keys are pressed and move player accordingly
				//stop player if no arrow keys are pressed
				player.setHorizontalMovement(0);

				if ((leftPressed) && (!rightPressed)) {
					player.setHorizontalMovement(-playerSpeed);
				} else if ((rightPressed) && (!leftPressed)) {
					player.setHorizontalMovement(playerSpeed);
				}

			}





			//Check once every loop and destroy units that have been hit, or projectiles, motherships that went of the screen
			public void destroyUnits()  {

				for(int i=0;i<units.size();i++) {
					Unit tempUnit = (Unit) units.get(i);
					if(tempUnit.isDestroyed())  {
						if(tempUnit instanceof ProjectileUnit)  {
							//if projectile is destryed, we can now launch a new one.
							projectileExists = false;

						}
						else if(tempUnit instanceof AlienUnit)  {

							nrOfAliens--;
							aliens.remove(tempUnit);
							score += 25;

							//Increase alien speed
							for (int j=0; j<aliens.size(); j++) {
								AlienUnit tempAlien = (AlienUnit) aliens.get(j);
								tempAlien.setHorizontalMovement((tempAlien.getHorizontalMovement() * (1 +(level/10))));
							}

							//If alien is killed, add its position to the explosions array, which will animate an explosion on the screen
							Point exploPosition = new Point((tempUnit.getX()+5), (tempUnit.getY()+15));
							explosions.add(exploPosition);
							alienPop.playSound();
						}
						//If player is hit, deduct lives and paus game
						else if(tempUnit instanceof PlayerUnit)  {
							nrOfLives--;
							running = false;
							playerDie.playSound();
							//This booelan tells the paint() method to draw a new screen while waiting for user to press a key
							lostPlayer = true;

						}
						else if(tempUnit instanceof MotherShipUnit)  {
							motherExists = false;
							//If the mothership is destroyed before it has exited the screen(i.e shot down by player), points are awarded and sound played
							if(tempUnit.getX() > -80) {
								score += 250;
								//Add the motherships position to explosions array.
								Point exploPosition = new Point((tempUnit.getX()+35), (tempUnit.getY()+15));
								explosions.add(exploPosition);
								motherPop.playSound();
							}

						}

					//Finally delete the destroyed unit from the main array
						units.remove(tempUnit);
					}
				}
			}





		/**
		 * Check if any units occupy the same space, and destroy them.
		 * This is done by cycling trough all units, getting a rectangle
		 * with the same dimensions as the units sprite, and compare them
		 * against eachother. If the rwctangles intersects, it counts as a
		 * hit. The units are set to be destroyed the next loop.
		 */
			public void resolveHits()  {
				for(int i=0;i<units.size();i++) {
					Unit unit1 = (Unit) units.get(i);
					Rectangle r1 = unit1.getPosition();

					for(int j=0; j<units.size();j++) {
						Unit unit2 = (Unit) units.get(j);
						Rectangle r2 = unit2.getPosition();

						//Make sure it does not trigger on itself by getting the units unique id
						if((r1.intersects(r2)) && (unit1.getID() != unit2.getID()))  {

							//If alien hits shields, they should all be destroyed
							if((unit1 instanceof AlienUnit && unit2 instanceof ShieldUnit) || (unit2 instanceof AlienUnit && unit1 instanceof ShieldUnit))  {
								destroyShields();
							//If aliens hits player, the aliens return to the top of the screen, but keeps their speed
							}else if((unit1 instanceof AlienUnit && unit2 instanceof PlayerUnit) || (unit2 instanceof AlienUnit && unit1 instanceof PlayerUnit))  {
								alienReset();
								unit1.destroy();
								unit2.destroy();

							}else  {
								unit1.destroy();
								unit2.destroy();
							}
						}
					}
				}
			}



		//All eliens destroyed
			public void levelCleared()  {
				//pause game and wait for key input
				running = false;
				message = "ALIENS DEFEATED! PREPARE FOR NEXT LEVEL...";
			}


		//Player has lost all lives
			public void gameLost()  {
				//paus for a second before displaying game over screen
				try {Thread.sleep(1000);} catch (Exception e) {}
				message = "(PRESS ANY KEY TO TRY AGAIN)";
				alienWin.playSound();

				//If score is higher than current highscore, overwrite it.
				if(score > getHighscore()) {
					setHighscore();
				}

				//when set to true, paint() method will display the game over screen
				gameLost = true;
				running = false;
			}

		//Aliens have hit the player and should return to the top of the screen, keeping their x value and speed
			public void alienReset()  {
				for(int i=0;i<aliens.size();i++) {
					AlienUnit tempUnit = (AlienUnit) aliens.get(i);
					tempUnit.setY(80 + (tempUnit.getRow()*38));
				}
			}

		//An alien hit a shield, and all shields should be destroyed
			public void destroyShields()  {
				for(int i=0;i<units.size();i++) {
					Unit tempUnit = (Unit) units.get(i);
					if(tempUnit instanceof ShieldUnit)  {
						tempUnit.destroy();
					}
				}
			}

		//Called when player has a lost a life and removes all bombs, so we don't have to respawn right infront of another alien bomb
			public void removeBombs()  {
				for(int i=0;i<units.size();i++) {
					Unit tempUnit = (Unit) units.get(i);
					if(tempUnit instanceof BombUnit)  {
						units.remove(tempUnit);
					}
				}
			}




   	//Key listener class listens for keyinput by the player.

			private class KeyInputHandler extends KeyAdapter {

				public void keyPressed(KeyEvent e) {
					//Left arrow key
					if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						leftPressed = true;
					}
					//Right arrow key
					if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						rightPressed = true;
					}
					//Spacebar
					if (e.getKeyCode() == KeyEvent.VK_SPACE) {
						spacePressed = true;
					}
				}



				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						leftPressed = false;
					}
					if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						rightPressed = false;
					}
					if (e.getKeyCode() == KeyEvent.VK_SPACE) {
						spacePressed = false;
					}
				}


				public void keyTyped(KeyEvent e) {
					//If game is paused and waiting for "press any key".
					if (!running) {
						prevTime = System.currentTimeMillis();

					//If the game-over screen is displayed
						if(gameLost)  {
							//reset values and start a new game
							gameLost = false;
							message = "";
							nrOfLives = 3;
							level = 0;
							score = 0;
							startGame();
							gameStarted = true;
							running = true;
						}
					//If player has lost a life and lostPlayer screen is displayed
						else if(nrOfAliens > 0)  {
							//Add a new player and remove all bombs
							initPlayer();
							lostPlayer = false;
							running = true;
							removeBombs();
						}
					//This is executed every time the player completed a level(or just started the game), and starts a new one
						else {
							gameLost = false;
							gameStarted = true;
							//remove text from the screen
							message = "";
							startGame();
						}
					}

					// if player hits escape, quit the game
					if (e.getKeyChar() == 27) {
						System.exit(0);

					}
				}
			}



		//Reads the score.txt file, and return the hichscore value
			public int getHighscore()  {

				try {
					Scanner scoreReader = new Scanner(new File("./highscore/score.txt"));
					while(scoreReader.hasNext())  {
						highscore = Integer.parseInt(scoreReader.nextLine());
					}
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}

				return highscore;


			}



		//Called if score is higher than current highscore and overwrites it
			public void setHighscore()  {

				highscore = score;

				Writer w = null;
				try {
					File file = new File("./highscore/score.txt");
					w = new BufferedWriter(new FileWriter(file));
					String scoreString = "" + score;
					w.write(scoreString);
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}

				finally {
				try {
					if (w != null) {
						w.close();
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}

				}
			}



	}