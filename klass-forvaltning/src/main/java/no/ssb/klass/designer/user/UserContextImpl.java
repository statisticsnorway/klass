package no.ssb.klass.designer.user;

import com.google.common.collect.ImmutableSet;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.VaadinSessionScope;
import no.ssb.klass.core.model.ClassificationEntityOperations;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Role;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Mads Lundemo, SSB.
 */
@SpringComponent
@VaadinSessionScope
public class UserContextImpl implements UserContext {

    private static final Logger log = LoggerFactory.getLogger(UserContextImpl.class);

    private final UserService userService;
    private final String[] adminUsers;
    private final Environment environment;

    private User currentUser;

    public UserContextImpl(
            @Autowired
            UserService userService,
            @Value("${klass.security.roles.admin.users}")
            String[] adminUsers,
            @Autowired
            Environment environment
    ) {
        if (adminUsers == null) {
            this.adminUsers = new String[]{};
        } else {
            this.adminUsers = adminUsers;
        }
        this.environment = environment;
        log.debug("Users to assign admin role to {}", this.adminUsers);
        VaadinSession session = VaadinSession.getCurrent();
        this.userService = userService;
        User user = session.getAttribute(User.class);
        if (user == null) {
            log.error("User is null! Application features may be broken.");
        } else {
            log.debug("Setting user {}", user.getUsername());
            this.setUser(user);
        }
    }

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
        String userSection = getSectionCode(currentUser.getSection());
        String classificationSection = getSectionCode(series.getContactPerson().getSection());
        boolean result = userSection != null
                && isAdministrator()
                || Objects.equals(userSection, classificationSection);
        log.debug("User: {} section: {} can alter: {} classification: {} section: {}", currentUser.getUsername(), userSection, result, series.getNameInPrimaryLanguage(), classificationSection);
        return result;
    }

    /**
     * Extract the section code from the entire section string.
     *
     * @param section A string in the form "320 - Seksjon for befolkningsstatistikk"
     * @return the code in the form "320"
     */
    private static String getSectionCode(String section) {
        if (section == null) return null;
        return section.split(" - ")[0];
    }

    @Override
    public boolean isAdministrator() {
        return currentUser.isAdministrator();
    }

    private void saveChanges() {
        currentUser = userService.saveUser(currentUser);
    }

    public void setUser(User currentUser) {
        this.currentUser = updateOrCreateUser(currentUser);
        if (Arrays.stream(environment.getActiveProfiles()).noneMatch((profile) -> Objects.equals(profile, "hardcoded-user"))) {
            this.currentUser.setRole(getRoleForUser(this.currentUser));
        } else {
            log.warn("Hardcoded user! Not deducing role.");
        }
    }

    private Role getRoleForUser(User user) {
        Role role = Role.STANDARD;
        if (shouldHaveAdminRole(user, this.adminUsers)) {
            role = Role.ADMINISTRATOR;
        }
        log.info("User '{}' assigned role '{}'", user.getUsername(), role);
        return role;
    }

    public static boolean shouldHaveAdminRole(User user, String[] adminUsers) {
        return Arrays.stream(adminUsers).anyMatch((adminUser) -> {
            boolean result = Objects.equals(adminUser, user.getUsername());
            log.debug("Current user: {} | admin user: {} | result: {}", user.getUsername(), adminUser, result);
            return result;
        });
    }

    private User updateOrCreateUser(User currentUser) {
        User savedUser = userService.getUserByUserName(currentUser.getUsername());
        if (savedUser == null) {
            log.debug("New user {}. Persisting.", currentUser.getUsername());
            return userService.saveUser(currentUser);
        }
        if (!Objects.equals(savedUser.getSection(), currentUser.getSection()) && currentUser.getSection() != null) {
            log.debug("Updating section for user {} to {}.", currentUser.getUsername(), currentUser.getSection());
            savedUser.setSection(currentUser.getSection());
            return userService.saveUser(savedUser);
        }
        log.debug("Found user {}. No updates required.", currentUser.getUsername());
        return savedUser;
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
