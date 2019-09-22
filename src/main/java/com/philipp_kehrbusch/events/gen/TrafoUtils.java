package com.philipp_kehrbusch.events.gen;

import com.philipp_kehrbusch.gen.webdomain.target.cd.CDAttribute;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDClass;

import java.util.List;
import java.util.stream.Collectors;

public class TrafoUtils {

  public static boolean isTypeDomain(String type, List<CDClass> domains) {
    return domains.stream()
            .map(CDClass::getName)
            .collect(Collectors.toList())
            .contains(type);
  }

  public static boolean hasAnnotation(CDAttribute attr, String annotation) {
    return attr.getAnnotations().stream().anyMatch(a -> a.equals(annotation));
  }

  public static boolean hasAnnotation(CDClass clazz, String annotation) {
    return clazz.getAnnotations().stream().anyMatch(a -> a.equals(annotation));
  }

  public static List<CDClass> getDomains(List<CDClass> entities) {
    return entities.stream()
            .filter(entity -> !hasAnnotation(entity, "@Form"))
            .collect(Collectors.toList());
  }
}
