package clients;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * File Client Application for (a)
 * 
 * @author Milen Vitanov
 * @version 1.0
 * @since 08.04.2018
 *
 */

public class FileClientA {

	/**
	 * Private properties for the Client's socket connection and its
	 * input/output streams.
	 */
	private static Socket sock;
	private static BufferedReader in;
	private static PrintWriter out;
	
	/**
	 * Client main() function that  initializes the client with 
	 * the server's port, asks the user for a command and if the
	 * command is index it returns all files in server's directory,
	 * else if it is from the form "get file", it returns the contents
	 * of that file if it exists in server's directory, otherwise it 
	 * returns an error message.
 	 * 
	 * @param args command-line arguments
	 * @throws IOException 
	 */
	
	public static void main(String[] args) throws IOException {
		try {
			sock = new Socket("localhost", 8080);
			in = new BufferedReader(new InputStreamReader(System.in));
		} catch (Exception e) {
			System.err.println("Cannot connect to the server!");
		}
		try {
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
			System.out.println("ClientSide");
			System.out.println("Enter a command:");
			String command = in.readLine();
			out.println(command);
			String str;
			BufferedReader serverIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			while(!(str = serverIn.readLine()).equals("END")) {
				System.out.println(str);
			}
			serverIn.close();
			System.out.println("DONE");
		} finally {
			in.close();
			out.close();
			sock.close();
		}

	}

}
