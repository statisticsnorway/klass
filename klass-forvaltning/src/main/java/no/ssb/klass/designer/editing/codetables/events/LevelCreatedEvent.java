package no.ssb.klass.designer.editing.codetables.events;

import no.ssb.klass.core.model.Level;

public class LevelCreatedEvent {
    private final Level createdLevel;

    public LevelCreatedEvent(Level createdLevel) {
        this.createdLevel = createdLevel;
    }

    public Level getCreatedLevel() {
        return createdLevel;
    }
}