package no.ssb.klass.core.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;

import java.util.List;

public class ClassificationFamilySummaryRes {
    private final ClassificationFamilyRepository classificationFamilyRepository;

    public ClassificationFamilySummaryRes(ClassificationFamilyRepository classificationFamilyRepository) {
        this.classificationFamilyRepository = classificationFamilyRepository;
    }

    public List<ClassificationFamily> findPublicClassificationFamilies(){
        return classificationFamilyRepository.findAll();
    }

    public void printClassificationFamilyClassifications() throws JsonProcessingException {
        List<ClassificationFamily> classificationFamilies = findPublicClassificationFamilies();
        for (ClassificationFamily classificationFamily : classificationFamilies) {
            ObjectMapper objectMapper = new ObjectMapper();
            String family = objectMapper.writeValueAsString(classificationFamily.getName());
            String family2 = objectMapper.writeValueAsString(classificationFamily.getIconPath());
            String family3 = objectMapper.writeValueAsString(classificationFamily.getId());

            List<ClassificationSeries> res = classificationFamily.getPublicClassificationSeries();
            for (ClassificationSeries classificationSeries : res) {
                String ser = objectMapper.writeValueAsString(classificationSeries.getContactPerson());
                System.out.println("Contact person: " + ser);
                String ser2 = objectMapper.writeValueAsString(classificationSeries.isCopyrighted());
                System.out.println("Is copyrighted: " + ser2);
                String ser3 = objectMapper.writeValueAsString(classificationSeries.isDeleted());
                System.out.println("Is deleted: " + ser3);
                List<ClassificationVersion> versions = classificationSeries.getClassificationVersions();
                for (ClassificationVersion classificationVersion : versions) {
                    String vary = objectMapper.writeValueAsString(classificationVersion.isPublishedInAnyLanguage());
                    System.out.println("Version published: " + vary);
                }
            }
            System.out.println("Family name: " + family);
            System.out.println("Icon path: " + family2);
            System.out.println("Id: " + family3);
        }
    }
}
