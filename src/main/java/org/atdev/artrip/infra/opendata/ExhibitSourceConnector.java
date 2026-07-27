package org.atdev.artrip.infra.opendata;

public interface ExhibitSourceConnector {

    String sourceCode();

    CanonicalPage fetchPage(int pageNo, int size);

    CanonicalExhibitDetail fetchDetail(String externalId);
}
