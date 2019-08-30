package no.ssb.klass.designer.components;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.designer.ClassificationFamilyView;
import no.ssb.klass.designer.ClassificationListView;
import no.ssb.klass.designer.util.KlassTheme;
import no.ssb.klass.designer.util.VaadinUtil;

public class BreadcumbPanel extends HorizontalLayout {
    public BreadcumbPanel() {
        addStyleName("breadcrumb-row");
    }

    public void renderBreadcrumbs(List<Breadcrumb> breadcrumbs) {
        removeAllComponents();
        addComponent(createHomeButton());
        addComponent(createArrowLabel());

        Label arrow = null;
        for (Breadcrumb breadcrumb : breadcrumbs) {
            addComponent(breadcrumb.createButton());
            arrow = createArrowLabel();
            addComponent(arrow);
        }
        removeComponent(arrow);

    }

    private Label createArrowLabel() {
        return new Label(">");
    }

    private Button createHomeButton() {
        Button home = new Button(FontAwesome.HOME);
        home.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        home.addStyleName(KlassTheme.BUTTON_AS_LINK);
        home.setHeight("100%");
        home.addClickListener(event -> VaadinUtil.navigateTo(ClassificationFamilyView.NAME));
        return home;
    }

    public abstract static class Breadcrumb {
        public static List<Breadcrumb> newBreadcrumbs(ClassificationFamily classificationFamily) {
            List<Breadcrumb> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(new ClassificationFamilyBreadcrumb(classificationFamily));
            return breadcrumbs;
        }

        public static List<Breadcrumb> newBreadcrumbs(ClassificationSeries classification) {
            List<Breadcrumb> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(new ClassificationFamilyBreadcrumb(classification));
            breadcrumbs.add(new ClassificationBreadcrumb(classification));
            return breadcrumbs;
        }

        public static List<Breadcrumb> newBreadcrumbs(ClassificationVersion version) {
            List<Breadcrumb> breadcrumbs = newBreadcrumbs(version.getClassification());
            breadcrumbs.add(new ClassificationVersionBreadcrumb(version));
            return breadcrumbs;
        }

        public static List<Breadcrumb> newBreadcrumbs(ClassificationVariant variant) {
            List<Breadcrumb> breadcrumbs = newBreadcrumbs(variant.getClassificationVersion());
            breadcrumbs.add(new ClassificationVariantBreadcrumb(variant));
            return breadcrumbs;
        }

        public static List<Breadcrumb> newBreadcrumbs(CorrespondenceTable correspondenceTable, boolean isSource) {
            ClassificationVersion version;
            if (isSource) {
                version = correspondenceTable.getSource();
            } else {
                version = correspondenceTable.getTarget();
            }
            List<Breadcrumb> breadcrumbs = newBreadcrumbs(version);
            breadcrumbs.add(new CorrespondenceTableBreadcrumb(correspondenceTable, version));
            return breadcrumbs;
        }

        public final Button createButton() {
            Button button = new Button(getName());
            button.addStyleName(ValoTheme.BUTTON_BORDERLESS);
            button.addStyleName(KlassTheme.BUTTON_AS_LINK);
            button.setHeight("100%");
            button.addClickListener(createClickListener());
            return button;
        }

        protected abstract String getName();

        public abstract Long getClassificationFamilyId();

        /**
         * Specifies the selection that shall be done in ClassificationListView, i.e which classificationSeries,
         * version, etc shall be marked as selected.
         */
        public abstract ClassificationListViewSelection getClassificationListViewSelection();

        private ClickListener createClickListener() {
            return (event) -> {
                VaadinUtil.getKlassState().setClassificationListViewSelection(getClassificationListViewSelection());
                VaadinUtil.navigateTo(ClassificationListView.NAME, ImmutableMap.of(
                        ClassificationListView.PARAM_FAMILY_ID, getClassificationFamilyId().toString()));
            };
        }
    }

    private static class ClassificationFamilyBreadcrumb extends Breadcrumb {
        /*
         * Implementation Note. Prefer constructing based on ClassificationSeries since this will ensure that
         * ClassificationFamily is updated if classification changes its family
         */
        private final ClassificationSeries classification;
        private final ClassificationFamily classificationFamily;

        ClassificationFamilyBreadcrumb(ClassificationSeries classification) {
            this.classification = checkNotNull(classification);
            this.classificationFamily = null;
        }

        ClassificationFamilyBreadcrumb(ClassificationFamily classificationFamily) {
            this.classificationFamily = checkNotNull(classificationFamily);
            this.classification = null;
        }

        @Override
        public Long getClassificationFamilyId() {
            return classificationFamily != null ? classificationFamily.getId()
                    : classification.getClassificationFamily().getId();
        }

        @Override
        public ClassificationListViewSelection getClassificationListViewSelection() {
            return null;
        }

        @Override
        protected String getName() {
            return classificationFamily != null ? classificationFamily.getName()
                    : classification.getClassificationFamily().getName();
        }
    }

    private static class ClassificationBreadcrumb extends Breadcrumb {
        private final ClassificationSeries classification;

        ClassificationBreadcrumb(ClassificationSeries classification) {
            this.classification = classification;
        }

        @Override
        public Long getClassificationFamilyId() {
            return classification.getClassificationFamily().getId();
        }

        @Override
        protected String getName() {
            return classification.getNameInPrimaryLanguage();
        }

        @Override
        public ClassificationListViewSelection getClassificationListViewSelection() {
            return ClassificationListViewSelection.newClassificationListViewSelection(classification);
        }
    }

    private static class ClassificationVersionBreadcrumb extends Breadcrumb {
        private final ClassificationVersion version;

        ClassificationVersionBreadcrumb(ClassificationVersion version) {
            this.version = version;
        }

        @Override
        protected String getName() {
            return version.getNameInPrimaryLanguage();
        }

        @Override
        public Long getClassificationFamilyId() {
            return version.getClassification().getClassificationFamily().getId();
        }

        @Override
        public ClassificationListViewSelection getClassificationListViewSelection() {
            return ClassificationListViewSelection.newClassificationListViewSelection(version);
        }

    }

    private static class ClassificationVariantBreadcrumb extends Breadcrumb {
        private final ClassificationVariant variant;

        ClassificationVariantBreadcrumb(ClassificationVariant variant) {
            this.variant = variant;
        }

        @Override
        protected String getName() {
            return variant.getNameInPrimaryLanguage();
        }

        @Override
        public Long getClassificationFamilyId() {
            return variant.getOwnerClassification().getClassificationFamily().getId();
        }

        @Override
        public ClassificationListViewSelection getClassificationListViewSelection() {
            return ClassificationListViewSelection.newClassificationListViewSelection(variant);
        }
    }

    private static class CorrespondenceTableBreadcrumb extends Breadcrumb {
        private final CorrespondenceTable correspondenceTable;
        private final ClassificationVersion version;

        CorrespondenceTableBreadcrumb(CorrespondenceTable correspondenceTable, ClassificationVersion version) {
            this.correspondenceTable = correspondenceTable;
            this.version = version;
        }

        @Override
        protected String getName() {
            return correspondenceTable.getNameInPrimaryLanguage();
        }

        @Override
        public Long getClassificationFamilyId() {
            return version.getClassification().getClassificationFamily().getId();
        }

        @Override
        public ClassificationListViewSelection getClassificationListViewSelection() {
            return ClassificationListViewSelection.newClassificationListViewSelection(correspondenceTable, version);
        }
    }
}
