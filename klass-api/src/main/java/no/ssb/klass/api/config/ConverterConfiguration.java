package no.ssb.klass.api.config;

import no.ssb.klass.api.converters.ClassificationVariantCsvConverter;
import no.ssb.klass.api.converters.ClassificationVersionCsvConverter;
import no.ssb.klass.api.converters.CodeChangeListCsvConverter;
import no.ssb.klass.api.converters.CodeListCsvConverter;
import no.ssb.klass.api.converters.CorrespondenceItemListCsvConverter;
import no.ssb.klass.api.converters.CorrespondenceTableCsvConverter;
import no.ssb.klass.api.dto.CodeChangeList;
import no.ssb.klass.api.dto.CodeList;
import no.ssb.klass.api.dto.CorrespondenceItemList;
import no.ssb.klass.api.dto.hal.ClassificationVariantResource;
import no.ssb.klass.api.dto.hal.ClassificationVersionResource;
import no.ssb.klass.api.dto.hal.CorrespondenceTableResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

@Configuration
public class ConverterConfiguration {

  @Bean
  public HttpMessageConverter<CorrespondenceTableResource> correspondenceTableCsvConverter() {
    return new CorrespondenceTableCsvConverter();
  }

  @Bean
  public HttpMessageConverter<ClassificationVersionResource> classificationVersionCsvConverter() {
    return new ClassificationVersionCsvConverter();
  }

  @Bean
  public HttpMessageConverter<ClassificationVariantResource> classificationVariantCsvConverter() {
    return new ClassificationVariantCsvConverter();
  }

  @Bean
  public HttpMessageConverter<CodeList> codeListCsvConverter() {
    return new CodeListCsvConverter();
  }

  @Bean
  public HttpMessageConverter<CodeChangeList> codeChangeListCsvConverter() {
    return new CodeChangeListCsvConverter();
  }

  @Bean
  public HttpMessageConverter<CorrespondenceItemList> correspondenceItemListCsvConverter() {
    return new CorrespondenceItemListCsvConverter();
  }
}
