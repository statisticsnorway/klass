package no.ssb.klass.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
