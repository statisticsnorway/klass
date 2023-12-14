package no.ssb.klass.core.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class ClassificationAccessCounter {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(nullable = false)
    Date timeStamp;
    
    @OneToOne
    ClassificationSeries classificationSeries;
    
    public ClassificationAccessCounter() {}
    
    public ClassificationAccessCounter(ClassificationSeries classificationSeries) {
        this.classificationSeries = classificationSeries;
        this.timeStamp = new Date();
    }
    
}
