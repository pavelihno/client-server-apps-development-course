package com.mirea


import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table


@NoArgsConstructor
@AllArgsConstructor
@Data
@Table('books')
public class Book {
    
    @Id
    Long id
    
    @Column('title')
    String title

    @Column('author')
    String author

    @Column('published_year')
    Integer publishedYear

}