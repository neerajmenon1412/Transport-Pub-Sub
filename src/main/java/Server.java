/*
 * Acts as the broker and handles 
 * all requests from the Publisher and Subscribers
 * Also creates the txt files
 * */

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server extends Thread{
    public static void main(String[] args) throws IOException{
        if (args == null || args.length == 0) {
            System.exit(-1);
        }
    }
    
    private String _serverName;
    private int _port;
    public ServerSocket serverSocket;
    String server_file = "serverinfo.txt";
    String relation_file = "pubsubrelation.txt";
    String subscriber_file = "subscriberinfo.txt";

    public Server(String srvName) throws IOException{
        this._serverName = srvName;
        String ip = InetAddress.getLocalHost().getHostAddress();
        _port = availablePort();
        System.out.println("Broker(Server) ip:" + ip + " port:" + _port + " start..");

        //ip address written into serverinfo.txt for publishers and users
        File file = new File(server_file);
        boolean exist = false;

        if (file.exists()) {
            FileReader fr = new FileReader(server_file);
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] infoFields = line.split(" ");
                if (infoFields[0].equals(this._serverName) && infoFields[1].equals(ip) && infoFields[2].equals(Integer.toString(this._port))) {
                    exist = true;
                    break;
                }
            }
            br.close();
            fr.close();
        }

        // Server information written to serverinfo.txt in the format "server name" "ip" "port"
        if (!exist) {
            FileWriter fw = new FileWriter(server_file, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(this._serverName + " " + ip + " " + this._port);
            pw.close();
            pw.close();
        }
        
        //New files created
        File relation = new File(relation_file);
        if (!relation.exists()) {
            relation.createNewFile();
        }
        File subscriberInfo = new File(subscriber_file);
        if (!subscriberInfo.exists()) {
            subscriberInfo.createNewFile();
        }
        
    }

	@Override
	public void run() {
        try {
            serverSocket = new ServerSocket(this._port);

            while (true) {
                Socket socket = serverSocket.accept();
                String ip = socket.getInetAddress().getHostAddress();
                int port = socket.getPort();
                Scanner scanner = new Scanner(socket.getInputStream());
                while (scanner.hasNextLine()) {
                    String command = scanner.nextLine();

                    commandExecute(socket, command);
                }
                scanner.close();
                socket.close();
            }
            

        } catch (IOException e) {
            System.out.println(this._serverName + " is now closed");
        }
    }


    private void commandExecute(Socket clientSocket, String comm) {
        System.out.println("command: "+comm);
        if (comm.startsWith("publisher")) {
            System.out.println("Publisher: "+comm);
            int endIndex = comm.indexOf(" ", "publisher".length() + 1);
            String pubName = comm.substring("publisher".length() + 1, endIndex).trim();
            String message = comm.substring(endIndex + 1).trim();

            List<String> subList = new ArrayList<>();
            File relationFile = new File(relation_file);
            if (relationFile.exists()) {
                try {
                    FileReader fr = new FileReader(relationFile);
                    try (BufferedReader br = new BufferedReader(fr)) {
						String line = null;
						while ((line = br.readLine()) != null) {
						    String[] relfields = line.split(" ");
						    if (relfields[0].equals(pubName)) {
						        subList.add(relfields[1]);
						    }
						}
					}
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            Iterator<String> iterator = subList.iterator();
            while (iterator.hasNext()) {
                String subscriber = iterator.next();
                try {
                    FileReader fr = new FileReader(subscriber_file);
                    BufferedReader br = new BufferedReader(fr);
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        String[] subInfoFields = line.split(" ");
                        if (subInfoFields[0].equals(subscriber)) {
                            Socket socket = null;
                            String subsIP = subInfoFields[1];
                            int subsPort = Integer.valueOf(subInfoFields[2]);
                            try {
                                socket = new Socket(subsIP, subsPort);
                            } catch (Exception ex) {
                                System.err.println("Unable to connect to subscriber " + subscriber + " ip: " + subsIP + " Port:" + subsPort);
                                continue;
                            }

                            PrintStream printStream = new PrintStream(socket.getOutputStream());
                            printStream.println("message" + message);
                            if (socket != null) {
                                socket.close();
                            }
                        }
                    }
                    
                    br.close();
                    fr.close();
                } catch (IOException ex) {

                }

            }

        } else if (comm.startsWith("subscriber")) { 
            int startIndex = "subscriber".length() + 1;
            int endIndex = comm.indexOf(" ", startIndex);
            String subscriber = comm.substring(startIndex, endIndex);
            //Handles requests coming from the subscribers
            if (comm.startsWith("online", endIndex + 1)) {
                //Subscriber is online
                List<SubInfo> subscriberInfoList = new ArrayList<>();
                startIndex = endIndex + "online".length() + 1;
                String[] values = comm.split(" ");
                String subsNewIP = values[3];
                String subsNewPort = values[4];
                System.out.println("Subscriber " + subscriber + " is online...");
                
                
                boolean subexists = false;
                try {
                    File subsInfoFile = new File(subscriber_file);
                    if (subsInfoFile.exists()) {
                        FileReader fr = new FileReader(subsInfoFile);
                        BufferedReader br = new BufferedReader(fr);
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            String[] subsfields = line.split(" ");
                            if (subsfields[0].equals(subscriber)) {
                                subscriberInfoList.add(new SubInfo(subscriber, subsNewIP, subsNewPort));
                                subexists = true;
                            } else {
                                subscriberInfoList.add(new SubInfo(subsfields[0], subsfields[1], subsfields[2]));
                            }
                        }
                        br.close();
                        fr.close();
                    } else {
                        subsInfoFile.createNewFile();
                    }

                    if (!subexists) {
                        subscriberInfoList.add(new SubInfo(subscriber, subsNewIP, subsNewPort));
                    }

                    // Write new information to file subscriberinfo.txt
                    FileWriter fileWriter = new FileWriter(subsInfoFile, false);
                    PrintWriter printWriter = new PrintWriter(fileWriter);
                    Iterator<SubInfo> iterator = subscriberInfoList.iterator();

                    while (iterator.hasNext()) {
                        SubInfo subsInfo = iterator.next();
                        String content = subsInfo._subscriber + " " + subsInfo._ip + " " + subsInfo._port;
                        printWriter.println(content);
                    }

                    printWriter.close();
                    fileWriter.close();
                } catch (IOException ex) {

                }


            } else if (comm.startsWith("subscribe", endIndex + 1)) {
            	
                // Update pubsubrelation.txt with the format "topic" "subscriber"
                int offset = comm.lastIndexOf(" ") + 1;
                String publisher = comm.substring(offset);
                System.out.println("Subscriber " + subscriber + " subscribed to topic " + publisher);
                
                try {
                	FileReader fr = new FileReader(relation_file);
                	BufferedReader br = new BufferedReader(fr);
                	String lineToFind = publisher + " " + subscriber;
                	String readFromFile = null;
                	boolean exists = false;
                	while ((readFromFile = br.readLine()) != null) {
                		if (readFromFile.equals(lineToFind)) {
                			exists = true;
                			break;
                		}
                	}
                	br.close();
                	fr.close();
                	if(!exists) {
                		FileWriter fileWriter = new FileWriter(relation_file, true);
                        PrintWriter printWriter = new PrintWriter(fileWriter);
                        printWriter.println(publisher + " " + subscriber);
                        printWriter.close();
                        fileWriter.close();
                	}
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            } else if(comm.startsWith("unsubscribe", endIndex + 1)) {
            	//Unsubscribe user from a topic 
            	int offset = comm.lastIndexOf(" ") + 1;
                String publisher = comm.substring(offset);
                System.out.println("Subscriber " + subscriber + " unsubscribed to the topic " + publisher);
                File relationFile = new File(relation_file);
                System.out.println(publisher + " " + subscriber);
                if (relationFile.exists()) {
                	try {
                		//Remove from pubsubrelation.txt
    	            	FileReader fr = new FileReader(relation_file);
    	                BufferedReader br = new BufferedReader(fr);
                        File tempFile = new File("temp_file.txt");
                        FileWriter fileWriter = new FileWriter(tempFile, true);
                        PrintWriter printWriter = new PrintWriter(fileWriter);
                        String lineToRemove = publisher + " " + subscriber;
                        System.out.println("Line to Remove: " + lineToRemove);
                        String currentLine;
                        while((currentLine = br.readLine()) != null) {
                            // trim newline when comparing with lineToRemove
                            String trimmedLine = currentLine.trim();
                            if(trimmedLine.equals(lineToRemove)) continue;
                            printWriter.write(currentLine + System.getProperty("line.separator"));
                            printWriter.close();
                            fileWriter.close();
                        }
                        br.close();
                        fr.close();
                        boolean success = tempFile.renameTo(relationFile);
                        System.out.println(success); //Test
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                
             }
        } 
        
    }

    private class SubInfo{
        public String _subscriber;
        public String _ip;
        public String _port;

        public SubInfo(String sub, String ip, String port) {
            this._subscriber = sub;
            this._ip = ip;
            this._port = port;
        }
    }
    
    public static int availablePort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)){
            return serverSocket.getLocalPort();
        }
    }

}
