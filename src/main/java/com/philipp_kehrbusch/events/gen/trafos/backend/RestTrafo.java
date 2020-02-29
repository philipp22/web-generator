package com.philipp_kehrbusch.events.gen.trafos.backend;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawDomain;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawRestMethod;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.*;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDMethod;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.GlobalDomainTrafo;
import com.philipp_kehrbusch.gen.webdomain.trafos.RawDomains;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;
import com.philipp_kehrbusch.gen.webdomain.trafos.WebElements;
import com.philipp_kehrbusch.gen.webdomain.util.ImportUtil;
import com.philipp_kehrbusch.gen.webdomain.util.StringUtil;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@GlobalDomainTrafo
public class RestTrafo {

  @Transform
  public void transform(RawDomains domains, WebElements elements, GeneratorSettings settings) {
    var imports = ImportUtil.getDefaultImports();
    imports.add("javax.transaction.*");
    imports.add("org.springframework.web.bind.annotation.*");
    imports.add("org.springframework.beans.factory.annotation.Autowired");
    imports.add("com.philipp_kehrbusch.web.rte.*");
    imports.add("com.philipp_kehrbusch.web.rte.exceptions.*");
    imports.add(settings.getBasePackage(Targets.BACKEND) + ".bl.*");
    imports.add(settings.getBasePackage(Targets.BACKEND) + ".view.*");
    imports.add(settings.getBasePackage(Targets.BACKEND) + ".domain.*");

    domains.stream()
            .filter(domain -> !TrafoUtils.hasAnnotation(domain, "NoRest"))
            .forEach(domain -> {
              var name = domain.getName() + "Controller";
              elements.add(new WebElement(Targets.BACKEND, name, "rest", imports,
                      new CDArtifactBuilder()
                              .name(name)
                              .addClass(new CDClassBuilder()
                                      .name(name)
                                      .addModifier("public")
                                      .addAnnotation("@RestController")
                                      .addAnnotation("@RequestMapping(\"/api/v1/" + StringUtil.firstLower(domain.getName()) + "\")")
                                      .addAttribute(new CDAttributeBuilder()
                                              .addAnnotation("@Autowired")
                                              .addModifier("private")
                                              .name("bl")
                                              .type("I" + domain.getName() + "BL")
                                              .build())
                                      .addMethods(createMethods(domain))
                                      .build())
                              .build()));
            });
  }

  private String getMappingAnnotation(RawRestMethod restMethod) {
    return "@"
            + StringUtil.firstUpper(restMethod.getMethod().name().toLowerCase())
            + "Mapping(\"" + this.createRouteString(restMethod.getRoute()) + "\")";
  }

  private List<CDMethod> createMethods(RawDomain domain) {
    return domain.getRestMethods().stream()
            .map(restMethod -> {
              var builder = new CDMethodBuilder()
                      .addModifier("public")
                      .name(restMethod.getName())
                      .returnType(restMethod.getReturnType())
                      .addAnnotation(getMappingAnnotation(restMethod))
                      .addException("PermissionDeniedException")
                      .addException("ServerErrorException")
                      .addException("ResourceNotFoundException")
                      .addException("ValidationException");

              if (!StringUtils.isEmpty(restMethod.getBodyType())) {
                builder.addArgument(new CDArgumentBuilder()
                        .type(restMethod.getBodyType())
                        .name(restMethod.getBodyTypeName())
                        .addAnnotation("@RequestBody")
                        .build());
              }

              builder.addArguments(restMethod.getQueryParams().entrySet().stream()
                      .map(entry -> new CDArgumentBuilder()
                              .type(entry.getValue())
                              .name(entry.getKey())
                              .addAnnotation("@RequestParam(\"" + entry.getKey() + "\")")
                              .build())
                      .collect(Collectors.toList()));

              builder.addArguments(restMethod.getRouteVariables().entrySet().stream()
                      .map(entry -> new CDArgumentBuilder()
                              .type(entry.getValue())
                              .name(entry.getKey())
                              .addAnnotation("@PathVariable(\"" + entry.getKey() + "\")")
                              .build())
                      .collect(Collectors.toList()));
              var res = builder.build();
              TemplateManager.getInstance().setTemplate(res, "java/methods/rest/RouteMethod.ftl", restMethod);
              return res;
            })
            .collect(Collectors.toList());
  }

  private String createRouteString(String route) {
    if (route.endsWith("/")) {
      route = route.substring(0, route.length() - 1);
    }
    var pattern = Pattern.compile("\\{([^\\s]+) ([^\\s]+)}");
    var matcher = pattern.matcher(route);
    var res = route;

    while (matcher.find()) {
      res = res.replace(matcher.group(0), "{" + matcher.group(2) + "}");
    }
    return res;
  }
}
