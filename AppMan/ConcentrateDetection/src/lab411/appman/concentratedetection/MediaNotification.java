package lab411.appman.concentratedetection;

import com.example.concentratedetection.R;

import android.content.Context;
import android.media.MediaPlayer;



public class MediaNotification {
	private static MediaPlayer mp;
	public static final int DELETE_MODE = 0;
	public static final int CHANGE_MODE = 1;
	public static final int GET_ERROR = 2;
	public static final int ADDED_MODE = 3;
	public static final int EDITTED_MODE = 4;

	/**
	 * Manager of SoundsS
	 */
	public static void managerOfSound(Context context, int command) {
		if (mp != null) {
			mp.reset();
			mp.release();
		}
		switch (command) {
		case DELETE_MODE:
			mp = MediaPlayer.create(context, R.raw.delete);
			break;
		case CHANGE_MODE:
			mp = MediaPlayer.create(context, R.raw.notice);
			break;
		case GET_ERROR:
			mp = MediaPlayer.create(context, R.raw.error);
			break;
		case ADDED_MODE:
			mp = MediaPlayer.create(context, R.raw.added);
			break;
		case EDITTED_MODE:
			mp = MediaPlayer.create(context, R.raw.editted);
			break;
		}
		mp.start();
	}
}
