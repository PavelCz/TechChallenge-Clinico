package clients;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * File Client Application for (b)
 * 
 * @author Milen Vitanov
 * @version 1.0
 * @since 08.04.2018
 *
 */

public class FileClientB {

	/**
	 * Private properties for the Client's socket connection and its
	 * input/output streams. 
	 */
	private static Socket sock;
	private static BufferedReader in;
	private static PrintWriter out;

	/**
	 * Client main() function that  initializes the client with 
	 * the server's port, and gives the user the following 3 options:
	 * <p>
	 * 1.Get a list of the files that are available on the server;
	 * 2.Display the list;
	 * 3.Select and get a copy of a specified file from the server and save it to a local file.
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
			String command, str;
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
			BufferedReader serverIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("ClientSide");
			System.out.println("Pick one of the following choices: \n 1.Get a list of the files that are available on the server");
			System.out.println(" 2.Display the list\n 3.Select and get a copy of a specified file from the server and save it to a local file");
			int choice = Integer.parseInt(in.readLine());
			switch(choice) {
				case 1:
					command = "index";
					out.println(command);
					ArrayList<String> fileNames = new ArrayList<>();
					while(!(str = serverIn.readLine()).equals("END")) {
						fileNames.add(str);
					}
					System.out.println("An array containing all the file names was successfully created");
					break;
				case 2:
					command  = "index";
					out.println(command);
					while(!(str = serverIn.readLine()).equals("END")) {
						System.out.println(str);
					}
					break;
				case 3:
					
					command = "index";
					out.println(command);
					ArrayList<String> fileNames2 = new ArrayList<>();
					System.out.println("Pick one of the following files: ");
					while(!(str = serverIn.readLine()).equals("END")) {
						fileNames2.add(str);
						System.out.println(str);
					}
					String answer = in.readLine();
					boolean flag = true;
					while (flag) {
						for (String x : fileNames2) {
							if(answer.equals(x)) {
								flag = false;
								break;
							}
						}
						if (flag) {
							System.out.println("Invalid input! Pick again:");
							answer = in.readLine();						
						}
					}
					out.println("get " + answer);
					serverIn.readLine(); // get read of the 'ok' message
					final String NEWFILENAME= "copy" + answer;
					PrintWriter fileCopy = new PrintWriter(new FileWriter(NEWFILENAME));
					
					while(!(str = serverIn.readLine()).equals("END")) {
						System.out.println(str);
						fileCopy.println(str);
					}
					fileCopy.flush();
					fileCopy.close();
					break;	
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
