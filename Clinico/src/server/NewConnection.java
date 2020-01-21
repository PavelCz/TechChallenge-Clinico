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
     */
    private GUI gui;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;


    /**
     * Parametric constructor
     *
     * @param s   socket of the current new connection
     * @throws IOException thrown if something goes wrong with the openning of the input/output stream
     */
    public NewConnection(Socket s) throws IOException {
        socket = s;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }

    public void setGui(GUI gui) {
        this.gui = gui;

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
