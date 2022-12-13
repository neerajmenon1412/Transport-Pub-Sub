/**
 * Servlet for Publisher
 * */

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name="pubservlet")
@SuppressWarnings("serial")
public class PubServlet extends HttpServlet {

 public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
	 
 }
 
 public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
	String topic = request.getParameter("name");
	String content = request.getParameter("content");
	PrintWriter out = response.getWriter();
	if(!topic.isBlank() && !content.isBlank()) {
		PubHandler ps = new PubHandler(topic);
	    ps.start(content);
	    
	    out.print("The following content has been posted under: " + topic + "\n\n");
	    out.print(content);
	}
	else if(topic.isBlank()) {
		out.print("Topic cannot be blank\n");
	}
	else if(content.isBlank()) {
		out.print("Content cannot be blank");
	}
 }
}