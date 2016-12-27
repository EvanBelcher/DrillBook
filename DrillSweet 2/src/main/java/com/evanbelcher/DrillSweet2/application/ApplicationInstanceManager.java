package com.evanbelcher.DrillSweet2.application;

import com.evanbelcher.DrillSweet2.data.State;

import java.io.*;
import java.net.*;

/**
 * Makes application single-instance
 * http://www.rbgrn.net/content/43-java-single-application-instance
 *
 * @author Robert Green
 */
public class ApplicationInstanceManager {

	private static ApplicationInstanceListener subListener;

	/**
	 * Randomly chosen, but static, high socket number
	 */
	public static final int SINGLE_INSTANCE_NETWORK_SOCKET = 44331;

	/**
	 * Must end with newline
	 */
	public static final String SINGLE_INSTANCE_SHARED_KEY = "$$NewInstance$$\n";

	/**
	 * Registers this instance of the application.
	 *
	 * @return true if first instance, false if not.
	 */
	public static boolean registerInstance() {
		// returnValueOnError should be true if lenient (allows app to run on network error) or false if strict.
		final boolean returnValueOnError = true;
		// try to open network socket
		// if success, listen to socket for new instance message, return true
		// if unable to open, connect to existing and send new instance message, return false
		try {
			final ServerSocket socket = new ServerSocket(SINGLE_INSTANCE_NETWORK_SOCKET, 10, InetAddress.getLocalHost());
			State.print("Listening for application instances on socket " + SINGLE_INSTANCE_NETWORK_SOCKET);
			Thread instanceListenerThread = new Thread(() -> {
				boolean socketClosed = false;
				while (!socketClosed) {
					if (socket.isClosed()) {
						socketClosed = true;
					} else {
						try {
							Socket client = socket.accept();
							BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
							String message = in.readLine();
							if (SINGLE_INSTANCE_SHARED_KEY.trim().equals(message.trim())) {
								State.print("Shared key matched - new application instance found");
								fireNewInstance();
							}
							in.close();
							client.close();
						} catch (IOException e) {
							socketClosed = true;
						}
					}
				}
			});
			instanceListenerThread.start();
			// listen
		} catch (UnknownHostException e) {
			State.print(e.getMessage(), e);
			return returnValueOnError;
		} catch (IOException e) {
			State.print("Port is already taken.  Notifying first instance.");
			try {
				Socket clientSocket = new Socket(InetAddress.getLocalHost(), SINGLE_INSTANCE_NETWORK_SOCKET);
				OutputStream out = clientSocket.getOutputStream();
				out.write(SINGLE_INSTANCE_SHARED_KEY.getBytes());
				out.close();
				clientSocket.close();
				State.print("Successfully notified first instance.");
				return false;
			} catch (UnknownHostException e1) {
				State.print(e.getMessage(), e);
				return returnValueOnError;
			} catch (IOException e1) {
				State.print("Error connecting to local port for single instance notification");
				State.print(e1.getMessage(), e1);
				return returnValueOnError;
			}

		}
		return true;
	}

	/**
	 * Sets the application instance listener to the given listener
	 *
	 * @param listener
	 */
	public static void setApplicationInstanceListener(ApplicationInstanceListener listener) {
		subListener = listener;
	}

	/**
	 * Calls the application instance listener's instance created method
	 */
	private static void fireNewInstance() {
		if (subListener != null) {
			subListener.newInstanceCreated();
		}
	}
}
