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

import edu.bath.InstManager;

public class RunShow {
  private static BsfAgent punchAgent;
  private static BsfAgent judyAgent;
  private static BsfAgent joeyAgent;
  private static BsfAgent babyAgent;
  private static BsfAgent policeAgent;
  private static InstManager man;
  private static boolean debug = true;
  private static String eventNode;
  private static Queue<String> sceneQueue; 
  private static EventPublisher pubber;

  public static void main(String[] args) throws XMPPException, JasonException, IOException, InterruptedException {
    sceneQueue = new LinkedList<String>();

    eventNode = "NODE_PERCEPT";
    String host = "localhost";

    EventSubscriber esub;

    esub = new EventSubscriber(host, "log", "loguser", true);

    pubber = new EventPublisher(host, "director", "directoruser", eventNode);

    //Connection conn = esub.getConnection();

    punchAgent = new BsfAgent(host, "punch", "punchuser", "punch.asl", eventNode);
    judyAgent = new BsfAgent(host, "judy", "judyuser", "judy.asl", eventNode);
    joeyAgent = new BsfAgent(host, "joey", "joeyuser", "joey.asl", eventNode);
    babyAgent = new BsfAgent(host, "baby", "babyuser", "baby.asl", eventNode);
    policeAgent = new BsfAgent(host, "police", "policeuser", "police.asl", eventNode);

    // This has to come after for some reason
    // Seems like it's bad for subscribers to create nodes?
    esub.subscribeTo(eventNode);

    String[] instArgs = {host, "inst", "instuser", "pj_model.ial"};

    man = new InstManager(instArgs);
    man.initialiseBSF();

    new Thread() {
      public void run() {
        try {
          man.run();
        }
        catch(XMPPException e) {

        }
      }
    }.start();


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
        babyAgent.run();
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
   babyAgent.cleanup();
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
    babyAgent.reset();
    policeAgent.reset();

    // don't forget! FIFO!
    sceneQueue.add("intro");
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
        pubber.publishEvent("director", "nextScene", sceneQueue.remove());
        //pubber.publishEvent("director", "scene", sceneQueue.remove());
      } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }
  

}
