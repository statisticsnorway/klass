package no.ssb.klass.core.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ClassificationAccessCounter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    Date timeStamp;

    @OneToOne
    ClassificationSeries classificationSeries;

    public ClassificationAccessCounter() {
    }

    public ClassificationAccessCounter(ClassificationSeries classificationSeries) {
        this.classificationSeries = classificationSeries;
        this.timeStamp = new Date();
    }

}
