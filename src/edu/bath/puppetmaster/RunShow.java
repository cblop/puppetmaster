package edu.bath.puppetmaster;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import jason.JasonException;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;

public class RunShow {
  private static BsfAgent punchAgent;
  private static BsfAgent judyAgent;
  private static BsfAgent joeyAgent;
  private static BsfAgent policeAgent;
  private static boolean debug = true;
  private static String eventNode;
  private static Queue<String> sceneQueue; 
  private static EventPublisher pubber;

  public static void main(String[] args) throws XMPPException, JasonException, IOException {
    sceneQueue = new LinkedList<String>();

    eventNode = "ev";

    EventSubscriber esub;

    esub = new EventSubscriber("localhost", "log", "loguser", true);

    pubber = new EventPublisher("localhost", "director", "directoruser", eventNode);

    //Connection conn = esub.getConnection();

    punchAgent = new BsfAgent("localhost", "punch", "punchuser", "punch.asl", eventNode);
    judyAgent = new BsfAgent("localhost", "judy", "judyuser", "judy.asl", eventNode);
    joeyAgent = new BsfAgent("localhost", "joey", "joeyuser", "joey.asl", eventNode);
    policeAgent = new BsfAgent("localhost", "police", "policeuser", "police.asl", eventNode);

    // This has to come after for some reason
    // Seems like it's bad for subscribers to create nodes?
    esub.subscribeTo(eventNode);

    //punchAgent.setupLogger();
    //judyAgent.setupLogger();
    //policeAgent.setupLogger();

    /*
    try {
      pubber.publishEvent("police", "move", "stageCentre");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    */

    //BsfAgent punchAgent = new BsfAgent(conn, "judy", "judyuser", "punch-bsf.asl", eventNote);

    new Thread() {
      public void run() {
        try {
          readKeyboard();
        }
        catch(IOException ex){
        }
      }
    }.start();

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
        joeyAgent.run();
      }
    }.start();
    new Thread() {
      public void run() {
        policeAgent.run();
      }
    }.start();

    //startShow();

  }

  public static void readKeyboard() throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
String line = "";

   while (line.equalsIgnoreCase("q") == false) {
       line = in.readLine();
   }

   in.close();

   System.out.println("Exiting...");

   pubber.cleanup();
   punchAgent.cleanup();
   judyAgent.cleanup();
   policeAgent.cleanup();
   System.exit(0);

  }

  public static void publishInput(String type) {
    try {
      pubber.publishEvent("director", "input", type);
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public static void startShow() throws XMPPException {

    System.out.println("Starting the show");
    for (String item : sceneQueue) {
      sceneQueue.remove();
    }

    // What's the memory situation here?
    punchAgent.reset();
    judyAgent.reset();
    joeyAgent.reset();
    policeAgent.reset();

    // don't forget! FIFO!
    //sceneQueue.add("intro");
    sceneQueue.add("judy");
    //sceneQueue.add("police");

    nextScene();
  }

  public static void nextScene() throws XMPPException {
    if (sceneQueue.isEmpty()) {
      // end the show
    }
    else {
      try {
        System.out.println("New Scene");
        pubber.publishEvent("director", "scene", sceneQueue.remove());
      } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }
  

}
