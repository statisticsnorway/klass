package no.ssb.klass.api.dto.hal;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.Lists;

import no.ssb.klass.core.model.Changelog;

import org.junit.jupiter.api.Test;

import java.util.List;

public class ChangelogResourceTest {
    @Test
    public void changelog() {
        // given
        Changelog changelog = new Changelog("user", "description");

        // when
        ChangelogResource subject = new ChangelogResource(changelog);

        // then
        assertNotNull(subject.getChangeOccured());
        assertEquals("description", subject.getDescription());
    }

    @Test
    public void convert() {
        // when
        List<ChangelogResource> result =
                ChangelogResource.convert(Lists.newArrayList(new Changelog("user", "description")));

        // then
        assertEquals(1, result.size());
    }
}
