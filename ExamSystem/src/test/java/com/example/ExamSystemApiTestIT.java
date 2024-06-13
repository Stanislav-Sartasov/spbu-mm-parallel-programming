package com.example;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ExamSystemApiTestIT {

    private static final String BASE_URL = "http://localhost:8080/api";

    @Test
    public void testApi() throws IOException, InterruptedException {
        waitForApi();

        addCredit(1, 1);
        assertEquals(1, getCount());
        assertTrue(containsCredit(1, 1));
        removeCredit(1, 1);
        assertEquals(0, getCount());
        assertFalse(containsCredit(1, 1));
    }

    private void waitForApi() throws InterruptedException {
        int attempts = 30;
        while (attempts > 0) {
            try {
                System.out.println("Waiting for API...");
                if ("UP".equals(sendRequest(BASE_URL + "/health", "GET"))) {
                    System.out.println("API is UP");
                    return;
                }
            } catch (IOException e) {
                System.out.println("API not available yet, retrying...");
                Thread.sleep(1000);
                attempts--;
            }
        }
        throw new RuntimeException("API did not become available in time");
    }

    private void addCredit(long studentId, long courseId) throws IOException {
        sendRequest(BASE_URL + "/add?studentId=" + studentId + "&courseId=" + courseId, "POST");
    }

    private void removeCredit(long studentId, long courseId) throws IOException {
        sendRequest(BASE_URL + "/remove?studentId=" + studentId + "&courseId=" + courseId, "DELETE");
    }

    private boolean containsCredit(long studentId, long courseId) throws IOException {
        return Boolean.parseBoolean(sendRequest(BASE_URL + "/contains?studentId=" + studentId + "&courseId=" + courseId, "GET"));
    }

    private int getCount() throws IOException {
        return Integer.parseInt(sendRequest(BASE_URL + "/count", "GET"));
    }

    private String sendRequest(String urlString, String method) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setConnectTimeout(1000);
        conn.setReadTimeout(1000);
        conn.connect();

        System.out.println("Sending " + method + " request to URL : " + url);

        if (method.equals("POST") || method.equals("DELETE")) {
            if (conn.getResponseCode() != 200) {
                throw new IOException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            System.out.println("Response code from " + urlString + ": " + conn.getResponseCode());
            return "";
        }

        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            if (scanner.hasNext()) {
                String response = scanner.useDelimiter("\\A").next();
                System.out.println("Response from " + urlString + ": " + response);
                return response;
            } else {
                throw new IOException("No response received from " + urlString);
            }
        }
    }
}

