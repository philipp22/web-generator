package com.philipp_kehrbusch.events.gen.trafos.app_flutter;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.gen.webdomain.FileNameResolvers;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawDomain;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.*;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDCode;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDMethod;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.*;
import com.philipp_kehrbusch.gen.webdomain.util.StringUtil;
import com.philipp_kehrbusch.gen.webdomain.util.TypeUtil;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@GlobalDomainTrafo
public class NetClientTrafo {

  @Transform
  public void transform(RawDomains domains, RawViews views, WebElements elements, GeneratorSettings settings) {
    if (!settings.getTargets().keySet().contains(Targets.APP)) {
      return;
    }

    domains.stream()
            .filter(domain -> !TrafoUtils.hasAnnotation(domain, "NoRest"))
            .forEach(domain -> {
              var imports = new ArrayList<String>();
              imports.add("'dart:convert'");
              imports.add("'package:http/http.dart' as http");
              imports.add(getBaseImportHwc(settings) + "/net/auth.dart'");
              imports.add(getBaseImportHwc(settings) + "/net/net-constants.dart'");

              var name = domain.getName() + "NetClient";

              var factory = new CDCode();
              TemplateManager.getInstance().setTemplate(factory, "dart/factories/NetClient.ftl", name);

              elements.add(new WebElement(Targets.APP, name, "net", imports,
                      new CDArtifactBuilder()
                              .name(name)
                              .addClass(new CDClassBuilder()
                                      .name(name)
                                      .addMethods(createMethods(domain, imports, views, domains, settings))
                                      .addAttribute(new CDAttributeBuilder()
                                              .type("String")
                                              .name("baseUrl")
                                              .initialValue("'http://' + NetConstants.HOST + '/api/v1/" + StringUtil.firstLower(domain.getName()) + "'")
                                              .build())
                                      .addArbitraryCode(factory)
                                      .build())
                              .build()));
            });
  }

  private List<CDMethod> createMethods(RawDomain domain, List<String> imports, RawViews views, RawDomains domains, GeneratorSettings settings) {
    var importTypes = new ArrayList<String>();

    return domain.getRestMethods().stream()
            .map(restMethod -> {
              var builder = new CDMethodBuilder()
                      .name(restMethod.getName())
                      .returnType("Future<" + TypeUtil.javaToDart(restMethod.getReturnType()) + ">");

              if (!StringUtils.isEmpty(restMethod.getBodyType())) {
                builder.addArgument(new CDArgumentBuilder()
                        .type(restMethod.getBodyType())
                        .name("body")
                        .build());
              }

              if (TrafoUtils.isTypeView(restMethod.getReturnType(), views) &&
                      !importTypes.contains(TrafoUtils.getPrimitiveType(restMethod.getReturnType()))) {
                importTypes.add(TrafoUtils.getPrimitiveType(restMethod.getReturnType()));
                imports.add(getBaseImportGen(settings) + "/model/" + FileNameResolvers.DART.resolve(
                        TrafoUtils.getPrimitiveType(restMethod.getReturnType())) + "'");
              }

              if (restMethod.getBodyType() != null &&
                      TrafoUtils.isTypeView(restMethod.getBodyType(), views) &&
                      !importTypes.contains(TrafoUtils.getPrimitiveType(restMethod.getBodyType()))) {
                importTypes.add(TrafoUtils.getPrimitiveType(restMethod.getBodyType()));
                imports.add(getBaseImportGen(settings) + "/model/" + FileNameResolvers.DART.resolve(
                        TrafoUtils.getPrimitiveType(restMethod.getBodyType())) + "'");
              }

              builder.addArguments(restMethod.getRouteVariables().entrySet().stream()
                      .map(entry -> new CDArgumentBuilder()
                              .type(TypeUtil.javaToDart(entry.getValue()))
                              .name(entry.getKey())
                              .build())
                      .collect(Collectors.toList()));
              var res = builder.build();

              switch (restMethod.getMethod()) {
                case GET:
                  if (restMethod.getReturnType().startsWith("List<")) {
                    TemplateManager.getInstance().setTemplate(res, "dart/methods/net/GetMany.ftl",
                            TrafoUtils.getPrimitiveType(restMethod.getReturnType()), createRouteString(restMethod.getRoute()),
                            new TrafoUtils(), views, new TypeUtil());
                  } else {
                    TemplateManager.getInstance().setTemplate(res, "dart/methods/net/GetOne.ftl",
                            TrafoUtils.getPrimitiveType(restMethod.getReturnType()), createRouteString(restMethod.getRoute()),
                            new TrafoUtils(), views, new TypeUtil());
                  }
                  break;
                case POST:
                  TemplateManager.getInstance().setTemplate(res, "dart/methods/net/Post.ftl",
                          TrafoUtils.getPrimitiveType(restMethod.getReturnType()), createRouteString(restMethod.getRoute()),
                          restMethod.getBodyType(), new TrafoUtils(), domains, views, new TypeUtil());
                  break;
                case PUT:
                  TemplateManager.getInstance().setTemplate(res, "dart/methods/net/Put.ftl",
                          TrafoUtils.getPrimitiveType(restMethod.getReturnType()), createRouteString(restMethod.getRoute()),
                          restMethod.getBodyType(), new TrafoUtils(), domains, views, new TypeUtil());
                  break;
                case DELETE:
                  TemplateManager.getInstance().setTemplate(res, "dart/methods/net/Delete.ftl",
                          TrafoUtils.getPrimitiveType(restMethod.getReturnType()), createRouteString(restMethod.getRoute()));
                  break;
              }
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
      res = res.replace(matcher.group(0), "' + " + matcher.group(2) + ".toString() + '");
    }
    return "+ '" + res + "'";
  }

  private String getBaseImportGen(GeneratorSettings settings) {
    return "'package:" + settings.getTargets().get(Targets.APP).getName() + "/gen/" +
            settings.getBasePackage(Targets.APP).replace(".", "/");
  }

  private String getBaseImportHwc(GeneratorSettings settings) {
    return "'package:" + settings.getTargets().get(Targets.APP).getName() + "/hwc/" +
            settings.getBasePackage(Targets.APP).replace(".", "/");
  }
}
