import java.lang.Math;
import java.lang.Thread;
import java.util.Scanner;
import android.media.AudioTrack;
import android.media.AudioManager;
import android.media.AudioFormat;

class PlaySound extends Thread {

	private AudioTrack	track;
	private byte[]	buff;
	private short	s;
	private double	c;
	private double	d;
	private double	b;
	private double	vol;
	private boolean	flag;
	private double	depth;
	private double	freq;
	private double	mfreq;
	private double	m2freq;
	private double	fmr;
	private double	tmax;
	private double	time;

	public PlaySound() {

		vol = 1.0;
		flag = true;
		depth = 0.3;
		freq = 1000;
		mfreq = 30;
		m2freq = 1;
		fmr = 44100;
		tmax = 10000000000.0;
		time = 0;
	}

	public void		run() {

		buff = new byte[2];
		track = new AudioTrack(
				AudioManager.STREAM_MUSIC,
				44100,
				AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				2,
				AudioTrack.MODE_STREAM
				);
		track.play();
		
		while (time < tmax && flag)
		{
			c = 32768 * vol * Math.sin((double)time * 2 * Math.PI * (freq / fmr));
			d = 0.5 + 0.5 * Math.sin((double)time * 2 * Math.PI * (mfreq / fmr));
			b = 0.5 + 0.5 * depth * Math.sin((double)time * 2 * Math.PI * (m2freq / fmr));
			s = (short)(c * d * b);
			buff[1] = (byte)(s & 0x00ff);
			buff[0] = (byte)((s & 0xff00) >> 8);
			track.write(buff, 0, 2);
			time++;
		}

		System.out.print("Done!\n");
	}
}

public class SoundGen {

	private static void controller() {

		Scanner scanner = new Scanner(System.in);
		String	command;
		while(PlaySound.flag)
		{
			command = scanner.nextLine();
			if (command.equals("exit"))
				PlaySound.flag = false;
		}
	}
	
	public static void main(String[] args) {
		
		PlaySound playSound = new PlaySound();
		playSound.start();
		controller();
	}
}
