package no.ssb.klass.designer.editing.codetables.events;

import no.ssb.klass.core.model.Level;

public class LevelDeletedEvent {
    private final Level deletedLevel;

    public LevelDeletedEvent(Level deletedLevel) {
        this.deletedLevel = deletedLevel;
    }

    public Level getDeletedLevel() {
        return deletedLevel;
    }
}