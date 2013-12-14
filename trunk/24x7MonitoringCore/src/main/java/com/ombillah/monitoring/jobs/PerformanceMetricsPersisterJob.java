package com.ombillah.monitoring.jobs;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import javax.inject.Inject;

import org.codehaus.jackson.map.ObjectMapper;

import com.ombillah.monitoring.domain.CollectedData;

public class PerformanceMetricsPersisterJob implements Runnable {
	
	@Inject
	private CollectedData performanceMetrics;
	
	private String url = "http://localhost:8080/24x7monitoring/rest/";
	private ObjectMapper  mapper = new ObjectMapper();
	
	public void run() {
		
		try {
			submitCollectedMetricsToServer(performanceMetrics);
			performanceMetrics.clearTracer();
			performanceMetrics.cleaLoggedExceptions();
		}
		catch (Throwable ex) {
			ex.printStackTrace();
		}
		
	}
	
	private void submitCollectedMetricsToServer(CollectedData collectedData) throws Exception {
		String jsonString = mapper.writeValueAsString(collectedData);
		String postUrl = url + "processCollectedData";
		doPost(postUrl, jsonString);
	}
	
	private String doPost(String url, String jsonBody) throws IOException {
		HttpURLConnection con = null; 
		StringBuffer response = new StringBuffer();
		try {
			URL obj = new URL(url);
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.write(jsonBody.getBytes(Charset.forName("UTF-8")));
			wr.flush();
			wr.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} finally {
			if(con != null) con.disconnect();
		}
		
 
		return response.toString();
	 
	}

}
