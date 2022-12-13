/*
 * Servlet for Subscribe/Unsubscribe operations*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name="subunsubservlet")
@SuppressWarnings("serial")
public class SubUnsubServlet extends HttpServlet {

 public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
	 
 }
 
 public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
	String sub = request.getParameter("sub");
	String topic = request.getParameter("topic");
	String action = request.getParameter("button");
	
	FileReader fr = new FileReader("subscriberinfo.txt");
    try (BufferedReader br = new BufferedReader(fr)) {
		String line = null;
		PrintWriter out = response.getWriter();
		boolean subexists = false;
		while ((line = br.readLine()) != null) {
		    String[] serverfields = line.split(" ");
		    if (sub.equals(serverfields[0])) {
		    	subexists = true;
		    	break;
		    }
		}

		if(!subexists) {
			out.print("subscriber " + sub + " does not exist!\nPlease create subscriber first");
			return;
		}
		
		SubHandler us = new SubHandler(sub);
		us.subscribe(topic, action);
		
		out = response.getWriter();
		out.print("Subscriber " + sub + " " + action + "d to topic " + topic);
	}
    
 }
}