# KLASS SOLR

As of version 5 Solr is no longer available as a war and must be installed as a standalone service on Klass servers.
an explanation on why WARs are no longer released can be found [here](https://wiki.apache.org/solr/WhyNoWar)

We currently use version 5.5.0 which can be downloaded here:
https://archive.apache.org/dist/lucene/solr/5.5.0/ 

Installation should be as simple as extracting Solr to a preferred location and running `bin/install_solr_service.sh`.
Core configuration files for Klass should be place in `server\solr\Klass`

The configuration files can be found in `Klass-solr-{version}-config.zip` for nexus releases, or in `src/main/resources/solr/embedded/` if copied from source code.