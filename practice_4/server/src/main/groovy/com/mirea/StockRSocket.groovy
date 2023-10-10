package com.mirea

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.annotation.Nullable
import io.rsocket.Payload
import io.rsocket.RSocket
import io.rsocket.util.DefaultPayload

class StockRSocket implements RSocket {

    @Autowired
    StockService stockService

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
        String requestData = payload.getDataUtf8()
        println(requestData)

        Stock stock = stockService.findStockByCompanyName(requestData)
        println(stock)
        if (stock != null) {
            String response = "Company Name: ${stock.companyName}, Price: ${stock.price}"
            return Mono.just(DefaultPayload.create(response))
        } else {
            return Mono.error(new IllegalArgumentException("Stock not found"))
        }
    }

    @MessageMapping("request-stream")
    public Flux<Payload> requestStream(Payload payload) {
        String requestData = payload.getDataUtf8()
        List<Stock> stocks = stockService.findStocksByPrice(requestData)

        if (!stocks.isEmpty()) {
            return Flux.fromIterable(stocks).map(stock -> {
                String response = "Company Name: ${stock.companyName}, Price: ${stock.price}"
                return DefaultPayload.create(response)
            })
        } else {
            return Flux.error(new IllegalArgumentException("No stocks found with the specified price"))
        }
    }

    @MessageMapping("channel")
    public Flux<Payload> channel(Flux<Payload> payloads) {
        return payloads.flatMap(payload -> {
            String inputData = payload.getDataUtf8()
            String response = "Processed Data: $inputData"
            return Mono.just(DefaultPayload.create(response))
        })
    }
}