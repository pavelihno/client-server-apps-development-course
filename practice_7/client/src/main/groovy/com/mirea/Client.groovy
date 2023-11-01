package com.mirea

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.client.WebClient

import reactor.core.scheduler.Schedulers
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


public class Client {
    public static void main(String[] args) {
        try {
            
            WebClient client = WebClient.create("http://localhost:8080")

            // list
            def books = client
                .get()
                .uri("/book/list")
                .retrieve()
                .bodyToFlux(Book.class)
            
            books.subscribe(book -> println "${book.id}, ${book.title}, ${book.author}, ${book.publishedYear}")
            
            // create
            Book newBook = new Book(title: 'War and Peace', author: 'Leo Tolstoy', publishedYear: 1867)

            Mono<Book> createdBook = client
                .post()
                .uri("/book/create")
                .body(Mono.just(newBook), Book.class)
                .retrieve()
                .bodyToMono(ResponseEntity.class)
            
            // createdBook.subscribe(book -> println "${book.id}, ${book.title}, ${book.author}, ${book.publishedYear}")

            // get
            Mono<Book> existingBook = client
                .get()
                .uri("/book/get/{id}", 1)
                .retrieve()
                .bodyToMono(Book.class)

            existingBook.subscribe(book -> println "${book.id}, ${book.title}, ${book.author}, ${book.publishedYear}")
            
            // update
            Book updateBook = new Book(title: 'Anna Karenina', author: 'Leo Tolstoy', publishedYear: 1878)

            Mono<Book> updatedBook = client
                .put()
                .uri("/book/update/{id}", 1)
                .body(Mono.just(updateBook), Book.class)
                .retrieve()
                .bodyToMono(ResponseEntity.class)

            // updatedBook.subscribe(book -> println "${book.id}, ${book.title}, ${book.author}, ${book.publishedYear}")

            // delete
            Mono<Book> deletedBook = client
                .delete()
                .uri("/book/delete/{id}", 3)
                .retrieve()
                .bodyToMono(ResponseEntity.class)

            // deletedBook.subscribe(book -> println "${book.id}, ${book.title}, ${book.author}, ${book.publishedYear}")

            Thread.sleep(10000)

        } catch (Exception e) {
            // println "Failed to connect to the server: ${e.message}"
            throw e
        }
    }
}