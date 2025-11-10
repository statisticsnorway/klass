package no.ssb.klass.core.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class ClassificationAccessCounter {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  Date timeStamp;

  @ManyToOne ClassificationSeries classificationSeries;

  public ClassificationAccessCounter() {}

  public ClassificationAccessCounter(ClassificationSeries classificationSeries) {
    this.classificationSeries = classificationSeries;
    this.timeStamp = new Date();
  }
}
