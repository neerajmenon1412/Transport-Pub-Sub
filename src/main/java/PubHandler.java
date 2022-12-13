/*
 * Handles the Publisher end 
 * binds to the broker send the published message
 * */
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class PubHandler {

    public static void main(String[] args) throws IOException{
        if (args == null || args.length == 0) {
            System.exit(-1);
        }
    }

    private String _pubName = "";
    private String _ip = "";
    int _port;

    public PubHandler(String pubName) throws IOException {
        this._pubName =pubName;
        this._ip = InetAddress.getLocalHost().getHostAddress();
        this._port = Helper.availablePort();
    }

    public void start(String msg) {

        try {
        	
            Socket socket = null;
            FileReader fr = new FileReader("serverinfo.txt");
			try (BufferedReader br = new BufferedReader(fr)) {
				String line = null;
				boolean isconnected = false;
				while ((line = br.readLine()) != null) {
				    String [] values = line.split(" ");
				    String srvname = values[0];
				    String ip = values[1];
				    int port = Integer.valueOf(values[2]);
				    try {
				        socket = new Socket(ip, port);
				    } catch (Exception e){
				        System.err.println("Cannot locate server/broker " + srvname + " " + ip + ":" + port + "\nSending request to backup broker");
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
            if (socket == null) {
                System.err.println("Cannot locate the broker... Please create a new broker(server) to publish message");
            }

            PrintStream ps = new PrintStream(socket.getOutputStream());
            String timeStamp = Helper.currentTime();
            ps.println("Publisher " + this._pubName + " (from " + this._pubName + " at " + timeStamp + "): " + msg);

            ps.flush();
            ps.close();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
            
    }

}


