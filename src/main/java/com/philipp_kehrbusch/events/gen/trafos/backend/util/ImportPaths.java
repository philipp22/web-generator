package com.philipp_kehrbusch.events.gen.trafos.backend.util;

public class ImportPaths {

  public static String getRTEImport() {
    return "com.philipp_kehrbusch.web.rte.*";
  }

  public static String getDomainImport(String basePackage) {
    return basePackage + ".domain.*";
  }

  public static String getDAOImport(String basePackage) {
    return basePackage + ".dao.*";
  }

  public static String getDTOImport(String basePackage) {
    return basePackage + ".dto.*";
  }
}
