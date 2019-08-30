package no.ssb.klass.designer.util;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

public final class FavoriteUtils {

    private FavoriteUtils() {
    }

    public static Resource getFavoriteIcon(boolean favorite) {
        return favorite ? FontAwesome.STAR : FontAwesome.STAR_O;
    }
}
