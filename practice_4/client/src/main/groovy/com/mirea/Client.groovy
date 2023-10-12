package com.mirea

import reactor.core.publisher.Mono
import reactor.core.publisher.Flux
import org.reactivestreams.Publisher
import io.rsocket.RSocket
import io.rsocket.Payload
import io.rsocket.util.DefaultPayload
import io.rsocket.core.RSocketConnector
import io.rsocket.core.RSocketClient
import io.rsocket.transport.netty.client.TcpClientTransport

public class Client {
    public static void main(String[] args) {
        try {
            RSocket rSocket = connectToRSocketServer()

            // fire-and-forget
            // sendFireAndForgetRequest(rSocket, "Tesla", 150)

            // request-response
            // sendRequestResponse(rSocket, "Apple")

            // request-stream
            // sendRequestStream(rSocket, "150")

            // channel
            sendChannelRequest(rSocket)

            Thread.sleep(10000)

            rSocket.dispose()
            
        } catch (Exception e) {
            // println "Failed to connect to the RSocket server: ${e.message}"
            throw e
        }
    }

    private static RSocket connectToRSocketServer() {
        return RSocketConnector.connectWith(TcpClientTransport.create("localhost", 7000)).block()
    }

    private static void sendFireAndForgetRequest(RSocket rSocket, String companyName, long price) {
        rSocket.fireAndForget(DefaultPayload.create("${companyName};${price}")).block()
    }

    private static void sendRequestResponse(RSocket rSocket, String companyName) {
        rSocket.requestResponse(DefaultPayload.create(companyName))
            .doOnNext(response -> println "Response from server: ${response.getDataUtf8()}")
            .subscribe()
    }

    private static void sendRequestStream(RSocket rSocket, String price) {
        rSocket.requestStream(DefaultPayload.create(price))
            .doOnNext(response -> println "Response from server: ${response.getDataUtf8()}")
            .subscribe()
    }

    private static void sendChannelRequest(RSocket rSocket) {
        Publisher<Payload> payloads = Flux.fromIterable(Arrays.asList(
            DefaultPayload.create("Data1"),
            DefaultPayload.create("Data2"),
            DefaultPayload.create("Data3")
        ))

        rSocket.requestChannel(payloads)
            .doOnNext(response -> println "Response from server: ${response.getDataUtf8()}")
            .subscribe()
    }
}