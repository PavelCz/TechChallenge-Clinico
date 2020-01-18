package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server Application
 * 
 * @author Milen Vitanov
 * @version 1.0
 * @since 08.04.2018
 *
 */

public class ServerProgram {

	/**
	 * PORT const variable for the server port
	 */
	private static final int PORT = 8080;
	
	/**
	 * Server main() function that initialized the server, 
	 * starts listening for clients and creates a different
	 * thread for every new Client.
	 * 
	 * @param args command-line arguments
	 * @throws IOException 
	 */
	
	public static void main(String[] args) throws IOException {


		GUI gui = new GUI();
		gui.startGUI();


		ServerSocket s = new ServerSocket(PORT);
		System.out.println("Server Started!");
		try {
			while(true) {
				Socket socket = s.accept();
				try {
					//gui.addSocket(socket);
					NewConnection connection = new NewConnection(socket, "/");
					Thread t = new Thread(connection);
					t.start();
					gui.setConnection(connection);
					connection.setGui(gui);
				} catch (IOException e) {
					socket.close();
				}
			}
		} finally {
			s.close();
		}
	}

}
