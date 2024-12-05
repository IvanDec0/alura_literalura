package com.literalura.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "authors")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private Integer id;

    @Column(name = "name", nullable = false)
    @JsonAlias("name")
    private String name;

    @Column(name = "birth_year")
    @JsonAlias("birth_year")
    private Integer birthYear;

    @Column(name = "death_year")
    @JsonAlias("death_year")
    private Integer deathYear;
}
