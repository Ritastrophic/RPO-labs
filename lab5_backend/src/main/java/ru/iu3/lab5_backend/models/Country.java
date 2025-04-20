package ru.iu3.lab5_backend.models;

import jakarta.persistence.*;

@Entity
@Table(name = "countries")
@Access(AccessType.FIELD)
public class Country {

    public Country() { }
    public Country(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    public long id;

    @Column(name = "name")
    public String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}