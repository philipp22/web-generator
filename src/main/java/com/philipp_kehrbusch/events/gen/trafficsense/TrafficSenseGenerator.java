package com.philipp_kehrbusch.events.gen.trafficsense;

import com.philipp_kehrbusch.gen.webdomain.trafos.WebDomainGeneratorException;

public class TrafficSenseGenerator {

  public static void main(String[] args) throws WebDomainGeneratorException {
    AuthProviderGenerator.main(args);
    MarketplaceGenerator.main(args);
  }
}
