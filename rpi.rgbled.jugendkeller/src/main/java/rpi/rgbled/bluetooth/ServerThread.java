package rpi.rgbled.bluetooth;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class ServerThread implements Runnable {

	public ServerThread() {
	}

	@Override
	public void run() {
		// retrieve the local bluetooth device object
		LocalDevice local = null;

		StreamConnectionNotifier notifier;
		StreamConnection connection = null;

		// setup the server to listen for connection
		try {
			local = LocalDevice.getLocalDevice();
			local.setDiscoverable(DiscoveryAgent.GIAC);
			//"04c6093b-0000-1000-8000-00805f9b34fb";
			//final String serviceUUID = "da3a08038b1b4a3d85155bf87c0615c0";
			final String serviceUUID =   "04c6093b00001000800000805f9b34fb";
			UUID uuid = new UUID(serviceUUID, false); //80087355);
			String url = "btspp://localhost:" + uuid.toString()
					+ ";name=RemoteBluetooth";
			notifier = (StreamConnectionNotifier) Connector.open(url);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		// waiting for connection
		while (true) {
			try {
				System.out.println("waiting for connection...");
				connection = notifier.acceptAndOpen();

				Thread processThread = new Thread(new ConnectionThread(
						connection));
				processThread.start();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

}
