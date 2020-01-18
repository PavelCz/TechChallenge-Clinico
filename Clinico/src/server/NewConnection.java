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
 */

public class NewConnection implements Runnable {
    /**
     * Private properties for the current socket with its input and output streams
     * as well as the serverDir (server folder).
     */
    private GUI gui;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String serverDir;
    private String outgoingMessage = "[{\"question\":\"Question: Is there pain?\", \"image\":\"https://static.spacecrafted.com/ff54fd0b912c4dd99d383fad9530cb4a/i/bb1044de01b447abbdd734687d094dfb/1/4SoifmQp45JMgBnHp7ed2/pain-points-smaller.jpg\"," +
            "\"choices\":[\"Severe\", \"Moderate\", \"Mild\", \"No pain\"], \"correct\":\"\", \"explanation\":\"\"" +
            "}, {\"question\":\"Question: Is there vomitting?\", \"image\":\"https://previews.123rf.com/images/krisdog/krisdog1906/krisdog190600119/125055526-vomiting-puking-emoji-emoticon-icon-cartoon.jpg\"," +
            "\"choices\":[\"Vimitting without blood\", \"Vomitting with blood\", \"No vomitting\"], \"correct\":\"\"," +
            "\"explanation\":\"\"},{\"question\":\"Question: How many times did you vomit so far?\", \"image\":\"https://cdn0.iconfinder.com/data/icons/audio-and-video-outline-1/24/audio_-30-512.png\"," +
            "\"choices\":[\"1-2 times\",\"3-4 times\",\"5-6 times\",\"No vomitting!\"],\"correct\":\"\"," +
            "\"explanation\":\"\"}, {\"question\":\"Question: How tired are you of writing this stupid code, Milen?\", \"image\":\"https://sayingimages.com/wp-content/uploads/i-dont-know-anymore-guys-i-quit-life-meme.jpg\"," +
            "\"choices\":[\"I don't know!\", \"Do NOT dare ask!\", \"I am strong, I can do it.\"," +
            "\"Desperately need a break!!!\"],\"correct\":\"\",\"explanation\":\"\"}]";

    /**
     * Parametriic constructor
     *
     * @param s   socket of the current new connection
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
     * @param dir   directory look at for files
     * @param files A collection for storing the results
     */
    public static void getFiles(String dir, TreeMap<String, String> files) {

        File directory = new File(dir);
        File[] array = directory.listFiles();

        for (File el : array) {
            if (el.isFile()) {
                files.put(el.getName(), el.getAbsolutePath());
            } else if (el.isDirectory()) {
                getFiles(el.getAbsolutePath(), files);
            }
        }
    }

    public void setGui(GUI gui) {
        this.gui = gui;

    }

    public void setOutgoingMessage(String msg) {
        this.outgoingMessage = msg;
    }

    public void sendToClient(String msg) {
        String json = msg;
        System.out.println("Sending the following message:");
        System.out.println(msg);
        out.println(json);
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            String str;
            while ((str = in.readLine()) != null) {
                System.out.println("Received answer:");
                System.out.println(str);
                if (!str.contains("Hello")) {
                    System.out.println("Sending to GUI...");
                    this.gui.receiveAnswers(str);

				/*
				if(str.equalsIgnoreCase("index")) {
					System.out.println(serverDir);
					TreeMap<String, String> myList = new TreeMap<>();
					//getFiles(serverDir, myList);
					for(String el: myList.keySet()) {
						out.println(el);
					}
					out.println("END");
				} else {
					String json = this.outgoingMessage;
					System.out.println("Sending the following message:");
					System.out.println(this.outgoingMessage);
					out.println(json);
				}*/
                }
            }
        } catch (IOException e) {
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
