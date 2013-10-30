import javax.swing.JFrame;


/**
 * Construct a new window on screen and load a new game on it
 */
public class SpaceInvaders extends JFrame {
	private PlayArea newGame;

    public SpaceInvaders()  {

		setTitle("Space Invaders");

    //initiate game
        newGame = new PlayArea();
        add(newGame);

	//Center the window on screen
		setLocationRelativeTo(null);
        setSize(600, 500);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

	//Start the game loop
        newGame.loop();
    }



    public static void main(String[] args) {
        SpaceInvaders s = new SpaceInvaders();
    }
}