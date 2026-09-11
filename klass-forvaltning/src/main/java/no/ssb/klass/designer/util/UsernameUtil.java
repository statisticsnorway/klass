package no.ssb.klass.designer.util;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.ssb.klass.core.model.User;

public final class UsernameUtil {

    private static final Logger log = LoggerFactory.getLogger(UsernameUtil.class);

    private UsernameUtil() {
        // utility class
    }

    /**
     * Filters out users whose username contains '@' (email-style usernames), which are
     * considered illegal for contact person selection.
     */
    public static List<User> filterOutEmailUsernames(List<User> users) {
        List<User> kept = users.stream()
                .filter(user -> user.getUsername() != null && !user.getUsername().contains("@"))
                .collect(Collectors.toList());
        int filteredOut = users.size() - kept.size();
        if (filteredOut > 0) {
            log.debug("Filtered out {} of {} users with email-style usernames", filteredOut, users.size());
        }
        return kept;
    }
}
