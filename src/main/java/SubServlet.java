/*
 * Servlet for Create Subscriber*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@WebServlet(name = "subservlet")
@SuppressWarnings("serial")
public class SubServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) {

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String subscriber = request.getParameter("name");
		if (!subscriber.isBlank()) {
			FileReader fileReader = new FileReader("subscriberinfo.txt");
            try (BufferedReader br = new BufferedReader(fileReader)) {
				String line = null;
				PrintWriter out = response.getWriter();
				while ((line = br.readLine()) != null) {
				    String[] serverfields = line.split(" ");
				    if (subscriber.equals(serverfields[0])) {
				    	out.print(subscriber + " already exists!");
				    	br.close();
				        fileReader.close();
				        return;
				    }
				}
				
				/* Fetching the contents of the userName field from the form */
				SubHandler ss = new SubHandler(subscriber);
				ss.createSub();
				
				String html = "<html><head>\n"
				 		+ "  <meta http-equiv=\"refresh\" content=\"5\">\n"
				 		+ "</head><body><div id=\"maindiv\"></div></body></html>";
				
				String filename = subscriber+".html";
				File file = new File(filename);
				 if(!file.exists()) {
					 try {
				    	 BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				    	 bw.write(html);
				    	 bw.close();
				     }
				     catch(IOException e){
				    	 e.printStackTrace();
				     } 
				  }
				
				Document doc = Jsoup.parse(file, "UTF-8");
				Element div = doc.getElementById("maindiv");
				div.append("<div id=\""+subscriber+"\"><h2>Messages for Subscriber "+subscriber+"</h2></div>");
				PrintWriter writer = new PrintWriter(file);
				writer.print(doc.outerHtml());
				writer.close();
				ss.startGet();
				
				out.print(subscriber + " has been created!");
			}
		}
		else {
			PrintWriter out = response.getWriter();
			out.println("Please enter a subscriber name");
		}
	}
}