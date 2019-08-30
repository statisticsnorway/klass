package no.ssb.klass.designer.user;

import java.util.Set;

import no.ssb.klass.core.model.ClassificationEntityOperations;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.User;

/**
 * @author Mads Lundemo, SSB.
 */
public interface UserContext {

    boolean toggleFavorite(ClassificationSeries series);

    void addFavorite(ClassificationSeries series);

    void removeFavorite(ClassificationSeries series);

    Set<ClassificationSeries> getFavorites();

    boolean hasFavorites();

    boolean isFavorite(ClassificationSeries classification);

    boolean canUserAlterObject(ClassificationEntityOperations series);

    boolean isAdministrator();

    void setUser(User currentUser);

    User getDetachedUserObject();

    String getUsername();

    boolean hasUser();
}
