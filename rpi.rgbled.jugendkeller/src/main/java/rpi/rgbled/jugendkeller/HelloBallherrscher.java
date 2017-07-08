package rpi.rgbled.jugendkeller;

import rpi.rgbled.bluetooth.ServerThread;

public class HelloBallherrscher {

	public static void main(String[] args) {
		Thread waitThread = new Thread(new ServerThread());
		waitThread.start();
	}
}
