
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceRef;
import webservice.IOException_Exception;
import webservice.MessagingException_Exception;
import webservice.NewWebService_Service;

@WebServlet(urlPatterns = { "/NewServlet" })
public class NewServlet extends HttpServlet {

	@WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/TestProject/NewWebService.wsdl")
	private NewWebService_Service service;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException {
		response.setContentType("text/html;charset=UTF-8");

		String host = "pop.gmail.com";
		String storeType = "pop3";
		int portId = xxx;
		String user = "xxxxxxxxxxxxxxx";
		String password = "xxxxxxxxxxx";

		try (PrintWriter out = response.getWriter()) {

			out.println("<html>");
			out.println("<head><title>JSON Table</title></head>");
			out.println("<body>");
			out.println("<center><h1>JSON Table</h1>");
			Connection jsonConnResult = null;
			Statement stmt = null;
			Connection xmlConnResult = null;
			Statement stmt2 = null;
			try {

				webservice.NewWebService port = service.getNewWebServicePort();
				try {
					port.checkGmail(host, portId, user, password);

				} catch (IOException_Exception ex) {
					Logger.getLogger(NewServlet.class.getName()).log(Level.SEVERE, null, ex);
				} catch (MessagingException_Exception ex) {
					Logger.getLogger(NewServlet.class.getName()).log(Level.SEVERE, null, ex);
				}

				jsonConnResult = DriverManager.getConnection("jdbc:mysql://localhost/a_json", "root", "xxxxxxxx");

				stmt = jsonConnResult.createStatement();

				String query = "SELECT * from a_json_table_3" + ";";
				ResultSet rs = stmt.executeQuery(query);

				out.println("<table border = '1'>");
				out.println("<tr>");

				out.println("<td align=\"center\"> Message ID </td>");
				out.println("<td align=\"center\"> Name </td>");
				out.println("<td align=\"center\"> Gender </td>");
				out.println("<td align=\"center\">Language #1</td>");
				out.println("<td align=\"center\">Language #2</td>");
				out.println("<td align=\"center\">Language #3</td>");
				out.println("<td align=\"center\">Language #4</td>");
				out.println("</tr>");
				out.println("</table>");

				while (rs.next()) {
					int id_message = rs.getInt("id");
					String name = rs.getString("name");
					String gender = rs.getString("gender");
					String languages1 = rs.getString("languages1");
					String languages2 = rs.getString("languages2");
					String languages3 = rs.getString("languages3");
					String languages4 = rs.getString("languages4");

					out.println("<table border = '1'>");
					out.println("<tr>");
					out.println("<td align=\"center\">" + id_message + "</td>");
					out.println("<td align=\"center\">" + name + "</td>");
					out.println("<td align=\"center\">" + gender + "</td>");
					out.println("<td align=\"center\">" + languages1 + "</td>");
					out.println("<td align=\"center\">" + languages2 + "</td>");
					out.println("<td align=\"center\">" + languages3 + "</td>");
					out.println("<td align=\"center\">" + languages4 + "</td>");
					out.println("</tr>");

					out.println("</table>");

				}

				out.println("<br/>");
				out.println("<br/>");
				out.println("<br/>");
				out.println("<center><h1>XML Table</h1>");

				xmlConnResult = DriverManager.getConnection("jdbc:mysql://localhost/a_xml", "root", "xxxxxxx");

				stmt2 = xmlConnResult.createStatement();

				String query2 = "SELECT * from a_xml_table" + ";";
				ResultSet rs2 = stmt2.executeQuery(query2);
				while (rs2.next()) {
					int id_message = rs2.getInt("id");
					String name = rs2.getString("name");
					String gender = rs2.getString("gender");
					String languages1 = rs2.getString("languages1");
					String languages2 = rs2.getString("languages2");
					String languages3 = rs2.getString("languages3");
					String languages4 = rs2.getString("languages4");

					out.println("<table border = '1'>");
					out.println("<tr>");

					out.println("<td align=\"center\"> Message ID </td>");
					out.println("<td align=\"center\"> Name </td>");
					out.println("<td align=\"center\"> Gender </td>");
					out.println("<td align=\"center\">Language #1</td>");
					out.println("<td align=\"center\">Language #2</td>");
					out.println("<td align=\"center\">Language #3</td>");
					out.println("<td align=\"center\">Language #4</td>");
					out.println("</tr>");

					out.println("<tr>");
					out.println("<td align=\"center\">" + id_message + "</td>");
					out.println("<td align=\"center\">" + name + "</td>");
					out.println("<td align=\"center\">" + gender + "</td>");
					out.println("<td align=\"center\">" + languages1 + "</td>");
					out.println("<td align=\"center\">" + languages2 + "</td>");
					out.println("<td align=\"center\">" + languages3 + "</td>");
					out.println("<td align=\"center\">" + languages4 + "</td>");
					out.println("</tr>");

					out.println("</table>");

				}
			} catch (SQLException e) {

				out.println("An error occured while retrieving data ..." + e.toString());
			} finally {
				try {
					if (stmt != null) {
						stmt.close();
					}
					if (stmt2 != null) {
						stmt.close();
					}
					if (jsonConnResult != null) {
						jsonConnResult.close();
					}
					if (xmlConnResult != null) {
						jsonConnResult.close();
					}
				} catch (SQLException ex) {
				}
			}
			out.close();
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			processRequest(request, response);

		} catch (SQLException ex) {
			Logger.getLogger(NewServlet.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (SQLException ex) {
			Logger.getLogger(NewServlet.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public String getServletInfo() {
		return "Short description";
	}

}
