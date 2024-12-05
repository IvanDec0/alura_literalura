package com.literalura.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    @JsonAlias("title")
    private String title;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "books_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @JsonAlias("authors")
    private List<Author> authors;

    @Transient
    @JsonAlias("languages")
    private List<String> languages;

    @JsonAlias("language")
    @Column(name = "language")
    private String language;

    @Column(name = "download_count")
    @JsonAlias("download_count")
    private Integer downloadCount;

}
