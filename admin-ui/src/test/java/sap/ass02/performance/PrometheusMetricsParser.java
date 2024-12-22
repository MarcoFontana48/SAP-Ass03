package sap.ass02.performance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PrometheusMetricsParser {
    private static final Logger LOGGER = LogManager.getLogger(PrometheusMetricsParser.class);
    
    public double getAverageLatencyUserService() throws MalformedURLException {
        return this.getAverageLatencyFrom(new URL("http://localhost:8081/metrics"));
    }
    
    public double getAverageLatencyEBikeService() throws MalformedURLException {
        return this.getAverageLatencyFrom(new URL("http://localhost:8082/metrics"));
    }
    
    private double getAverageLatencyFrom(URL serviceURL) throws MalformedURLException {
        String metrics = this.getMetricsFromPrometheus(serviceURL);
        double totalLatency = this.getTotalLatency(new StringBuilder(metrics));
        double totalRequestsNumber = this.getTotalRequestsNumber(new StringBuilder(metrics));
        if (totalLatency == -1 || totalRequestsNumber == -1) {
            LOGGER.error("Error while parsing metrics from Prometheus");
            return -1;
        }
        return totalLatency / totalRequestsNumber;
    }
    
    private String getMetricsFromPrometheus(URL serviceURL) {
        try {
            HttpURLConnection connection = (HttpURLConnection) serviceURL.openConnection();
            connection.setRequestMethod("GET");
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                connection.disconnect();
                
                return content.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private double getTotalLatency(StringBuilder content) {
        String[] lines = content.toString().split("# ");
        for (String line : lines) {
            if (line.contains("requests_latency_seconds_sum")) {
                Pattern pattern = Pattern.compile("requests_latency_seconds_sum\\s+(\\S+)");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return Double.parseDouble(matcher.group(1));
                }
            }
        }
        return -1;
    }
    
    private double getTotalRequestsNumber(StringBuilder content) {
        String[] lines = content.toString().split("# ");
        for (String line : lines) {
            if (line.contains("requests_latency_seconds_sum")) {
                Pattern pattern = Pattern.compile("(\\d+\\.\\d+)requests_latency_seconds_sum");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return Double.parseDouble(matcher.group(1));
                }
            }
        }
        return -1;
    }
}
