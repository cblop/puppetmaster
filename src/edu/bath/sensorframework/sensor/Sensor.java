package edu.bath.sensorframework.sensor;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;

import org.json.simple.JSONObject; // support JSON

import edu.bath.sensorframework.Config;
import edu.bath.sensorframework.DataReading;
import edu.bath.sensorframework.JsonReading;

/**
 * Sensor class, should be extended by any agent wishing to act as a sensor 
 * within the framework. This uses a publish/subscribe design pattern, with 
 * other agents sending a subscription request message to the sensor, which 
 * then publishes sensor data to all subscribers as it becomes available.
 * 
 * All sensors should periodically call one of the receiveSensorMessages 
 * methods in order to pick up messages directed at the sensor framework 
 * rather than the sensor itself.
 * 
 * @author adan
 *
 */
public abstract class Sensor {
	private LeafNode leaf;
	private PubSubManager mgr;
	private String nodeName;
	private Connection connection;
	private String username, password;

	/**
	 * Creates a sensor from an existing connection.
	 * @param serverAddress Address of XMPP server.
	 * @param username Username to authenticate as.
	 * @param password Password to authenticate with.
	 * @param nodeName Node to publish to.
	 * @throws XMPPException
	 */
	public Sensor(String serverAddress, String username, String password, String nodeName) throws XMPPException {
		// Set up XMPP server connection first
		Config.configure();
		connection = new XMPPConnection(serverAddress);
		connection.connect();
		connection.login(username, password);
		createSensorCommon(username, password, nodeName);
	}
	
	/**
	 * Creates a sensor from an existing connection.
	 * @param connection Connection to use.
	 * @param username Username used to reconnect.
	 * @param password Password used to reconnect.
	 * @param nodeName Node to publish to.
	 * @throws XMPPException
	 */
	public Sensor(Connection connection, String username, String password, String nodeName) throws XMPPException  {
		this.connection = connection;
		createSensorCommon(username, password, nodeName);
	}
	
	/**
	 * Creates a sensor from an existing connection.
	 * @param connection Connection to use.
	 * @param username Username used to reconnect.
	 * @param password Password used to reconnect.
	 * @throws XMPPException
	 * @description Creates a sensor for messaging (not publish)
	 */
	public Sensor(String serverAddress, String username, String password) throws XMPPException  {
		Config.configure();

		connection = new XMPPConnection(serverAddress);
		connection.connect();
		connection.login(username, password);
		
		Presence presence = new Presence(Presence.Type.available);
		connection.sendPacket(presence);
	}
	
	/**
	 * Common part of constructor.
	 * @param username
	 * @param password
	 * @param nodeName
	 * @throws XMPPException
	 */
	private void createSensorCommon(String username, String password, String nodeName) throws XMPPException  {
		this.username = username; this.password = password;
		
		if(connection.isConnected())
			System.out.println("Now connected!");
		else
			System.out.println("Not connected!");
		
		mgr = new PubSubManager(connection, "pubsub."+connection.getServiceName());
		
		this.nodeName = nodeName;
		//System.out.println("created node: " + nodeName);
		try {
			leaf = mgr.createNode(nodeName);
			ConfigureForm form = new ConfigureForm(FormType.submit);
			form.setAccessModel(AccessModel.open);
			form.setDeliverPayloads(true);
			form.setNotifyRetract(false);
			form.setPersistentItems(false);
			form.setPublishModel(PublishModel.open);
			leaf.sendConfigurationForm(form);
		} catch(Exception e) {
			System.out.println("Node creation failed, fetching old one.");
			leaf = (LeafNode)mgr.getNode(nodeName);
		}
	}
	
	/**
	 * Publishes an item of data to all current subscribers.
	 * @param data Data to publish.
	 * @see edu.bath.sensorframework.DataReading
	 * @throws UnsupportedEncodingException 
	 * @throws RDFHandlerException 
	 * @throws RepositoryException 
	 */
	protected void publish(DataReading data) throws UnsupportedEncodingException {
		//long nanoToMili=1000000;
		//long preTime = System.nanoTime();
		String rdfdString = data.toRDF();
//System.out.println("time to create rdfdString " + ((System.nanoTime()-preTime)/nanoToMili));
		String msgString = "<RDF>"+StringEscapeUtils.escapeXml(rdfdString)+"</RDF>";
//System.out.println("time to create msgString " + ((System.nanoTime()-preTime)/nanoToMili));
		SimplePayload sp = new SimplePayload("RDF", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", msgString);
//System.out.println("time to create simplePayload " + ((System.nanoTime()-preTime)/nanoToMili));
		PayloadItem<SimplePayload> pi = new PayloadItem<SimplePayload>("pwrsensor"+ System.currentTimeMillis(), sp);
//System.out.println("time to create payloadItem " + ((System.nanoTime()-preTime)/nanoToMili));
		//long postTime = System.nanoTime();
		//System.out.println("full time to create msg " + ((System.nanoTime()-preTime)/nanoToMili));
		leaf.publish(pi);
		//System.out.println("time to publish msg " + ((System.nanoTime()-postTime)/nanoToMili));
	}
	
	/**
	 * Publishes an item of data to all current subscribers.
	 * @param data Data to publish in the form of JSON.
	 * @see edu.bath.sensorframework.DataReading
	 * @throws UnsupportedEncodingException 
	 * @throws RDFHandlerException 
	 * @throws RepositoryException 
	 */
	protected void publish(JsonReading jr) throws UnsupportedEncodingException {
		leaf.publish(new PayloadItem<SimplePayload>("JsonItem" + System.currentTimeMillis(), 
				new SimplePayload("JSON", "http://www.json.org/temp-ns#", "<JSON>" + jr.getJsonObject().toString() + "</JSON>")));
	}
	
	/**
	 * Cleans up upon sensor exit.
	 * @throws XMPPException
	 */
	public void cleanup() {
		try {
			mgr.deleteNode(nodeName);
      //leaf.deleteAllItems();
		} catch (XMPPException e) {
			System.out.println("Cleanup failed - Failure in deleteNode(" + nodeName + ")");
		}
	}

	/**
	 * Checks if this connection is still active, if not, automatically 
	 * try to reconnect and resubscribe.
	 * @return true if a reconnection was attempted, otherwise false.
	 * @throws XMPPException
	 */
	protected boolean checkReconnect() throws XMPPException {
		boolean output = false;
		if(!connection.isConnected()) {
			System.out.println("Not connected!");
			connection.connect();
			output = true;
		}
		if(!connection.isAuthenticated()) {
			System.out.println("Not authenticated!");
			connection.login(username, password);
			output = true;
		}
		return output;
	}
	
	/**
	 * Fetches the underlying connection (should you wish to use it for 
	 * other XMPP operations).
	 * @return Connection to XMPP server.
	 */
	public Connection getConnection() {
		return this.connection;
	}
}
