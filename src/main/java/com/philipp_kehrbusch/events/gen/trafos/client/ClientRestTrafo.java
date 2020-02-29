package com.philipp_kehrbusch.events.gen.trafos.client;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawDomain;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.*;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDMethod;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.SingleDomainTrafo;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;
import com.philipp_kehrbusch.gen.webdomain.trafos.WebElements;
import com.philipp_kehrbusch.gen.webdomain.util.ImportUtil;
import com.philipp_kehrbusch.gen.webdomain.util.RestUtil;
import com.philipp_kehrbusch.gen.webdomain.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SingleDomainTrafo
public class ClientRestTrafo {

  @Transform
  public void transform(RawDomain domain, WebElements elements, GeneratorSettings settings) {
    if (domain.getRestMethods().size() > 0) {
      var name = domain.getName() + "Client";
      var imports = ImportUtil.getDefaultImports();
      imports.add(settings.getBasePackage(Targets.CLIENT_JAVA) + ".domain.*");
      imports.add("org.springframework.beans.factory.annotation.Autowired");
      imports.add("org.springframework.web.client.RestTemplate");
      imports.add("org.springframework.http.HttpMethod");
      imports.add("org.springframework.http.HttpEntity");
      imports.add("org.springframework.core.ParameterizedTypeReference");
      imports.add("org.springframework.stereotype.Service");

      elements.add(new WebElement(Targets.CLIENT_JAVA, name, "", imports,
              new CDArtifactBuilder()
                      .name(name)
                      .addClass(new CDClassBuilder()
                              .name(name)
                              .addAnnotation("@Service")
                              .addAttribute(new CDAttributeBuilder()
                                      .addModifier("private")
                                      .type("RestTemplate")
                                      .name("rest")
                                      .addAnnotation("@Autowired")
                                      .build())
                              .addAttribute(new CDAttributeBuilder()
                                      .addModifier("private")
                                      .type("String")
                                      .name("baseUrl")
                                      .initialValue("\"/api/v1/" + domain.getName() + "\"")
                                      .build())
                              .addMethods(createRestMethods(domain))
                              .build())
                      .build()));
    }
  }

  private List<CDMethod> createRestMethods(RawDomain domain) {
    var res = new ArrayList<CDMethod>();
    domain.getRestMethods().forEach(restMethod -> {
      var builder = new CDMethodBuilder()
              .addModifier("public")
              .name(restMethod.getName())
              .returnType(restMethod.getReturnType())
              .addArguments(restMethod.getRouteVariables().entrySet().stream()
                      .map(entry -> new CDArgumentBuilder()
                              .name(entry.getKey())
                              .type(entry.getValue())
                              .build())
                      .collect(Collectors.toList()));

      if (restMethod.getBodyType() != null) {
        builder.addArgument(new CDArgumentBuilder()
                .type(restMethod.getBodyType())
                .name(RestUtil.getBodyName(restMethod))
                .build());
      }

      var method = builder.build();
      TemplateManager.getInstance().setTemplate(method, "java/methods/rest/ClientRouteMethod.ftl",
              restMethod, new RestUtil());
      res.add(method);
    });
    return res;
  }
}
