package edu.bath.puppetmaster;

import jason.asSyntax.Term;

import java.sql.Timestamp;
import java.util.List;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import edu.bath.sensorframework.JsonReading;

public class ReplayShow {
	private static EventPublisher publisher;
	
	public static void main(String[] args) throws Exception {
		ArrayList<HashMap<String, String>> events = new ArrayList<HashMap<String, String>>();
		EventLogger logger;
        logger = new EventLogger();
		DBCollection coll = logger.getLastCollection();

		// wait because the anim takes a while
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String eventNode = "new";
		
    EventSubscriber esub;
		//esub = new EventSubscriber("localhost", "punch", "punchuser", launcher);
		//esub = new EventSubscriber("localhost", "judy", "judyuser", launcher);
		esub = new EventSubscriber("localhost", "anim", "animuser", false);
		Connection conn = esub.getConnection();
		publisher = new EventPublisher("localhost", "logger", "loggeruser", eventNode);
		esub.subscribeTo(eventNode);
		
		replayEvents(coll);
		
		
	}
	
	
public static void replayEvents(DBCollection coll) throws Exception {
		List<DBObject> collArray = coll.find().toArray();
    long start = EventLogger.stringToTimestamp(collArray.get(0).get("timestamp").toString()).getTime();
		
		final String[] jsons = new String[collArray.size()];
		// stupid, why do this twice?
		int i = 0;
		for (DBObject dbo : collArray) {
			long temp = EventLogger.stringToTimestamp(dbo.get("timestamp").toString()).getTime();
			if (temp < start) {
				start = temp;
			}
			jsons[i] = dbo.get("event").toString();
			i ++;
			
		}

    i = 0;
		
		for (DBObject dbo : collArray) {
			Timestamp stamp = EventLogger.stringToTimestamp(dbo.get("timestamp").toString());
        final String json = dbo.get("event").toString();
        JsonReading js = new JsonReading();
        js.fromJSON(json);
        final String agname = js.findValue("AGENT").m_object.toString();
        final String functor = js.findValue("FUNCTOR").m_object.toString();
        String tvalue = "";
        if (js.findValue("VALUE") != null) {
        	tvalue = js.findValue("VALUE").m_object.toString();
        }
        else {
        	System.out.println("VALUE is null");
        	System.out.println(jsons[0]);
        }
        final String value = tvalue;

        // What to do?!

        /*
      Task eventTask = new Task(){
        @Override
        public void run() {
          //System.out.println("event triggered!");
          try {
            //System.out.println(j);
            //System.out.println(jsons[j]);
            //publisher.publishJson(jsons[j]);
        	publisher.publishEvent(agname, functor, value);
            //publisher.publishJson(json);
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      };

      i++;

			long future = stamp.getTime() - start;
			//System.out.println("Schedule: " + future / 1000f);
			tim.scheduleTask(eventTask, future / 1000f);
      */
		
		}

	}

}
