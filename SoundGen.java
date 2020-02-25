import java.lang.Math;
import javax.sound.sampled.*;
import java.lang.Thread;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class SoundGen
{
	static class PlaySound extends Thread
	{
		ReentrantLock locker;
		SourceDataLine	line;
		AudioFormat		format;
		byte[]	buff;
		short	s;
		double	c;
		double	d;
		double	b;
		double	vol = 1.0;
		boolean	flag = true;
		double	depth = 0.3;
		double	freq = 1000;
		double	mfreq = 30;
		double	m2freq = 1;
		double	fmr = 44100;
		double	tmax = 10000000000.0;
		double	time = 0;
		public void run()
		{
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
										  format);
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
			System.out.print("Exit\n");
		}
	}

	static void controller(PlaySound playSound)
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
				playSound.flag = false;
				controllerFlag = false;
			}
		}
	}

	public static void main(String[] args)
	{
		PlaySound playSound = new SoundGen.PlaySound();
		playSound.start();
		controller(playSound);
	}
}
