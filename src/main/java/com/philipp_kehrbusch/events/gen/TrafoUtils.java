package com.philipp_kehrbusch.events.gen;

import com.google.common.base.CaseFormat;
import com.philipp_kehrbusch.gen.webdomain.Target;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawAttribute;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawDomain;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawRestMethod;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RestMethod;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDAttribute;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDClass;
import com.philipp_kehrbusch.gen.webdomain.trafos.RawDomains;

import java.io.File;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class TrafoUtils {

  public static boolean isTypeDomain(String type, RawDomains domains) {
    String regex = "List<(.*)>";
    type = type.replaceAll(regex, "$1");
    return domains.stream()
            .map(clazz -> clazz.getName().replaceAll("Super(.*)", "$1"))
            .collect(Collectors.toList())
            .contains(type);
  }

  public static boolean hasAnnotation(CDAttribute attr, String annotation) {
    return attr.getAnnotations().stream().anyMatch(a -> a.equalsIgnoreCase(annotation) ||
            a.equalsIgnoreCase("@" + annotation));
  }

  public static boolean hasAnnotation(RawAttribute attr, String annotation) {
    return attr.getAnnotations().stream().anyMatch(a -> a.equalsIgnoreCase(annotation) ||
            a.equalsIgnoreCase("@" + annotation));
  }

  public static boolean hasAnnotation(RawDomain clazz, String annotation) {
    return clazz.getAnnotations().stream().anyMatch(a -> a.equalsIgnoreCase(annotation) ||
            a.equalsIgnoreCase("@" + annotation));
  }

  public static boolean hasAnnotation(CDClass clazz, String annotation) {
    return clazz.getAnnotations().stream().anyMatch(a -> a.equalsIgnoreCase(annotation) ||
            a.equalsIgnoreCase("@" + annotation));
  }

  public static RawDomains getDomains(RawDomains entities) {
    return entities.stream()
            .filter(entity -> hasAnnotation(entity, "Domain"))
            .collect(Collectors.toCollection(RawDomains::new));
  }

  public static String getPrimitiveType(String type) {
    String regex = "List<(.*)>";
    return type.replaceAll(regex, "$1");
  }

  public static String appendToPrimitiveType(String type, String prefix, String suffix) {
    String regex = "List<(.*)>";
    var primitive = type.replaceAll(regex, "$1");
    return type.matches(regex) ? "List<" + prefix + primitive + suffix + ">" : prefix + primitive + suffix;
  }

  public static boolean isTypeCollection(String type) {
    return type.matches("List<.*>") || type.matches("Set<.*>");
  }

  public static String getLongCollection(String type) {
    return type.replaceAll("(.*)<.*>", "$1<Long>");
  }

  public static boolean hasHandcodedJavaClass(RawDomain domain, Target target) {
    var file = new File(getFileForDomain(domain.getName(), target).getAbsolutePath()
            .replace(target.getBasePath(), target.getBasePathHandcoded()));
    return file.exists();
  }

  public static boolean hasHandcodedServiceClass(RawDomain domain, Target target) {
    var file = Paths.get(target.getBasePathHandcoded(), "services", "api",
            CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, domain.getName()) + "-api.service.ts").toFile();
    return file.exists();
  }

  private static File getFileForDomain(String domainName, Target target) {
    var path = Paths.get(target.getBasePath(), packageToPath(target.getBasePackage()),
            "domain");
    return new File(Paths.get(path.toString(), target.getResolver().resolve(domainName)).toString());
  }

  private static String packageToPath(String pckg) {
    return pckg.replace(".", "/");
  }

  public static String getReturnType(RawDomain domain, String httpMethod) {
    if (httpMethod.equalsIgnoreCase("get")) {
      return getReturnType(domain, RestMethod.GET);
    } else if (httpMethod.equalsIgnoreCase("put")) {
      return getReturnType(domain, RestMethod.PUT);
    } else if (httpMethod.equalsIgnoreCase("post")) {
      return getReturnType(domain, RestMethod.POST);
    } else if (httpMethod.equalsIgnoreCase("delete")) {
      return getReturnType(domain, RestMethod.DELETE);
    }
    return null;
  }

  public static String getReturnType(RawDomain domain, RestMethod httpMethod) {
    return domain.getRestMethods().stream()
            .filter(rm -> rm.getMethod() == httpMethod).findFirst()
            .map(RawRestMethod::getReturnType)
            .orElse(null);
  }
}
