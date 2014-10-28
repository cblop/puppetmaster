package edu.bath.sensors;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;

import edu.bath.sensorframework.sensor.Sensor;
import edu.bath.sensorframework.JsonReading;

public class NormSensor extends Sensor {
	public NormSensor(String server, String user, String pwd, String node) throws XMPPException {
		super(server, user, pwd, node);
	}
	
	public NormSensor(Connection conn, String user, String pwd, String node) throws XMPPException {
		super(conn, user, pwd, node);
	}
	
	public void releaseNorm(String norms) {
		JsonReading jr = new JsonReading();
		jr.addValue("STATE", norms);
		try {
			publish(jr);
			//System.out.println("Published norm: " + jr);
		} catch (UnsupportedEncodingException ue) {
			System.out.println("publish failed!");
		}
	}
	
	public void releaseNorm(List<String> norms) {
		int index = 0;
		JsonReading jr = new JsonReading();
		jr.addValue("CONTENT", "NORM");
		jr.addValue("COUNT", norms.size());
		for (String norm : norms) {
			jr.addValue("NORM" + (index++), norm);
			//System.out.println("NormSensor added: " + norm);
		}

		try {
			publish(jr);
			//System.out.println("Published norm list: " + jr);
		} catch (UnsupportedEncodingException ue) {
			System.out.println("publish failed!");
		}
	}
}
