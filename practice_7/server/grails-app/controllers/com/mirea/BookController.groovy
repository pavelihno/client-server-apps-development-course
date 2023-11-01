package com.mirea

import grails.gorm.transactions.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity

import reactor.core.publisher.BufferOverflowStrategy
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Transactional
@RestController
class BookController {

    static allowedMethods = [
        list: "GET",
        create: "POST",
        get: "GET",
        update: "PUT",
        delete: "DELETE"
    ]

    static responseFormats = ['json']

    Flux<Book> list() {
        def books = []
        
        String author = params.author
        Integer publishedYear = params.publishedYear as Integer

        if (author && publishedYear) {
            books = Book.findAllByPublishedYearAndAuthor(publishedYear, author)
        } 
        else if (author) {
            books = Book.findAllByAuthor(author)
        }
        else if (publishedYear) {
            books = Book.findAllByPublishedYear(publishedYear)
        } 
        else {
            books = Book.findAll()

        }
        return Flux.fromIterable(books).onBackpressureBuffer(3, () -> {}, BufferOverflowStrategy.ON_OVERFLOW_DROP_OLDEST)
    }

    Mono<ResponseEntity<Book>> create(@RequestBody Book book) {   
        return Mono.fromCallable {
            book.save(flush: true)
        }
        .map { newBook -> ResponseEntity.ok(newBook) }
        .onErrorReturn('Указаны некорректные данные')
    }

    Mono<ResponseEntity<Book>> get(@PathVariable("id") Long id) {
        return Mono.justOrEmpty(Book.get(id))
            .map(book -> ResponseEntity.ok(book))
            .defaultIfEmpty(ResponseEntity.notFound().build())
    }

    Mono<ResponseEntity<Book>> update(@PathVariable("id") Long id, @RequestBody Book book) {
        return Mono.justOrEmpty(Book.get(id))
            .map { existingBook ->
                existingBook.properties = book.properties
                existingBook.save(flush: true)
            }
            .onErrorReturn('Указаны некорректные данные')
            .defaultIfEmpty(ResponseEntity.notFound().build())
    }

    Mono<ResponseEntity<Book>> delete(@PathVariable("id") Long id) {
        return Mono.justOrEmpty(Book.get(id))
            .map { book ->
                book.delete(flush: true)
            }
            .onErrorReturn('Указаны некорректные данные')
            .defaultIfEmpty(ResponseEntity.notFound().build())
    }
}