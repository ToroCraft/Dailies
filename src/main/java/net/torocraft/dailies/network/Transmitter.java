package net.torocraft.dailies.network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.torocraft.dailies.DailiesException;

import org.apache.commons.io.IOUtils;

import com.google.gson.GsonBuilder;

public class Transmitter {

	private static final String SERVICE_URL = "http://www.minecraftdailies.com/";
	public static final String PATH_QUESTS = "/quests";
	public static final String PATH_ACCEPT = "/accept";
	public static final String PATH_ABANDON = "/abandon";
	public static final String PATH_COMPLETE = "/complete";
	
	private final String path;
	private final String jsonRequest;
	private final String requestMethod;
	
	private String jsonResponse;
	private HttpURLConnection conn;
	private URL url;
	private DailiesResponse response;
	
	public Transmitter(String path, String jsonRequest, String requestMethod) {
		this.path = path;
		this.jsonRequest = jsonRequest;
		this.requestMethod = requestMethod;
	}

	public String sendRequest() throws DailiesException {
		buildUrl();
		transmit();
		checkResponseForError();
		return jsonResponse;
	}
	
	private void buildUrl() throws DailiesException {
		try {
			url = new URL(SERVICE_URL + path);
		} catch (MalformedURLException e) {
			throw DailiesException.SYSTEM_ERROR(e);
		}
	}

	private void transmit() throws DailiesException {
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(requestMethod);
			conn.setDoOutput(true);
			IOUtils.write(jsonRequest, conn.getOutputStream());
			jsonResponse = IOUtils.toString(conn.getInputStream());
		} catch(IOException e) {
			e.printStackTrace();
			throw DailiesException.NETWORK_ERROR(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	
	private void checkResponseForError() throws DailiesException {
		response = new GsonBuilder().create().fromJson(jsonResponse, DailiesResponse.class);
		if (response.error != null && response.error.length() > 0) {
			throw DailiesException.SERVICE_ERROR(response.error);
		}
	}

}
