package com.mirea

import io.rsocket.core.RSocketConnector
import io.rsocket.transport.netty.client.TcpClientTransport

public class Client {
    public static void main(String[] args) {
        try {
            def connection = RSocketConnector.connectWith(TcpClientTransport.create("localhost", 7000)).block()
            println "Connected to the RSocket server"
        } catch (Exception e) {
            println "Failed to connect to the RSocket server: ${e.message}"
        }
    }
}