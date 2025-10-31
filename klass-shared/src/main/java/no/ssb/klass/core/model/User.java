package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "\"user\"",
        indexes = {
        @Index(columnList = "username", name = "user_username_idx", unique = true),
        @Index(columnList = "fullname", name = "user_fullname_idx")
})
public class User extends BaseEntity {
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String fullname;
    @Column(nullable = false)
    private String section;
    @Column(nullable = false, columnDefinition = "integer")
    private Role role;
    @Column
    private String email;
    @Column
    private String phone;

    @ManyToMany
    // Uses Set instead of List, see http://lkumarjain.blogspot.no/2013/07/why-hibernate-does-delete-all-entries.html
    private Set<ClassificationSeries> favorites;

    public User(String username, String fullname, String section) {
        this.username = checkNotNull(username);
        this.fullname = checkNotNull(fullname);
        this.section = checkNotNull(section);
        this.role = Role.STANDARD;
        this.favorites = new HashSet<>();
    }

    protected User() {
    }

    public String getSection() {
        return section;
    }

    public String getFullname() {
        return fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setFullname(String fullname) {
        this.fullname = checkNotNull(fullname);
    }

    public void setSection(String section) {
        this.section = checkNotNull(section);
    }

    public boolean isFavorite(ClassificationSeries classification) {
        return favorites.contains(classification);
    }

    public void addFavorite(ClassificationSeries classification) {
        favorites.add(classification);
    }

    public void removeFavorite(ClassificationSeries classification) {
        favorites.remove(classification);
    }

    public Set<ClassificationSeries> getFavorites() {
        return new HashSet<>(favorites);
    }

    public Role getRole() {
        return role;
    }

    public boolean isAdministrator() {
        return Role.ADMINISTRATOR == role;
    }

    public void setRole(Role role) {
        this.role = checkNotNull(role);
    }

    public boolean switchFavorite(ClassificationSeries favorite) {
        if (isFavorite(favorite)) {
            removeFavorite(favorite);
            return false;
        } else {
            addFavorite(favorite);
            return true;
        }
    }

    public boolean hasFavorites() {
        return !favorites.isEmpty();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
