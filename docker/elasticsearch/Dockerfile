# Elasticsearch with Nori (Korean analyzer) plugin
ARG ELASTIC_VERSION

# https://www.docker.elastic.co/
FROM docker.elastic.co/elasticsearch/elasticsearch:${ELASTIC_VERSION:-8.11.0}

RUN bin/elasticsearch-plugin install --batch analysis-nori

#ENTRYPOINT ["/entrypoint.sh"]
