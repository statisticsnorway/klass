package no.ssb.klass.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

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
