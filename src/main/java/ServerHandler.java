/*
 * Servlet for broker
 * */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name="serverhandler")
@SuppressWarnings("serial")
public class ServerHandler extends HttpServlet {
	
	Map<String, Server> serverMap = new HashMap<>();

 public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
	 if(request.getParameter("name") == null) {
    	 
    	  File file = new File("serverinfo.txt");
    	  if (file.exists()){
    		  FileReader fr = new FileReader("serverinfo.txt");
    		  try (BufferedReader br = new BufferedReader(fr)) {
				String line = null;
				  PrintWriter pw = response.getWriter();
				  pw.println("<html><head><title>Read Server File</title></head><body>");
				  while ((line = br.readLine()) != null) {
					  pw.println("<h4>"+line+"<br>");
					  }
				  pw.println("</body></html>");
				  pw.close();
			}
    	  }
	 }
 }
	
	
 public void doPost(HttpServletRequest request, HttpServletResponse response)
 {
     try { 
    	 
         String broker = request.getParameter("name");
         String action = request.getParameter("action");
         PrintWriter out = response.getWriter();
         if(!broker.isEmpty()) {
             File serverFile = new File("serverinfo.txt");
        	 if(serverFile.exists() && action != null && action.equals("submit")) {
        		 FileReader fr = new FileReader("serverinfo.txt");
                 try (BufferedReader br = new BufferedReader(fr)) {
					String line = null;
					 while ((line = br.readLine()) != null) {
					     String[] serverfields = line.split(" ");
					     if (broker.equals(serverfields[0])) {
					     	br.close();
					     	fr.close();
					     	out.print(broker + " already exists!\nPlease enter a new broker name");
					     	return;
					     }
					 }
				} 
        	 }
             
             if(action != null && action.equals("delete")) {
            	 Server s = serverMap.get(broker);
            	 if(s != null) {
            		 s.serverSocket.close();
            		 System.out.println("Server port is closed");
            	 }
            	 out.print(broker + " has been deleted!");
            	 return;
             }
             if(broker != null) {
            	 //Fetching the contents of the userName field from the form
                 Server ms = new Server(broker);
                 serverMap.put(broker, ms);
                 ms.start(); 
            	 out.print(broker + " has been created!");
                 
             } 
         } else {
        	 out.print("Broker name cannot be blank");
         }
        	  
     }
     
     catch (Exception e) {
         System.out.println(e);
     }
     
	     
 }
}
