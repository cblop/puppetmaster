package edu.bath.puppetmaster;

import java.net.UnknownHostException;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import edu.bath.sensorframework.JsonReading.Value;
import edu.bath.sensorframework.JsonReading;

public class EventLogger {
	private MongoClient mongoClient;
	private DB db;
	private DBCollection coll;
	private Timestamp time;
	
	public EventLogger() throws UnknownHostException {
		mongoClient = new MongoClient("localhost", 27017);
		db = mongoClient.getDB("eventlog");
		time = getTimestamp();
		coll = getCollection(time.toString());

	}
	
	public Set<String> getCollectionNames() {
		return db.getCollectionNames();
	}
	
	public DBCollection getCollection(String coll) {
		return db.getCollection(coll);
	}
	
	public static Timestamp getTimestamp() {
		java.util.Date date= new java.util.Date();
		return new Timestamp(date.getTime());
	}
	
	public static Timestamp stringToTimestamp(String stamp) {
		try{
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		    Date parsedDate = dateFormat.parse(stamp);
		    Timestamp timestamp = new Timestamp(parsedDate.getTime());
		    return timestamp;
		}
		catch(Exception e){
			// yes
			return null;
		}
		
		
	}
	
	public DBCollection getLastCollection() {
		Set<String> allCollections = getCollectionNames();
		Timestamp recent = stringToTimestamp(allCollections.toArray()[0].toString());

		for (String s : allCollections) {
			if (!s.equals("system.indexes")) {
                Timestamp tstamp = stringToTimestamp(s);
                if (tstamp.after(recent)) {
                        recent = tstamp;
                }
			}
		}
		
		//System.out.println(recent.toString());
		
		return getCollection(recent.toString());
	
	}
	
	public void logJson(String json) throws Exception {
		//JsonReading jr = new JsonReading();
		//jr.fromJSON(json);
		
		BasicDBObject dbo = new BasicDBObject("timestamp", getTimestamp().toString())
			.append("event", json);
		
		coll.insert(dbo);

		// actually unnecessary
		/*
        String agname = jr.findValue("AGENT").m_object.toString();
        String functor = jr.findValue("FUNCTOR").m_object.toString();
        Value terms = jr.findValue("TERMS"); 

        if (terms != null && functor != null && agname != null) {
                @SuppressWarnings("unchecked")
                LinkedList<String> termList = (LinkedList<String>) terms.m_object;
                
                String firstTerm;
                if (termList.size() > 0) {
                        firstTerm = termList.getFirst();
                }
                else {
                	firstTerm = null;
                }
                
                Timestamp stamp = getTimestamp();
                
                BasicDBObject dbo = new BasicDBObject("timestamp", stamp.toString())
                        .append("agent", agname)
                        .append("functor", functor)
                        .append("term", firstTerm);
                
                coll.insert(dbo);
        }
        */
        
	}

}
