package no.ssb.klass.initializer.stabas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;

public class ClassificationVersionWrapper {
    private final String stabasVersionId;
    private final ClassificationVersion version;
    private final Map<String, Level> levelsMap;
    private final Map<String, ClassificationItemWrapper> itemsMap;

    public ClassificationVersionWrapper(String stabasVersionId, ClassificationVersion version) {
        this.version = version;
        this.stabasVersionId = stabasVersionId;
        this.levelsMap = new HashMap<>();
        this.itemsMap = new HashMap<>();
    }

    public void addLevel(String stabasLevelId, Level level) {
        levelsMap.put(stabasLevelId, level);
        version.addLevel(level);
    }

    public void addClassificationItems(List<ClassificationItemWrapper> classificationItems) {
        for (ClassificationItemWrapper classificationItem : classificationItems) {
            ClassificationItem parent = getParent(classificationItem.getParentStabasId());
            version.addClassificationItem(classificationItem.getItem(), classificationItem.getLevelNumber(), parent);
            if (itemsMap.put(classificationItem.getStabasId(), classificationItem) != null) {
                throw new IllegalStateException("Code is not unique: " + classificationItem.getStabasId());
            }
        }
    }

    private ClassificationItem getParent(String parentStabasId) {
        if (Strings.isNullOrEmpty(parentStabasId)) {
            return null;
        }
        parentStabasId = removeLevelId(StabasUtils.parseUrn(parentStabasId));
        ClassificationItemWrapper parentWrapper = itemsMap.get(parentStabasId);
        if (parentWrapper == null) {
            throw new IllegalArgumentException("No parent found for: " + parentStabasId);
        }
        return parentWrapper.getItem();
    }

    private String removeLevelId(String itemId) {
        // stabasId has levelId-itemId. Removing levelId
        return itemId.substring(itemId.lastIndexOf('-') + 1);
    }

    public ClassificationItem findItemWithStabasId(String stabasId) {
        if (stabasId == null) {
            return null;
        }
        stabasId = removeLevelId(StabasUtils.parseUrn(stabasId));

        ClassificationItemWrapper itemWrapper = itemsMap.get(stabasId);
        if (itemWrapper == null) {
            throw new IllegalArgumentException("No item with stabas id: " + stabasId);
        }
        return itemWrapper.getItem();
    }

    public boolean hasItemWithStabasId(String stabasId) {
        stabasId = removeLevelId(StabasUtils.parseUrn(stabasId));

        return itemsMap.get(stabasId) != null;
    }

    public void patch(ShortnamePatcher shortnamePatcher) {
        shortnamePatcher.patch(stabasVersionId, this);
    }

    public void publish() {
        for (Language language : Language.values()) {
            // TODO kmgv verifiser at dette fungerer.
            // Bruk http://www.byranettet.ssb.no/metadata/statusrapporter/rapporter/stabas_reports_report4.html for å
            // verifisere
            if (version.canPublish(language).isEmpty()) {
                version.publish(language);
            }
        }
    }

    public Level getLevel(String stabasLevelId) {
        Level level = levelsMap.get(StabasUtils.parseUrn(stabasLevelId));
        if (level == null) {
            throw new IllegalArgumentException("No level found with stabas id: " + stabasLevelId);
        }
        return level;
    }

    public ClassificationVersion getVersion() {
        return version;
    }

    public String getStabasVersionId() {
        return stabasVersionId;
    }

    static class ClassificationItemWrapper {
        private final String stabasId;
        private final ClassificationItem item;
        private final int levelNumber;
        private final String parentStabasId;

        ClassificationItemWrapper(String stabasId, ClassificationItem item, int levelNumber, String parentStabasId) {
            // stabasId has levelId-itemId. Removing levelId
            this.stabasId = stabasId.substring(stabasId.lastIndexOf('-') + 1);
            this.item = item;
            this.levelNumber = levelNumber;
            this.parentStabasId = parentStabasId;
        }

        public String getStabasId() {
            return stabasId;
        }

        public ClassificationItem getItem() {
            return item;
        }

        public int getLevelNumber() {
            return levelNumber;
        }

        public String getParentStabasId() {
            return parentStabasId;
        }
    }
}
