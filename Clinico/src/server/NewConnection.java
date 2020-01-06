package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.TreeMap;

/**
 * Server Multithreaded Connections class
 * 
 * @author Milen Vitanov
 * @version 1.0
 * @since 08.04.2018
 *
 */

public class NewConnection implements Runnable{
	/**
	 * Private properties for the current socket with its input and output streams
	 * as well as the serverDir (server folder).
	 */
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private String serverDir;
	
	/**
	 * Parametriic constructor
	 * 
	 * @param s socket of the current new connection
	 * @param dir the server folder address
	 * @throws IOException thrown if something goes wrong with the openning of the input/output stream
	 */
	public NewConnection(Socket s, String dir) throws IOException {
		socket = s;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		serverDir = dir;
	}
	
	/**
	 * A function for adding all the files' absolute paths in dir and its subdirectories
	 * to a Treemap. The key is a file's name adn the value is its absolute path.
	 * 
	 * @param dir directory look at for files
	 * @param files A collection for storing the results
	 */
	public static void getFiles(String dir,  TreeMap<String, String> files) {
		
		File directory = new File(dir);
		File[] array = directory.listFiles();
		
		for(File el:array) {
			if(el.isFile()) {
				files.put(el.getName(), el.getAbsolutePath());
			} else if(el.isDirectory()) {
				getFiles(el.getAbsolutePath(), files);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			String str;
			while((str = in.readLine()) != null) {
				System.out.println(str);
				if(str.equalsIgnoreCase("index")) {
					System.out.println(serverDir);
					TreeMap<String, String> myList = new TreeMap<>();
					getFiles(serverDir, myList);
					for(String el: myList.keySet()) {
						out.println(el);
					}
					out.println("END");
				} else if(str.toLowerCase().substring(0, 4).equals("get ")) {
					String fileName = str.substring(4);
					TreeMap<String, String> myList = new TreeMap<>();
					getFiles(serverDir, myList);
					boolean flag = false;
					for(String el: myList.keySet()) {
						System.out.println(el);
						if(el.equals(fileName)) {
							out.println("ok");
							BufferedReader fileReader = new BufferedReader(new FileReader(new File(myList.get(el))));
							try { 
								String line;
								while((line = fileReader.readLine()) != null) {
									System.out.println(line);
									out.println(line);
								}
								out.println("END");
								flag = true;
							} finally {
								fileReader.close();
							}
						}
					}
					if(!flag) {
						out.println("error");
					}
				} else {
					out.println("Use get + name of file");
				}
			} 
		}catch(IOException e) {
			e.printStackTrace();	
			System.err.println("IOException");
		} finally {
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e) {
				System.err.println("Socket not closed!");
			}
		}
	}
}
