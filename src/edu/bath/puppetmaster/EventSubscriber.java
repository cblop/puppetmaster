package edu.bath.puppetmaster;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.jivesoftware.smack.XMPPException;

//import edu.bath.sensorframework.DataReading;
//import edu.bath.sensorframework.DataReading.Value;
import edu.bath.sensorframework.JsonReading;
import edu.bath.sensorframework.JsonReading.Value;
import edu.bath.sensorframework.client.ReadingHandler;
import edu.bath.sensorframework.client.SensorClient;

// Why not just use SensorClient? Because I might have to add custom stuff
public class EventSubscriber extends SensorClient {
	private boolean debug;
	private EventLogger logger;
	public EventSubscriber(String server, String jid, String password, boolean dbg) throws XMPPException {
		super(server, jid, password);
		debug = dbg;
		
		if (debug) {
			startLogger();
		}
	}
	
	private void startLogger() {
		try {
			logger = new EventLogger();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void subscribeTo(String nodeName) throws XMPPException 
	{
		// TODO Auto-generated method stub
		//SensorClient sc = new SensorClient("172.16.125.2", "user3", "bathstudent");
		System.out.println("Setting up subscriber...");
		
		addHandler(nodeName, new ReadingHandler() {
			public void handleIncomingReading (String node, String rdf) {
				//System.out.println("Handling reading...");

        //System.out.println(rdf);

				try {
					JsonReading jr = new JsonReading();
					jr.fromJSON(rdf);
					
					if (debug) {
						logger.logJson(rdf);
					}
					
					//System.out.println(rdf);
					
					Value agname = jr.findValue("AGENT");
					Value functor = jr.findValue("FUNCTOR"); 
					Value value = jr.findValue("VALUE"); 

          System.out.println(agname.m_object.toString());
          System.out.println(functor.m_object.toString());
          System.out.println(value.m_object.toString());
					
					
					if (functor != null && value != null && agname != null) {

                            if (functor.m_object.toString().equals("nextScene")) {
                            	RunShow.nextScene();
                            }

                            else if (functor.m_object.toString().equals("start")) {
                              System.out.println("Starting again");
                              RunShow.startShow();
                            }

					}
                    
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		subscribe(nodeName);
		System.out.println("Subscribed to " + nodeName);
		
		
	}

}
