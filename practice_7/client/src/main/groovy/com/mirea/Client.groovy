package com.mirea

import org.springframework.web.reactive.function.client.WebClient

public class Client {
    public static void main(String[] args) {
        try {
            
            WebClient client = WebClient.create("http://localhost:8080")

            
        } catch (Exception e) {
            // println "Failed to connect to the server: ${e.message}"
            throw e
        }
    }
}