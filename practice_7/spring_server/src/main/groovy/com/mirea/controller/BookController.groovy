package com.mirea

import org.springframework.transaction.annotation.*
import org.springframework.beans.factory.annotation.*
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity

import reactor.core.publisher.BufferOverflowStrategy
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Transactional
@RestController
@RequestMapping('/book')
public class BookController {
    
    private BookRepository bookRepository

    @Autowired
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository
    }

    @GetMapping('/list')
    public Flux<Book> list(@RequestParam(name='author', required=false) String author, @RequestParam(name='publishedYear', required=false) Integer publishedYear) {
        def books

        if (author != null && publishedYear != null) {
            books = bookRepository.findByAuthorAndPublishedYear(author, publishedYear)
        } else if (author != null) {
            books = bookRepository.findByAuthor(author)
        } else if (publishedYear != null) {
            books = bookRepository.findByPublishedYear(publishedYear)
        } else {
            books = bookRepository.findAll()
        }

        return books.onBackpressureBuffer(3)
    }

    @PostMapping('/create')
    public Mono<ResponseEntity<Book>> create(@RequestBody Book book) {
        return bookRepository.save(book)
            .map { newBook -> ResponseEntity.ok(newBook) }
            .onErrorReturn('Указаны некорректные данные')
    }

    @GetMapping('/get/{id}')
    public Mono<ResponseEntity<Book>> get(@PathVariable("id") Long id) {
        return bookRepository.findById(id)
            .map(book -> ResponseEntity.ok(book))
            .onErrorReturn('Указаны некорректные данные')
            .defaultIfEmpty(ResponseEntity.notFound().build())
    }

    @PutMapping('/update/{id}')
    public Mono<ResponseEntity<Book>> update(@PathVariable("id") Long id, @RequestBody Book updatedBook) {
        return bookRepository.findById(id)
            .flatMap(existingBook -> {
                existingBook.setTitle(updatedBook.getTitle())
                existingBook.setAuthor(updatedBook.getAuthor())
                existingBook.setPublishedYear(updatedBook.getPublishedYear())

                return bookRepository.save(existingBook)
                    .map(updated -> ResponseEntity.ok(updated))
            })
            .onErrorReturn('Указаны некорректные данные')
            .defaultIfEmpty(ResponseEntity.notFound().build())
    }

    @DeleteMapping('/delete/{id}')
    Mono<ResponseEntity<Void>> delete(@PathVariable("id") Long id) {
        return bookRepository.findById(id)
            .flatMap(existingBook -> {
                bookRepository.delete(existingBook)
                    .then(Mono.just(ResponseEntity.noContent().<Void>build()))
            })
            .defaultIfEmpty(ResponseEntity.notFound().build())
    }
}