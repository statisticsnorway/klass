package no.ssb.klass.designer.user;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableSet;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.VaadinSessionScope;

import no.ssb.klass.core.model.ClassificationEntityOperations;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.UserService;

/**
 * @author Mads Lundemo, SSB.
 */
@SpringComponent
@VaadinSessionScope
class UserContextImpl implements UserContext {

    @Autowired
    private UserService userService;

    private User currentUser;

    @Override
    public boolean toggleFavorite(ClassificationSeries series) {
        boolean isFavorite = currentUser.switchFavorite(series);
        saveChanges();
        return isFavorite;
    }

    @Override
    public void addFavorite(ClassificationSeries series) {
        currentUser.addFavorite(series);
        saveChanges();
    }

    @Override
    public void removeFavorite(ClassificationSeries series) {
        currentUser.removeFavorite(series);
        saveChanges();
    }

    @Override
    public boolean hasFavorites() {
        return currentUser.hasFavorites();
    }

    @Override
    public Set<ClassificationSeries> getFavorites() {
        return ImmutableSet.copyOf(currentUser.getFavorites().stream()
                .filter(fav -> !fav.isDeleted()).collect(Collectors.toSet()));
    }

    @Override
    public boolean isFavorite(ClassificationSeries classification) {
        return currentUser.isFavorite(classification);
    }

    @Override
    public boolean canUserAlterObject(ClassificationEntityOperations series) {
        return isAdministrator() || Objects.equals(currentUser.getSection(), series.getContactPerson().getSection());
    }

    @Override
    public boolean isAdministrator() {
        return currentUser.isAdministrator();
    }

    private void saveChanges() {
        currentUser = userService.saveUser(currentUser);
    }

    public void setUser(User currentUser) {
        if (hasUser()) {
            throw new RuntimeException("User already set");
        }
        this.currentUser = currentUser;
    }

    @Override
    public boolean hasUser() {
        return currentUser != null;
    }

    @Override
    public User getDetachedUserObject() {
        return currentUser;
    }

    @Override
    public String getUsername() {
        return currentUser.getUsername();
    }
}
