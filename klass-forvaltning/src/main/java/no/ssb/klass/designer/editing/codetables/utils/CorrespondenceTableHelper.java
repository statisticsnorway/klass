package no.ssb.klass.designer.editing.codetables.utils;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Level;

public final class CorrespondenceTableHelper {
    private CorrespondenceTableHelper() {
        // Utility class
    }

    public static void throwUnsupportedException() {
        throw new UnsupportedOperationException(
                "Not supported, use overloaded method that has CorrespondenceTable as argument");
    }

    public static List<ClassificationItem> findClassificationItems(ClassificationVersion version,
            Optional<Level> level) {
        List<ClassificationItem> classificationItems = new ArrayList<>();
        if (level.isPresent()) {
            classificationItems.addAll(
                    level.get().getClassificationItems().stream().sorted().collect(toList()));
        } else {
            // All levels
            Optional<Level> optionalLevel = version.getFirstLevel();
            while (optionalLevel.isPresent()) {
                classificationItems.addAll(
                        optionalLevel.get().getClassificationItems().stream().sorted().collect(toList()));
                optionalLevel = version.getNextLevel(optionalLevel.get());
            }
        }
        return classificationItems;
    }

    public static boolean alreadyContainsMapping(CorrespondenceTable correspondenceTable, CorrespondenceMap map) {
        if (correspondenceTable.alreadyContainsIdenticalMap(map)) {
            Notification.show("Mapping finnes allerede for: " + extractNameOfCorrespondenceMap(map),
                    Type.WARNING_MESSAGE);
            return true;
        }
        return false;
    }

    private static String extractNameOfCorrespondenceMap(CorrespondenceMap map) {
        StringBuilder builder = new StringBuilder();
        builder.append(extractCode(map.getSource()));
        builder.append(" - ");
        builder.append(extractCode(map.getTarget()));
        return builder.toString();
    }

    private static String extractCode(Optional<ClassificationItem> classificationItem) {
        if (classificationItem.isPresent()) {
            return classificationItem.get().getCode();
        } else {
            return "<ingen>";
        }
    }
}