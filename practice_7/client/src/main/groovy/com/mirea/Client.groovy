package com.mirea

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.client.WebClient

import reactor.core.scheduler.Schedulers
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


public class Client {

    private static WebClient client = WebClient.create("http://localhost:8080")

    private static printBook(book) {
        println "${book.id}, ${book.title}, ${book.author}, ${book.publishedYear}"
    }

    private static list() {
        println 'LIST'
        def books = client
            .get()
            .uri("/book/list")
            .retrieve()
            .bodyToFlux(Book.class)
        
        books.subscribe(this::printBook)
    }

    private static create(Book book) {
        println 'CREATE'
        Mono<Book> createdBook = client
            .post()
            .uri("/book/create")
            .body(Mono.just(book), Book.class)
            .retrieve()
            .bodyToMono(Book.class)
        
        createdBook.subscribe(this::printBook)
    }

    private static get(Long id) {
        println 'GET'
        Mono<Book> existingBook = client
            .get()
            .uri("/book/get/{id}", id)
            .retrieve()
            .bodyToMono(Book.class)

        existingBook.subscribe(this::printBook)
    }

    private static update(Long id, Book book) {
        println 'UPDATE'
        Mono<Book> updatedBook = client
            .put()
            .uri("/book/update/{id}", id)
            .body(Mono.just(book), Book.class)
            .retrieve()
            .bodyToMono(Book.class)

        updatedBook.subscribe(this::printBook)
    }

    private static delete(Long id) {
        println 'DELETE'
        Mono<Book> deletedBook = client
                .delete()
                .uri("/book/delete/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)

            deletedBook.subscribe()
    }

    public static void main(String[] args) {
        try {
            // list
            this::list()
            
            // create
            this::create(new Book(title: 'War and Peace', author: 'Leo Tolstoy', publishedYear: 1867))
            
            // get
            this::get(1)
            
            // update
            this::update(5, new Book(title: 'Anna Karenina', author: 'Leo Tolstoy', publishedYear: 1878))

            // delete
            this::delete(7)

            Thread.sleep(10000)

        } catch (Exception e) {
            // println "Failed to connect to the server: ${e.message}"
            throw e
        }
    }
}