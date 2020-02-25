import java.lang.Math;
import javax.sound.sampled.*;
import java.lang.Thread;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

class PlaySound extends Thread {
	
	public ReentrantLock 	locker;
	private SourceDataLine	line;
	private AudioFormat		format;
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

	public boolean getFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

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
	
	public void run() {

		locker = new ReentrantLock();
		buff = new byte[2];
		if (line == null) {
			format = new AudioFormat(
					44100,
					16,
					1,
					true,
					true
					);
			DataLine.Info dataLineInfo = new DataLine.Info(
					                  SourceDataLine.class,
									  format
									  );
			try{
				line = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
				line.open(format);
			} catch (Exception e){
				System.out.println(e);
				System.exit(0);
			}
		}
		line.start();
		while (time < tmax && flag)
		{
			c = 32768 * vol * Math.sin((double)time * 2 * Math.PI * (freq / fmr));
			d = 0.5 + 0.5 * Math.sin((double)time * 2 * Math.PI * (mfreq / fmr));
			b = 0.5 + 0.5 * depth * Math.sin((double)time * 2 * Math.PI * (m2freq / fmr));
			s = (short)(c *d * b);
			buff[1] = (byte)(s & 0x00ff);
			buff[0] = (byte)((s & 0xff00) >> 8);
			line.write(buff, 0, 2);
			locker.lock();
			locker.unlock();
			time++;
		}
		line.drain();
		line.stop();
		line.close();
		System.out.println("Bye");
	}
}

public class SoundGen {
	
	private static void controller(PlaySound playSound)
	{
		Scanner scanner = new Scanner(System.in);
		String	command;
		boolean	controllerFlag = true;
		while(controllerFlag)
		{
			command = scanner.nextLine();
			if (command.equals("play")) {
				try {	
				playSound.locker.unlock();
				} catch (Exception e) {
					System.out.println(e);
				}
			}
			else if (command.equals("pause")) {
				try {	
				playSound.locker.lock();
				} catch (Exception e) {
					System.out.println(e);
				}
			}
			else if (command.equals("exit"))
			{
				playSound.setFlag(false);
				controllerFlag = false;
			}
		}
	}

	public static void main(String[] args) {
	
		PlaySound playSound = new PlaySound();
		playSound.start();
		controller(playSound);
	}
}
