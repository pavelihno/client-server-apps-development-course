package com.mirea

class Book {
    
    String title
    String author
    Integer publishedYear
    
    static constraints = {
        title(blank: false, maxSize: 255)
        author(blank: false, maxSize: 100)
        publishedYear(min: 0, max: new Date().year)
    }
}