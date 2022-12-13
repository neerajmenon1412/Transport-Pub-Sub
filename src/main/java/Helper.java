/*
 * Helper class to find available ports while creating a subscriber
 * and find current date and time while publishing a message
 * */

import java.io.IOException;
import java.net.ServerSocket;
import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {

    public static int availablePort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)){
            return serverSocket.getLocalPort();
        }
    }

    public static String currentTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date)); 
        return dateFormat.format(date);
    }
}
