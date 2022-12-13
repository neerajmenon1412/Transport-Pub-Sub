/*
 * Used for Subscribe Post(Write) to Server(broker)
 * */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class SubPost{

    private String _subName;

    public SubPost(String subName) {
        this._subName = subName;
    }

 
    public void sendPost(String topic) {

            String command = topic;

            try {
                Socket socket = null;
                FileReader fr = new FileReader("serverinfo.txt");
                BufferedReader br = new BufferedReader(fr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    String[] serverfields = line.split(" ");
                    String serverIP = serverfields[1];
                    int serverPort = Integer.valueOf(serverfields[2]);
                    boolean isconnected = false;
                    try {
                        socket = new Socket(serverIP, serverPort);
             
                    } catch (Exception e) {
                        continue;
                    }
    			    isconnected = true;
    			    if (isconnected) {
    			        break;
    			    }

                    PrintStream ps = new PrintStream(socket.getOutputStream());
                    ps.println("subscriber " + _subName + " " + command);

                    ps.close();
                    socket.close();
                    break;
                }


                br.close();
                fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
    }


}
