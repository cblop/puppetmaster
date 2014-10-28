/*
 * InstManager 
 * - Main instance of the institution manager. Once receive the percepts from agents,
 * 	then convert them into exogenous events that are acceptable to institutions. Finally,
 * 	these events are delivered to the instance of institutions. Also, it is able to receive 
 *  the norms from institutions and send them to agents for the deliberation inside agents.
 * 
 * 		@author		JeeHang
 * 		@date		29 Mar 2012
 * 
 * (+) Adding multiple institution (Aug 2013, JeeHang Lee) 
 */

package edu.bath;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.io.UnsupportedEncodingException;

import org.jivesoftware.smack.XMPPException;

import edu.bath.institution.*;
import edu.bath.sensorframework.JsonReading;
import edu.bath.sensorframework.JsonReading.Value;
import edu.bath.sensorframework.client.ReadingHandler;
import edu.bath.sensorframework.client.SensorClient;
import edu.bath.sensors.NormSensor;

/*
 * class InstManager
 * 	- Institution Manager
 * 	- The abstraction layer between institutional models and BDI agents
 * 	- Subscribe event from the external environment, publish corresponding norms to BDI agents 
 */
public class InstManager {
	
	private static final String PERCEPT = "NODE_PERCEPT";
	private static final String NORM = "NODE_NORM";

	private NormSensor pubNorm; 		// sensor publishing current states		
	private SensorClient subPercept;	// sensor client perceiving events from environments	
	private String server, username, password;	// login information
	
	private InstFactory factory;		// container of multiple institution
	private Queue<String> m_req;		// 
	
	/*
	 * Percept node reading handler 
	 */
	public class PerceptReadingHandler implements ReadingHandler {
		@Override
		public void handleIncomingReading(String node, String rdf) {
			//System.out.println("got a message..");
			try {
				if ((rdf != null) && (rdf.isEmpty() != true)) {
					JsonReading jr = new JsonReading();
					jr.fromJSON(rdf);
					Value agname = jr.findValue("AGENT");
					Value functor = jr.findValue("FUNCTOR");
					Value val = jr.findValue("VALUE");
					if (val != null & functor != null) {
            String outputString = functor.m_object.toString() + "(" + agname.m_object.toString() + ", " + val.m_object.toString() + ")";
						//System.out.println("processed it ok to " + outputString);
						m_req.add(outputString);
          }
				};
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}

	
	// Constructor
	public InstManager(String[] args) throws XMPPException, InterruptedException {
		m_req = new LinkedList<String>();
		initConfig(args);
		initFactory(args);
	}
	
	private void initConfig(String[] args) {
		server = args[0];
		username = args[1];
		password = args[2];
	}
	
	private void initFactory(String[] args) {
		factory = new InstFactory(args);
	}
	
	public void initialiseBSF() {
		try {
			pubNorm = new NormSensor(server, username, password, PERCEPT); sleep(5000);	// ensure the enough creation time
			//pubNorm = new NormSensor(server, username, password, NORM); sleep(5000);	// ensure the enough creation time
			subPercept = new SensorClient(pubNorm.getConnection(), username, password);
			subPercept.addHandler(PERCEPT, new PerceptReadingHandler()); 
			subPercept.subscribe(PERCEPT);
		} catch (XMPPException e) {
			System.out.println("NormSensor creation failed");
		}
	}
	
	public void invokeRequest(String evt) {
		if (factory != null) {
			//System.out.println("started invokeRequest with " + evt);
			factory.updateStates(evt);
			//System.out.println("Updated states");
			List<String> norms = factory.getCurrentStates();
			if ((norms != null) && (norms.isEmpty() != true)) {
				//System.out.println("Norms start with: " + norms.get(0));
				pubNorm.releaseNorm(norms);
			}
		}
	}
	
	public void run() throws XMPPException	{
		//VB ORIG CODE: 
		/*while (true) {
			if ((m_req != null) && (m_req.isEmpty() != true)) {
				invokeRequest(m_req.poll());
			}
		}*/
		String req;
		while (true)
		{
			if ((m_req != null) && (m_req.isEmpty() != true)) {
				//System.out.println("called invokeRequest");
				invokeRequest(m_req.poll());
			}
			//This can be useful to comment back on if trying to debug message receipt..
			else if (m_req != null)
			{
				//System.out.println("mreq is null");
				if (m_req.isEmpty() != true)
				{
					System.out.println("and empty");
				}
			}
			else
			{
				//System.out.println("mreq is empty");
			}
			//VB added a small sleep, I seem to have messages lost/not processed if this isn't present.. strangely
			sleep(100);
		}

	}
	
	public void sleep(long mili) {
        try {
        	Thread.sleep(mili);
        } catch (InterruptedException e) {
        	// no-op
        }
    }
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws XMPPException, InterruptedException {
		if (args.length < 4) {
			System.out.println("Usage : instManager.jar server username password ial_filename1 ial_filename2 ...");
			return;
		}
		
		InstManager man = new InstManager(args);
		man.initialiseBSF();
		man.run();
	}
}
