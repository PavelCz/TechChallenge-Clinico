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
		ServerSocket s = new ServerSocket(PORT);
		System.out.println("Server Started!");
		try {
			while(true) {
				Socket socket = s.accept();
				try {
					Thread t = new Thread(new NewConnection(socket, args[0]));
					t.start();
				} catch (IOException e) {
					socket.close();
				}
			}
		} finally {
			s.close();
		}
	}

}
