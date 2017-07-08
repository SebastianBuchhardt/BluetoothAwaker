package rpi.rgbled.bluetooth;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.microedition.io.StreamConnection;

import audio.MusicPlayer;
import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;


public class ConnectionThread implements Runnable {
	private static final int PIN_RED = 17;
	private static final int PIN_GREEN = 22;
	private static final int PIN_BLUE = 24;

	private JPigpio pigpio;

	private StreamConnection mConnection;

	// Constant that indicate command from devices
	private static final int EXIT_CMD = -1;

	public ConnectionThread(StreamConnection connection) {
		mConnection = connection;
		// connect to pigpio via socket interface
		// pigpio = new PigpioSocket(host, 8888);
		/*pigpio = new Pigpio(); // connect to pigpio directly/locally
		try {
			pigpio.gpioInitialize();
			Utils.addShutdown(pigpio);
		} catch (PigpioException e) {
			e.printStackTrace();
		}*/
	}

	@Override
	public void run() {
		try {
			// prepare to receive data
			InputStream inputStream = mConnection.openInputStream();

			System.out.println("waiting for input");

			String message = "";
			while (true) {
				int command = inputStream.read();
				char letter = (char) command;

				if (command == EXIT_CMD) {
					System.out.println("finish process");
					break;
				}
				message = message.concat(String.valueOf(letter));
			}
			processCommand(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Process the command from client
	 * 
	 * @param command
	 *            the command code
	 */
	private void processCommand(String command) {
		try {
			switch (command.substring(0, 3)) {
			case "led": // led:#ffRRGGBB
				sendLEDCommands(command.substring(5));
				break;
			case "mus":
				sendMusicCommands(command.substring(6));
				break;
			case "fil":
				//prepareReceivingFile();
				break;
			}
			System.out.println("Received command:\t" + command);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendLEDCommands(String cmd) {
		// change the brightness via Pulse Width Modulation
		final String brightness = cmd.substring(0, 2);
		final String red = cmd.substring(2, 2);
		final String green = cmd.substring(4, 2);
		final String blue = cmd.substring(6, 2);

		changeColor(Integer.valueOf(brightness), Integer.valueOf(red),
				Integer.valueOf(green), Integer.valueOf(blue));
	}

	private void sendMusicCommands(String cmd) {
		final String cmd_part[] = cmd.split("=");
		switch (cmd_part[0]) {
		case "play":
			MusicPlayer.play();
			break;
		case "resume":
			MusicPlayer.pause();
			break;
		case "stop":
			MusicPlayer.stop();
			break;
		case "dbTitle":
			MusicPlayer.loadSong("test.wav");
			break;
		}
	}

	private void prepareReceivingFile() {
		try {
			// prepare to receive data
			InputStream is = mConnection.openInputStream();

			System.out.println("waiting for file");

			FileOutputStream fos = new FileOutputStream(
					"F:/Users/Basti/Music/Audials Imported Music/Empfangene Datei.mp3",
					true);
			byte[] buffer = new byte[1024 * 8];
			try {
				int bytesRead = is.read(buffer, 0, buffer.length);
				int current = bytesRead;

				do {
					bytesRead = is.read(buffer, current, buffer.length
							- current);

					fos.write(buffer);

					if (bytesRead >= 0)
						current += bytesRead;
				} while (bytesRead > -1);

			} catch (IOException e) {
				e.printStackTrace();

			} finally {
				fos.flush();
				fos.close();
				System.out.println("File received");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void changeColor(int brightness, int red, int green, int blue) {
		System.out.println("Changed color to R="+String.valueOf(red)+", \t G="+String.valueOf(green)+", \t B="+String.valueOf(blue)+" with brightness="+String.valueOf(brightness)+"%");
		/*try {
			pigpio.setPWMDutycycle(PIN_RED, red);
			pigpio.setPWMDutycycle(PIN_GREEN, green);
			pigpio.setPWMDutycycle(PIN_BLUE, blue);
		} catch (PigpioException e) {
			e.printStackTrace();
		}*/
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		/*try {
			pigpio.gpioTerminate();
		} catch (PigpioException e) {
			e.printStackTrace();
		}*/
	}

	/**
	 * A thread that connects to a selected OPP Server and pushes a single file
	 * with metadata.
	 */
	class OPPConnectThread extends Thread {
		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			// try {
			// Connection connection = null;
			// OutputStream outputStream = null;
			// Operation putOperation = null;
			// ClientSession cs = null;
			// try {
			// // Send a request to the server to open a connection
			// connection = Connector.open(_url);
			// cs = (ClientSession) connection;
			// cs.connect(null);
			// // updateStatus("[CLIENT] OPP session created");
			//
			// // Send a file with meta data to the server
			// final byte filebytes[] = "[CLIENT] Hello..".getBytes();
			// final HeaderSet hs = cs.createHeaderSet();
			// hs.setHeader(HeaderSet.NAME, "test.txt");
			// hs.setHeader(HeaderSet.TYPE, "text/plain");
			// hs.setHeader(HeaderSet.LENGTH, new Long(filebytes.length));
			//
			// putOperation = cs.put(hs);
			// // updateStatus("[CLIENT] Pushing file: " + "test.txt");
			// // updateStatus("[CLIENT] Total file size: "
			// // + filebytes.length + " bytes");
			//
			// outputStream = putOperation.openOutputStream();
			// outputStream.write(filebytes);
			// // updateStatus("[CLIENT] File push complete");
			// } finally {
			// outputStream.close();
			// putOperation.close();
			// cs.disconnect(null);
			// connection.close();
			// // updateStatus("[CLIENT] Connection Closed");
			// }
			// } catch (final Exception e) {
			// // BluetoothJSR82Demo.errorDialog(e.toString());
			// }
		}
	}

	private void receiveFile(Socket socket) throws FileNotFoundException {
		// Attach the i/p stream to the socket
		InputStream is;
		try {
			is = socket.getInputStream();
			// Create output streams & write to file
			FileOutputStream fos = new FileOutputStream(
					"F:/Users/Basti/Music/Audials Imported Music/Empfangene Datei.mp3",
					true);
			byte[] buffer = new byte[1024 * 8];
			try {
				int bytesRead = is.read(buffer, 0, buffer.length);
				int current = bytesRead;

				do {
					bytesRead = is.read(buffer, current, buffer.length
							- current);

					fos.write(buffer);

					if (bytesRead >= 0)
						current += bytesRead;
				} while (bytesRead > -1);

			} catch (IOException e) {
				e.printStackTrace();

			} finally {
				fos.flush();
				fos.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void sender(String musikDatei, String musikDateiSignal,
			String clientIP) {
		// File file = new File("/home/akrillo/Downloads/webradio.wav");
		//
		// OutputStream outputStream = new FileOutputStream(file);
		// byte[] bufferFile = new byte[3812];
		// int rounds = (int) Math.floor(fileSize / bufferFile.length);
		// if (rounds == 0)
		// rounds = 1;
		// for (int i = 0; i <= (rounds); i++) {
		// in.read(bufferFile);
		// outputStream.write(bufferFile);
		// }
		// outputStream.flush();
	}

	private void receiver(String msg, InputStream in) {
		// OutputStream out = socket.getOutputStream();
		// InputStream in = socket.getInputStream();
		// // ...
		// byte[] buffer = new byte[16384];
		// java.io.InputStream inputStream = new FileInputStream(musikDatei);
		// int len = 0;
		// while ((len = inputStream.read(buffer)) > 0) {
		// out.write(buffer);
		// }
		// System.out.println(clientIP + ": Musik - " + "sended");
	}

	private void createSocket() {
		// try {
		// int port = Integer.parseInt(args[0]);
		// int calls = 0;
		// System.out.println("Listening Port to: " + port);
		// ServerSocket server_socket = new ServerSocket(port);
		// while (true) {
		// Socket socket = server_socket.accept();
		// new Thread(new WebradioClientThread(++calls, socket)).run();
		// }
		// } catch (IOException e) {
		// System.err.println("Connection error: " + e.toString());
		// System.exit(1);
		// }
	}
}

