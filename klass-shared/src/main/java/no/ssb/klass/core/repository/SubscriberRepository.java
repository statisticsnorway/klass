package no.ssb.klass.core.repository;

import java.util.List;
import java.util.Optional;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Subscriber;
import no.ssb.klass.core.service.dto.StatisticalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
  Optional<Subscriber> findOneByEmail(String email);

  @Query(
      "select s from Subscriber s join s.subscriptions as su where su.classification = ?1 and su.verification = 'VALID'")
  List<Subscriber> findVerifiedSubscribersOfClassification(ClassificationSeries classification);

  @Query(
      value =
          "select s  from Subscriber s join s.subscriptions as su where su.verification = 'VALID'")
  List<Subscriber> findAllSubscribers();

  @Query(
      value =
          "select count(*) from Subscriber s join s.subscriptions as su where su.verification = 'VALID'")
  int findNumberOfSubscribers();

  @Query(
      value =
          "select count(*) from Subscriber s join s.subscriptions as su where s.email LIKE '%ssb.no%' and su.verification = 'VALID'")
  int findNumberOfInternalSubscribers();

  @Query(
      value =
          "select count(*) from Subscriber s join s.subscriptions as su where s.email NOT LIKE '%ssb.no%' and su.verification = 'VALID'")
  int findNumberOfExternalSubscribers();

  @Query(
      value =
          "select new no.ssb.klass.core.service.dto.AccessCounterDto(s.email,count(s.email)) "
              + "from Subscriber s join s.subscriptions as su "
              + "join su.classification as c "
              + "where c.deleted = false and su.verification = 'VALID' "
              + "group by s.email "
              + "order by count(s.email) desc,s.email")
  Page<StatisticalEntity> getSubscriberReportAll(Pageable p);

  @Query(
      value =
          "select new no.ssb.klass.core.service.dto.AccessCounterDto(s.email,count(s.email)) "
              + "from Subscriber s join s.subscriptions as su "
              + "join su.classification as c "
              + "where c.deleted = false and su.verification = 'VALID' "
              + "and s.email LIKE '%ssb.no%' "
              + "group by s.email "
              + "order by count(s.email) desc,s.email")
  Page<StatisticalEntity> getSubscriberReportInternal(Pageable p);

  @Query(
      value =
          "select new no.ssb.klass.core.service.dto.AccessCounterDto(s.email, count(s.email)) "
              + "from Subscriber s join s.subscriptions as su "
              + "join su.classification as c "
              + "where c.deleted = false and su.verification = 'VALID' "
              + "and s.email NOT LIKE '%ssb.no%' "
              + "group by s.email "
              + "order by count(s.email) desc,s.email")
  Page<StatisticalEntity> getSubscriberReportExternal(Pageable p);

  @Query(
      value =
          "select  new no.ssb.klass.core.service.dto.SubscriberCounterDto"
              + "("
              + "substring(s.email, LOCATE('@', s.email)+1) as name,"
              + " count(*) as sub_count"
              + ") "
              + "from Subscriber s join s.subscriptions as su "
              + "join su.classification as c "
              + "where c.deleted = false and su.verification = 'VALID' "
              + "group by substring(s.email, LOCATE('@', s.email)+1) "
              + "order by sub_count desc,name")
  Page<StatisticalEntity> getSubscriberDomainReportAll(Pageable p);

  @Query(
      value =
          "select  new no.ssb.klass.core.service.dto.SubscriberCounterDto"
              + "("
              + "substring(s.email, LOCATE('@', s.email)+1) as name,"
              + " count(*) as sub_count"
              + ") "
              + "from Subscriber s join s.subscriptions as su "
              + "join su.classification as c "
              + "where c.deleted = false and su.verification = 'VALID' "
              + "and s.email LIKE '%ssb.no%' "
              + "group by substring(s.email, LOCATE('@', s.email)+1) "
              + "order by sub_count desc,name")
  Page<StatisticalEntity> getSubscriberDomainReportInternal(Pageable p);

  @Query(
      value =
          "select  new no.ssb.klass.core.service.dto.SubscriberCounterDto"
              + "("
              + "substring(s.email, LOCATE('@', s.email)+1) as name,"
              + " count(*) as sub_count"
              + ") "
              + "from Subscriber s join s.subscriptions as su "
              + "join su.classification as c "
              + "where c.deleted = false and su.verification = 'VALID' "
              + "and s.email NOT LIKE '%ssb.no%' "
              + "group by substring(s.email, LOCATE('@', s.email)+1) "
              + "order by sub_count desc,name")
  Page<StatisticalEntity> getSubscriberDomainReportExternal(Pageable p);

  @Query(
      value =
          "select  new no.ssb.klass.core.service.dto.SubscriberCounterDto"
              + "("
              + " case c.primaryLanguage when 0 then c.nameNo when 1 then c.nameNn when 2 then c.nameEn end as name, "
              + " count(c.id) as sub_count"
              + ") "
              + "from Subscriber s join s.subscriptions as su "
              + "join su.classification as c "
              + "where c.deleted = false and su.verification = 'VALID' "
              + "group by case c.primaryLanguage when 0 then c.nameNo when 1 then c.nameNn when 2 then c.nameEn end "
              + "order by sub_count desc,name")
  Page<StatisticalEntity> getSubscriberClassReportAll(Pageable p);

  @Query(
      value =
          "select  new no.ssb.klass.core.service.dto.SubscriberCounterDto"
              + "("
              + " case c.primaryLanguage when 0 then c.nameNo when 1 then c.nameNn when 2 then c.nameEn end as name, "
              + " count(c.id) as sub_count"
              + ") "
              + "from Subscriber s join s.subscriptions as su "
              + "join su.classification as c "
              + "where c.deleted = false and su.verification = 'VALID' "
              + "and s.email LIKE '%ssb.no%' "
              + "group by case c.primaryLanguage when 0 then c.nameNo when 1 then c.nameNn when 2 then c.nameEn end "
              + "order by sub_count desc,name")
  Page<StatisticalEntity> getSubscriberClassReportInternal(Pageable p);

  @Query(
      value =
          "select  new no.ssb.klass.core.service.dto.SubscriberCounterDto"
              + "("
              + " case c.primaryLanguage when 0 then c.nameNo when 1 then c.nameNn when 2 then c.nameEn end as name, "
              + " count(c.id) as sub_count"
              + ") "
              + "from Subscriber s join s.subscriptions as su "
              + "join su.classification as c "
              + "where c.deleted = false and su.verification = 'VALID' "
              + "and s.email NOT LIKE '%ssb.no%' "
              + "group by case c.primaryLanguage when 0 then c.nameNo when 1 then c.nameNn when 2 then c.nameEn end "
              + "order by sub_count desc,name")
  Page<StatisticalEntity> getSubscriberClassReportExternal(Pageable p);
}
