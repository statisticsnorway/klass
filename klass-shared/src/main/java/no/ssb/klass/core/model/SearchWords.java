package no.ssb.klass.core.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class SearchWords {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(nullable = false)
    Date timeStamp;
    
    @Column(nullable = false)
    boolean hit;
    
    @Column(nullable = false)
    String searchString;
    
    public SearchWords() {
    }
    
    public SearchWords(String searchString, boolean hit) {
        this.timeStamp = new Date();
        this.searchString = searchString;
        this.hit = hit;
    }
}
