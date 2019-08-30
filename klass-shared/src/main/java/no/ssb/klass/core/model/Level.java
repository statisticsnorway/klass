package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import no.ssb.klass.core.util.Translatable;

@Entity
public class Level extends BaseEntity {
    @Column(nullable = false, length = 1000)
    private Translatable name;
    @Column(nullable = false)
    private final int levelNumber;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "level", orphanRemoval = true)
    private final List<ClassificationItem> classificationItems;
    @ManyToOne(optional = false)
    private StatisticalClassification statisticalClassification;

    // Dummy constructor, needed by Hibernate
    protected Level() {
        this.levelNumber = 0;
        this.classificationItems = null;
    }

    public Level(int levelNumber) {
        this(levelNumber, new Translatable("Nivå " + levelNumber, "Nivå " + levelNumber, "Level " + levelNumber));
    }

    public Level(int levelNumber, Translatable name) {
        checkArgument(levelNumber >= 1);
        this.levelNumber = levelNumber;
        this.name = name;
        this.classificationItems = new ArrayList<>();
    }

    /**
     * @return name for specified language, if no name an empty string is returned, never null
     */
    public String getName(Language language) {
        return name.getString(language);
    }

    /**
     * Not public since clients should add classificationItems through owning version/variant. This so that
     * version/variant can enforce no duplicated codes.
     */
    void addClassificationItem(ClassificationItem classificationItem) {
        checkNotNull(classificationItem);
        classificationItems.add(classificationItem);
        classificationItem.setLevel(this);
    }

    void removeClassificationItem(ClassificationItem classificationItem) {
        checkNotNull(classificationItem);
        classificationItems.remove(classificationItem);
        classificationItem.setLevel(null);
    }

    void setStatisticalClassification(StatisticalClassification statisticalClassification) {
        this.statisticalClassification = statisticalClassification;
    }

    public StatisticalClassification getStatisticalClassification() {
        return statisticalClassification;
    }

    public List<ClassificationItem> getClassificationItems() {
        return classificationItems;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setName(String name, Language language) {
        this.name = this.name.withLanguage(name, language);
    }

    public boolean isDeleted() {
        return statisticalClassification.isDeleted();
    }
}
