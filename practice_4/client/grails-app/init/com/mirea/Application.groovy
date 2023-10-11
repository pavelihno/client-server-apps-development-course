package com.mirea

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import groovy.transform.CompileStatic
import grails.plugins.metadata.*
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux
import io.rsocket.RSocket
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import io.rsocket.core.RSocketConnector
import io.rsocket.transport.netty.client.TcpClientTransport


@CompileStatic
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)

        try {
            RSocket rSocket = connectToRSocketServer()

            // fire-and-forget
            sendFireAndForgetRequest(rSocket, "Tesla", 150)

            // request-response
            String response = sendRequestResponse(rSocket, "Apple")
            println "Response from server: ${response}"

            // request-stream
            sendRequestStream(rSocket, "150")

            // channel
            sendChannelRequest(rSocket)

            rSocket.dispose()
            
        } catch (Exception e) {
            println "Failed to connect to the RSocket server: ${e.message}"
            throw e
        }

    }

    private static RSocket connectToRSocketServer() {
        return RSocketConnector.connectWith(TcpClientTransport.create("localhost", 7000)).block()
    }

    private static void sendFireAndForgetRequest(RSocket rSocket, String companyName, long price) {
        String stockData = "${companyName};${price}"
        Payload payload = DefaultPayload.create(stockData)
        rSocket.fireAndForget(payload).block()
    }

    private static String sendRequestResponse(RSocket rSocket, String requestData) {
        Payload requestPayload = DefaultPayload.create(requestData)
        Payload responsePayload = rSocket.requestResponse(requestPayload).block()
        return responsePayload.getDataUtf8()
    }

    private static void sendRequestStream(RSocket rSocket, String price) {
        Payload streamPayload = DefaultPayload.create(price)
        rSocket.requestStream(streamPayload)
            .doOnNext(response -> println "Stream response from server: ${response.getDataUtf8()}")
            .blockLast()
    }

    private static void sendChannelRequest(RSocket rSocket) {
        Flux<Payload> payloads = Flux.just(
            DefaultPayload.create("Data1"),
            DefaultPayload.create("Data2"),
            DefaultPayload.create("Data3")
        );

        rSocket.requestChannel(payloads)
            .doOnNext(response -> println "Channel response from server: ${response.getDataUtf8()}")
            .blockLast();
    }
}
