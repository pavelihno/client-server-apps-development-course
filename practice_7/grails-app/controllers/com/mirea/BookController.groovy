package com.mirea

import grails.gorm.transactions.Transactional

import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@RestController
class BookController {

    static allowedMethods = [
        list: "GET",
        create: "POST",
        read: "GET",
        update: "PUT",
        delete: "DELETE"
    ]

    static responseFormats = ['json']

    @Transactional
    def list() {
        def books = Book.list()
        respond([result: books, count: books.size()])
    }


    @Transactional
    def create(@RequestBody Book book) {
        if (book.validate()) {
            book.save(flush: true)
            respond([result: [book], count: 1])
        } else {
            respond([error: 'Указаны некорректные данные', status: 400])
        }
    }

    @Transactional
    def read(@PathVariable("id") Long id) {
        def book = Book.get(id)
        if (book) {
            respond([result: [book], count: 1])
        } else {
            respond([error: 'Не найдено', status: 404])
        }
    }

    @Transactional
    def update(@PathVariable("id") Long id, @RequestBody Book book) {
        def existingBook = Book.get(id)
        if (existingBook) {
            if (book.validate()) {
                existingBook.properties = book.properties
                existingBook.save(flush: true)
                respond([result: [existingBook], count: 1])
            } else {
                respond([error: 'Указаны некорректные данные', status: 400])
            }
        } else {
            respond([error: 'Не найдено', status: 404])
        }
    }

    @Transactional
    def delete(@PathVariable("id") Long id) {
        def book = Book.get(id)
        if (book) {
            book.delete(flush: true)
            respond([result: [book], count: 1])
        } else {
            respond([error: 'Не найдено', status: 404])
        }
    }
}