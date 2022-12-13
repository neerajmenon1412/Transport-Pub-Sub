/*
 * Used to Get (Read) from Server(Broker)
 * */
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class SubGet extends Thread{

    private String _subName;
    private int _port;

    public SubGet(String name, int port) {
        this._subName = name;
        this._port = port;
    }


    @Override
    public void run() {

        try (ServerSocket serverSocket = new ServerSocket(this._port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                Scanner scn = new Scanner(socket.getInputStream());

                while (scn.hasNextLine()) {
                    String message = scn.nextLine();
                    System.out.println("receive message: " + message);
                    String filename = this._subName+".html";
					File file = new File(filename);
					if (!file.exists()) {
						System.out.println("html file not found");
					}
                   
                    Document doc = Jsoup.parse(file, "UTF-8");
            	    Element div = doc.getElementById("maindiv");
            	    Element subdiv = div.getElementById(this._subName);
            	    subdiv.append("<p>"+message+"</p>");
            	    PrintWriter writer = new PrintWriter(file);
            	    writer.print(doc.outerHtml());
            	    writer.close();
                }
                
                scn.close();
                socket.close();
            }

        } catch (IOException ex) {

        }
        
    }
}
