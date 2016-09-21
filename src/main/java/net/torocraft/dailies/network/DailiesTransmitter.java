package net.torocraft.dailies.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import net.torocraft.dailies.DailiesException;

import org.apache.commons.io.IOUtils;

import com.google.gson.GsonBuilder;

public class DailiesTransmitter {

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
	private int responseCode;
	private InputStream responseStream;
	
	public DailiesTransmitter(String path, String jsonRequest, String requestMethod) {
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
			openConnection();
			setConnectionProperties();
			writeRequestToConnection();
			getResponseCode();
			getResponseStream();
			getJsonResponseFromStream();
		} catch(IOException e) {
			e.printStackTrace();
			throw DailiesException.NETWORK_ERROR(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private void openConnection() throws IOException {
		conn = (HttpURLConnection) url.openConnection();
	}
	
	private void setConnectionProperties() throws ProtocolException {
		conn.setRequestMethod(requestMethod);
	}
	
	private void writeRequestToConnection() throws IOException {
		if (conn.getRequestMethod().equals("POST")) {
			conn.setDoOutput(true);
			IOUtils.write(jsonRequest, conn.getOutputStream());			
		}
	}
	
	private void getResponseCode() throws IOException {
		responseCode = conn.getResponseCode();
	}
	
	private void getResponseStream() throws IOException {
		if (responseCode >= 200 && responseCode < 300) {
			responseStream = conn.getInputStream();
		} else {
			responseStream = conn.getErrorStream();
		}
	}
	
	private void getJsonResponseFromStream() throws IOException {
		jsonResponse = IOUtils.toString(responseStream);
	}
	
	private void checkResponseForError() throws DailiesException {
		try {
			response = new GsonBuilder().create().fromJson(jsonResponse, DailiesResponse.class);
		} catch (Exception e) {
			// Unable to deserialize the response, but that's probably ok.
		}
		if (response != null && response.error != null && response.error.length() > 0) {
			throw DailiesException.SERVICE_ERROR(response.error);
		}
	}

}
