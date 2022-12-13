/*
 * Handles the subscriber end
 * Binds to the broker to carry out Subscribe and Unsubscribe operations
 * */

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class SubHandler {

    private String _subName;
    private int _port;
    
    public SubHandler(String name) throws IOException{
        this._subName = name;
    }

    public void createSub() throws IOException{
        String ip = InetAddress.getLocalHost().getHostAddress();
        this._port = Helper.availablePort();

        //Access serverinfo.txt for server information
        Socket socket = null;
        File serverinfo = new File("serverinfo.txt");
        FileReader fr = new FileReader(serverinfo);
        try (BufferedReader br = new BufferedReader(fr)) {
			String line = null;
			while ((line = br.readLine()) != null) {
			    boolean isconnected = false;
			    String[] serverfields = line.split(" ");
			    String serverIP = serverfields[1];
			    int serverPort = Integer.valueOf(serverfields[2]);
			    try {
			        socket = new Socket(serverIP, serverPort);
			    } catch (Exception ex) {
			        continue;
			    }
			    isconnected = true;
			    if (isconnected) {
			        break;
			    }

			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //Inform server that the subscriber is online
        PrintStream ps = new PrintStream(socket.getOutputStream());
        ps.println("Subscriber " + _subName + " online " + ip + " " + this._port);
 
        ps.close();
        socket.close();

    }
    
    public void startGet() {
        //Start Get (Read from Server)
		SubGet subscriberGet = new SubGet(this._subName, this._port);
		subscriberGet.start();

    }
   
   public void subscribe(String msg, String action) throws IOException{
	   
       Socket socket = null;
       File serverinfo = new File("serverinfo.txt");
       FileReader fr = new FileReader(serverinfo);
       try (BufferedReader br = new BufferedReader(fr)) {
			String line = null;
			while ((line = br.readLine()) != null) {
			    boolean connected = false;
			    String[] serverfields = line.split(" ");
			    String serverIP = serverfields[1];
			    int serverPort = Integer.valueOf(serverfields[2]);
			    try {
			        socket = new Socket(serverIP, serverPort);
			    } catch (Exception ex) {
			        continue;
			    }
			    connected = true;
			    if (connected) {
			        break;
			    }

			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       PrintStream ps = new PrintStream(socket.getOutputStream());
	   ps.println("Subscriber " + _subName + " " +  action + " " + msg);
       ps.close();
       socket.close();
   }

}
