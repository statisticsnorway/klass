package no.ssb.klass.core.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class SearchWords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
