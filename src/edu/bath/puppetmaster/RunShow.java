package edu.bath.puppetmaster;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import jason.JasonException;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;

public class RunShow {
	private static BsfAgent punchAgent;
	private static BsfAgent judyAgent;
	private static BsfAgent policeAgent;
	private static boolean debug = true;
	private static String eventNode;
  private static Queue<String> sceneQueue; 
  private static EventPublisher pubber;

	public static void main(String[] args) throws XMPPException, JasonException {
		eventNode = "events";
		
    //EventSubscriber esub;

		//esub = new EventSubscriber("localhost", "anim", "animuser", launcher, true);

		pubber = new EventPublisher("localhost", "director", "directoruser", eventNode);

		//Connection conn = esub.getConnection();
		
		punchAgent = new BsfAgent("localhost", "punch", "punchuser", "punch-bsf.asl", eventNode);
		judyAgent = new BsfAgent("localhost", "judy", "judyuser", "judy-bsf.asl", eventNode);
		policeAgent = new BsfAgent("localhost", "police", "policeuser", "police.asl", eventNode);
		
		// This has to come after for some reason
		// Seems like it's bad for subscribers to create nodes?
		//esub.subscribeTo(eventNode);

		//BsfAgent punchAgent = new BsfAgent(conn, "judy", "judyuser", "punch-bsf.asl", eventNote);
		
		new Thread() {
			public void run() {
                punchAgent.run();
			}
		}.start();
		new Thread() {
			public void run() {
                judyAgent.run();
			}
		}.start();
		new Thread() {
			public void run() {
                policeAgent.run();
			}
		}.start();
		
		sceneQueue = new LinkedList<String>();
		
		// don't forget! FIFO!
		sceneQueue.add("police");
		sceneQueue.add("judy");
		
		nextScene();
		
	}
	
	public static void publishInput(String type) {
        try {
                pubber.publishEvent("director", "input", type);
        } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
		
	}
	
	public static void nextScene() throws XMPPException {
		if (sceneQueue.isEmpty()) {
			// end the show
		}
		else {
                try {
                        pubber.publishEvent("director", "scene", sceneQueue.remove());
                } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
		}
		
	}

}
