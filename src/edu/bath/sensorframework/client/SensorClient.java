package edu.bath.sensorframework.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.PubSubManager;

//for ReceiveMessage
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.MessageListener;

import edu.bath.sensorframework.Config;

/**
 * This is the class which controls client operations for the sensor framework.
 * Simply instantiate this object, and use!
 * 
 * @author adan
 *
 */
public class SensorClient {
	private PubSubManager mgr = null;
	private Map<String,List<String>> rawPendingData;
	private String myJID;
	private ReadingReceiver handler;
	private Connection connection;
	private String username, password;
	private Map<String, List<ReadingHandler>> handlersList;
	private List<String> subscriptionList = new ArrayList<String>();
	
	private Chat mChat;
	private String mTarget;
	
	/**
	 * Creates a sensor client.
	 * @param serverAddress Address of server to connect to.
	 * @param id Username to authenticate with.
	 * @param password Password to authenticate with.
	 * @throws XMPPException
	 */
	public SensorClient(String serverAddress, String id, String password) throws XMPPException {
		Config.configure();
		connection = new XMPPConnection(serverAddress);
		connection.connect();
		connection.login(id, password);
		sensorClientCommon(id, password);
	}
	
	/**
	 * Creates a sensor client with Message Communication
	 * @param serverAddress Address of server to connect to.
	 * @param id Username to authenticate with.
	 * @param password Password to authenticate with.
	 * @param useMessage This sensor client will be used for message based communication.
	 * @throws XMPPException
	 */
	public SensorClient(String serverAddress, String username, String password, Boolean useMessage) throws XMPPException  {
		Config.configure();

		connection = new XMPPConnection(serverAddress);
		connection.connect();
		connection.login(username, password);
		
		Presence presence = new Presence(Presence.Type.available);
		connection.sendPacket(presence);
	}
	
	/**
	 * Creates a sensorclient from a pre-existing connection.
	 * @param connection
	 */
	public SensorClient(Connection connection, String id, String password) {
		this.connection = connection;
		sensorClientCommon(id, password);
	}
	
	/**
	 * Creates a sensor client.
	 * @param serverAddress Address of server to connect to.
	 * @param id Username to authenticate with.
	 * @param password Password to authenticate with.
	 * @param resource Resource to support multiple connections for same user
	 * @throws XMPPException
	 */
	public SensorClient(String serverAddress, String id, String password, String resource) throws XMPPException {
		Config.configure();
		connection = new XMPPConnection(serverAddress);
		connection.connect();
		connection.login(id, password, resource);
		sensorClientCommon(id, password);
	}
	
	/**
	 * Common parts of constructor.
	 * @param id
	 * @param password
	 */
	private void sensorClientCommon(String id, String password) {
		this.username = id; this.password = password;
		this.mgr = new PubSubManager(connection, "pubsub."+connection.getServiceName());
		this.myJID = StringUtils.parseBareAddress(connection.getUser());
		this.rawPendingData = Collections.synchronizedMap(new HashMap<String, List<String>>());
		this.handlersList = Collections.synchronizedMap(new HashMap<String, List<ReadingHandler>>());
		this.handler = new ReadingReceiver(rawPendingData, handlersList);
	}
	
	/**
	 * Subscribes to a node.
	 * @param nodeName Node to subscribe to.
	 * @param addToList Whether this is a new subscription, or a reconnect.
	 * @throws XMPPException
	 */
	private void subscribe(String nodeName, boolean addToList) throws XMPPException {
		Node node = mgr.getNode(nodeName);
		node.addItemEventListener(handler);
		node.subscribe(myJID);
		if(addToList) {
			this.rawPendingData.put(nodeName, new ArrayList<String>(10));
			subscriptionList.add(nodeName);
		}
	}
	
	/**
	 * Subscribes to a node.
	 * @param nodeName Node to subscribe to.
	 * @throws XMPPException
	 */
	public void subscribe(String nodeName) throws XMPPException {
		subscribe(nodeName, true);
	}
	
	/**
	 * Unsubscribe from a node.
	 * @param nodeName
	 * @throws XMPPException
	 */
	public void unsubscribe(String nodeName) throws XMPPException {
		Node node = mgr.getNode(nodeName);
		node.removeItemEventListener(handler);
		node.unsubscribe(myJID);
		subscriptionList.remove(nodeName);
	}
	
	/**
	 * Send a message to target user.
	 * @param target which is target user id
	 * @param message
	 */
	public void setTargetUser(String target, MessageListener listener) {
		mTarget = target;
		ChatManager chatmanager = connection.getChatManager();
		mChat = chatmanager.createChat(mTarget, listener);
	}
	
	/**
	 * Fetch all pending data for a particular node.
	 * @param nodeID Node to fetch data for.
	 * @return List of incoming RDF data.
	 * @see edu.bath.sensorframework.DataReading#fromRDF(String)
	 */
	public List<String> getPendingData(String nodeID) {
		if(this.rawPendingData.get(nodeID) == null)
			return new ArrayList<String>(0);
		
		List<String> newList = new ArrayList<String>(this.rawPendingData.get(nodeID).size());
		newList.addAll(this.rawPendingData.get(nodeID));
		this.rawPendingData.clear();
		return newList;
	}
	
	/**
	 * Check if data is waiting to be handled for a particular node.
	 * @param nodeID Node to check.
	 * @return true if there is data waiting, false otherwise.
	 */
	public boolean isPendingData(String nodeID) {
		if(this.rawPendingData.get(nodeID) == null)
			return false;
		
		return (this.rawPendingData.get(nodeID).size()==0?false:true);
	}
	
	/**
	 * Checks if this connection is still active, if not, automatically 
	 * try to reconnect and resubscribe.
	 * @return true if a reconnection was attempted, otherwise false.
	 * @throws XMPPException
	 */
	public boolean checkReconnect() throws XMPPException {
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
		
		if(output) {// Resubscribe to everything
			for(String sub : subscriptionList)
				subscribe(sub);
		}
		return output;
	}
	
	/**
	 * Adds a handler for incoming data.
	 * @param nodeID Node to listen on.
	 * @param handler Handler for data from that node.
	 * @see edu.bath.sensorframework.client.ReadingHandler
	 */
	public void addHandler(String nodeID, ReadingHandler handler) {
		List<ReadingHandler> handlers = this.handlersList.get(nodeID);
		if(handlers == null) {
			handlers = Collections.synchronizedList(new ArrayList<ReadingHandler>(3));
			this.handlersList.put(nodeID, handlers);
		}
		
		handlers.add(handler);
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

