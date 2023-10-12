package com.mirea

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.annotation.Nullable
import io.rsocket.Payload
import io.rsocket.RSocket
import io.rsocket.util.DefaultPayload

@Component
class StockRSocket implements RSocket {

    @Autowired
    private StockService stockService

    @MessageMapping("fire-and-forget")
    public Mono<Void> fireAndForget(Payload payload) {
        String stockData = payload.getDataUtf8()
        String[] stockParts = stockData.split(";")

        if (stockParts.length == 2) {
            String companyName = stockParts[0].trim()
            Long price = Long.parseLong(stockParts[1].trim())
            Stock newStock = new Stock(companyName: companyName, price: price)
            
            stockService.saveStock(newStock)
            return Mono.empty()
        } else {
            return Mono.error(new IllegalArgumentException("Invalid stock data format"))
        }
    }

    @MessageMapping("request-response")
    public Mono<Payload> requestResponse(Payload payload) {
        String companyName = payload.getDataUtf8()
        
        // сервис не возвращает ничего
        // def stocks = stockService.findStocksByCompanyName(companyName)
        // if (stocks) {
        //     def stock = stocks.first()
        //     return Mono.just(DefaultPayload.create("Company Name: ${stock.companyName}, Price: ${stock.price}"))
        // } else {
        //     return Mono.error(new IllegalArgumentException("Stock not found"))
        // }
        
        return Mono.just(DefaultPayload.create("Company Name: ${companyName}, Price: ${1}"))
    }

    @MessageMapping("request-stream")
    public Flux<Payload> requestStream(Payload payload) {
        Long price = payload.getDataUtf8().toLong()
        
        // сервис не возвращает ничего
        // def stocks = stockService.findStocksByPrice(price)
        // if (stocks) {
        //     return Flux.fromIterable(stocks).map(stock -> {
        //         String response = "Company Name: ${stock.companyName}, Price: ${stock.price}"
        //         return DefaultPayload.create(response)
        //     })
        // } else {
        //     return Flux.error(new IllegalArgumentException("No stocks found with the specified price"))
        // }
        
        return Flux.fromIterable(['Apple', 'Tesla', 'MTS']).map(stock -> {
            return DefaultPayload.create("Company Name: ${stock}, Price: ${price}")
        })
    }

    @MessageMapping("request-channel")
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        Flux<Payload> payloadFlux = Flux.from(payloads)

        return payloadFlux.flatMap(payload -> {
            return Mono.just(DefaultPayload.create("Processed Data: ${payload.getDataUtf8()}"))
        })
    }
}