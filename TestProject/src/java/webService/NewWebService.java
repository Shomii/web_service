
package webService;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchProviderException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.faces.validator.Validator;
import javax.json.Json;
import javax.json.JsonReader;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

@WebService(serviceName = "NewWebService")
public class NewWebService {

	protected String emailCount;
	protected String emailFrom;
	protected String emailSubject;
	protected String emailContent;
	protected static Connection connection;
	protected String s;

	JsonParser jsonParser = new JsonParser();
	Timer timer = new Timer();

	String messageContent;
	String fileName;
	MimeBodyPart part;

	// @WebMethod
	public void checkGmail(String host, Integer port, String user, String password)
			throws MessagingException, IOException {

		try {

			Session session = Session.getDefaultInstance(new Properties());
			Store store = session.getStore("imaps");

			store.connect(host, user, password);

			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);

			Message messages[] = emailFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
			System.out.println("messages.length---" + messages.length);

			for (int i = 0, n = messages.length; i < n; i++) {
				Message message = messages[i];
				int id_message = message.getMessageNumber();

				if (hasAttachments(message)) {

					Multipart multiPart = (Multipart) message.getContent();
					int numberOfParts = multiPart.getCount();
					for (int partCount = 0; partCount < numberOfParts; partCount++) {
						part = (MimeBodyPart) multiPart.getBodyPart(partCount);
						if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
							fileName = part.getFileName();
						} else {
							messageContent = part.getContent().toString();
						}
					}

					String extension = "";
					try {
						int jot = fileName.lastIndexOf('.');
						if (jot > 0) {
							extension = fileName.substring(jot + 1);
						}
					} catch (Exception e) {
					}

					if ("json".equals(extension)) {
						Statement stm = null;
						int id_messageFromDB = 0;
						try {
							JSONParser parser = new JSONParser();

							Object obj = parser.parse(new InputStreamReader(part.getInputStream()));

							Connection jsonConn = DriverManager.getConnection("jdbc:mysql://localhost/a_json", "root",
									"xxxxxx");

							stm = jsonConn.createStatement();
							String query = "SELECT * from a_json_table_3" + ";";
							ResultSet rs = stm.executeQuery(query);
							while (rs.next()) {
								id_messageFromDB = rs.getInt("id");
							}
							if (id_message != id_messageFromDB) {
								PreparedStatement st = jsonConn
										.prepareStatement("insert into a_json_table_3 values (?,?,?,?,?,?,?)");

								JSONObject jsonObject = (JSONObject) obj;
								System.out.println(jsonObject);
								String name = (String) jsonObject.get("name");
								System.out.println("Name: " + name);
								String gender = (String) jsonObject.get("gender");
								System.out.println("Gender: " + gender);

								String languages1 = "";
								String languages2 = "";
								String languages3 = "";
								String languages4 = "";
								JSONArray languages = (JSONArray) jsonObject.get("languages");

								for (int p = 0; p < languages.size(); p++) {
									languages1 = languages.get(0).toString();
									languages2 = languages.get(1).toString();
									languages3 = languages.get(2).toString();
									languages4 = languages.get(3).toString();
								}

								st.setInt(1, id_message);
								st.setString(2, name);
								st.setString(3, gender);
								st.setString(4, languages1);
								st.setString(5, languages2);
								st.setString(6, languages3);
								st.setString(7, languages4);
								st.execute();
								jsonConn.close();

							}
						} catch (JsonParseException e) {

						}
					}

					if ("xml".equals(extension)) {

						if (!fileName.isEmpty()) {

							Statement stm = null;
							int id_messageFromDB = 0;

							DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
							factory.setValidating(false);
							factory.setNamespaceAware(true);
							DocumentBuilder builder = factory.newDocumentBuilder();
							builder.setErrorHandler(new SimpleErrorHandler());
							Document document = builder.parse(new InputSource(part.getInputStream()));

							Connection xmlConn = DriverManager.getConnection("jdbc:mysql://localhost/a_xml", "root",
									"xxxxxxx");
							stm = xmlConn.createStatement();
							String query = "SELECT * from a_xml_table" + ";";
							ResultSet rs = stm.executeQuery(query);
							while (rs.next()) {
								id_messageFromDB = rs.getInt("id");
							}
							if (id_message != id_messageFromDB) {

								PreparedStatement st = xmlConn
										.prepareStatement("insert into a_xml_table values (?,?,?,?,?,?,?)");

								NodeList nodeList = document.getElementsByTagName("info");
								for (int temp = 0; temp < nodeList.getLength(); temp++) {
									org.w3c.dom.Node node = nodeList.item(temp);
									if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
										Element element = (Element) node;
										String name = element.getElementsByTagName("name").item(0).getTextContent();
										String gender = element.getElementsByTagName("gender").item(0).getTextContent();
										String languages1 = element.getElementsByTagName("languages").item(0)
												.getTextContent();
										String languages2 = element.getElementsByTagName("languages").item(1)
												.getTextContent();
										String languages3 = element.getElementsByTagName("languages").item(2)
												.getTextContent();
										String languages4 = element.getElementsByTagName("languages").item(3)
												.getTextContent();

										st.setInt(1, id_message);
										st.setString(2, name);
										st.setString(3, gender);
										st.setString(4, languages1);
										st.setString(5, languages2);
										st.setString(6, languages3);
										st.setString(7, languages4);
										st.execute();
										xmlConn.close();
									}
								}
							}
						}
					}
				}
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//     @WebMethod(operationName = "hello")
//    public String hello(@WebParam(name = "name") String txt) {
//        return "Hello " + txt + " !";
//    }

	@WebMethod
	public void setTimer() {

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {

					checkGmail("pop.gmail.com", 993, "xxxxxxxx", "xxxxxxx");

				} catch (MessagingException ex) {
					Logger.getLogger(NewWebService.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(NewWebService.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		};

		long delay = 10000;
		long intevalPeriod = 1 * 30000;
		System.out.println("Check mail complete !!!");
		timer.scheduleAtFixedRate(task, delay, intevalPeriod);
	}

	boolean hasAttachments(Message msg) throws MessagingException, IOException {
		if (msg.isMimeType("multipart/mixed")) {
			Multipart mp = (Multipart) msg.getContent();
			if (mp.getCount() > 1)
				return true;
		}
		return false;
	}
}
