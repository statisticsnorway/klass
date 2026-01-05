package no.ssb.klass.core.repository;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.EMBEDDED;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;

import jakarta.transaction.Transactional;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.model.SearchWords;
import no.ssb.klass.core.service.dto.StatisticalEntity;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles({ConfigurationProfiles.POSTGRES_EMBEDDED, ConfigurationProfiles.MOCK_MAILSERVER})
@Transactional
@AutoConfigureEmbeddedDatabase(
        provider = EMBEDDED,
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
public class SearchWordsRepositoryTest {

    @Autowired private SearchWordsRepository searchWordsRepository;

    @Test
    public void findNumberOfSearchWordsTest() {
        makeSerarchWord("A", true);
        makeSerarchWord("B", true);
        makeSerarchWord("C", false);
        makeSerarchWord("A", true);
        int number = searchWordsRepository.getNumberOfSearchWords(getFromDate(), getToDate());
        assertEquals(3, number);
        number = searchWordsRepository.getNumberOfSearchWords(getFromDate(), getDate(1));
        assertEquals(0, number);
    }

    @Test
    public void findNumberOfMissedSearchWordsTest() {
        makeSerarchWord("A", true);
        makeSerarchWord("B", true);
        makeSerarchWord("C", false);
        makeSerarchWord("A", false);
        int number = searchWordsRepository.getNumberOfMiss(getFromDate(), getToDate());
        assertEquals(2, number);
        number = searchWordsRepository.getNumberOfMiss(getFromDate(), getDate(1));
        assertEquals(0, number);
    }

    @Test
    public void findSearchWordsTest() {
        makeSerarchWord("A", true);
        makeSerarchWord("B", true);
        makeSerarchWord("C", false);
        makeSerarchWord("A", true);

        Page<StatisticalEntity> result =
                searchWordsRepository.getSearchWords(
                        getFromDate(), getToDate(), PageRequest.of(0, 100));
        assertEquals(3, result.getTotalElements());
        List<StatisticalEntity> resultList = result.getContent();
        assertEquals("A", resultList.get(0).getName());
        assertEquals(Long.valueOf(2), resultList.get(0).getCount());
        assertEquals("B", resultList.get(1).getName());
        assertEquals(Long.valueOf(1), resultList.get(1).getCount());
        assertEquals("C", resultList.get(2).getName());
        assertEquals(Long.valueOf(1), resultList.get(2).getCount());
    }

    @Test
    public void findMissedSearchWordsTest() {
        makeSerarchWord("A", true);
        makeSerarchWord("B", false);
        makeSerarchWord("C", false);
        makeSerarchWord("A", true);
        makeSerarchWord("B", false);
        makeSerarchWord("C", false);
        makeSerarchWord("B", false);

        Page<StatisticalEntity> result =
                searchWordsRepository.getSearchWords(
                        false, getFromDate(), getToDate(), PageRequest.of(0, 100));
        assertEquals(2, result.getTotalElements());
        List<StatisticalEntity> resultList = result.getContent();
        assertEquals("B", resultList.get(0).getName());
        assertEquals(Long.valueOf(3), resultList.get(0).getCount());
        assertEquals("C", resultList.get(1).getName());
        assertEquals(Long.valueOf(2), resultList.get(1).getCount());
    }

    private void makeSerarchWord(String searchString, boolean hit) {
        SearchWords searchWord = new SearchWords(searchString, hit);
        searchWordsRepository.save(searchWord);
        searchWordsRepository.flush();
    }

    public static Date getFromDate() {
        return getDate(2);
    }

    public static Date getDate(int diff) {
        LocalDate from = LocalDate.now();
        from.minusDays(diff);
        return Date.from(from.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getToDate() {
        return new Date();
    }

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = {SearchWords.class})
    @ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
    static class Config {}
}
