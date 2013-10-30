import java.util.*;
import java.applet.*;
import java.net.*;

/**
 * Class for playing audio clips during gameplay.
 */


public class SoundModule {

	private AudioClip sound;


	//clipname is a string of the url to the audiofile
    public SoundModule(String clipname)  {

		try {
		   sound = Applet.newAudioClip(new URL("file:" + clipname));
		}
		catch (Exception e) {
			System.out.println("Failed to load audioclip: " + clipname);
			System.exit(0);
		}
	}


	public void playSound()  {
		sound.play();
	}

}