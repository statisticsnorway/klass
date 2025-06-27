FROM solr:5.5.2

USER root

COPY src/main/resources/solr/embedded/Klass /opt/solr/server/solr/mycores/Klass
RUN chown -R solr:solr /opt/solr/server/solr/mycores/Klass

USER solr

CMD ["solr-precreate", "Klass"]
