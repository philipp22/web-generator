package com.philipp_kehrbusch.events.gen.trafos.frontend;

import com.google.common.base.CaseFormat;
import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawDomain;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawRestMethod;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.*;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDConstructor;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDMethod;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.*;
import com.philipp_kehrbusch.gen.webdomain.util.RestUtil;
import com.philipp_kehrbusch.gen.webdomain.util.StringUtil;
import com.philipp_kehrbusch.gen.webdomain.util.TypeUtil;

import java.util.ArrayList;
import java.util.stream.Collectors;

@GlobalDomainTrafo
public class ApiServiceTrafo {

  @Transform
  public void transform(RawDomains domains, RawViews views, WebElements elements, GeneratorSettings settings) {
    if (!settings.getTargets().containsKey(Targets.FRONTEND)) {
      return;
    }

    domains.forEach(domain -> {
      var handcoded = TrafoUtils.hasHandcodedServiceClass(domain, settings.getTargets().get(Targets.FRONTEND));
      var name = (handcoded ? "Super" : "") + domain.getName() + "ApiService";
      var imports = new ArrayList<String>();
      imports.add("import {Inject, Injectable} from '@angular/core'");
      imports.add("import {HttpClient} from '@angular/common/http'");
      imports.add("import {AuthService} from '../../../hwc/auth.service'");
      imports.add("import {Observable} from 'rxjs'");

      var importTypes = domain.getRestMethods().stream()
              .filter(method -> method.getBodyType() != null &&
                      (TrafoUtils.isTypeDomain(method.getBodyType(), domains) || TrafoUtils.isTypeView(method.getBodyType(), views)))
              .map(RawRestMethod::getBodyType).collect(Collectors.toSet());

      importTypes.addAll(domain.getRestMethods().stream()
              .filter(method -> !method.getReturnType().equals("void") &&
                      (TrafoUtils.isTypeDomain(method.getReturnType(), domains) || TrafoUtils.isTypeView(method.getReturnType(), views)))
              .map(RawRestMethod::getReturnType).collect(Collectors.toSet()));

      imports.addAll(importTypes.stream()
              .map(type -> "import {" + TypeUtil.getPrimitiveType(type) + "} from '../../domain/" +
                      CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, TypeUtil.getPrimitiveType(type)) + "'")
              .collect(Collectors.toSet()));

//    var superClass = "ApiService<" + domain.getName();
//    if (handcoded) {
//      superClass += ", GET, CREATE";
//    }
//    superClass += ">";

      elements.add(new WebElement(Targets.FRONTEND, name, "services/api", imports,
              new CDArtifactBuilder()
                      .name(name)
                      .addClass(new CDClassBuilder()
                              .addModifier("export")
                              .name(name + (handcoded ? "<GET=User, CREATE=User>" : ""))
//                            .superClass(superClass)
                              .addAnnotation("@Injectable({ providedIn: 'root' })")
                              .addConstructor(createConstructor(domain))
                              .addMethods(domain.getRestMethods().stream()
                                      .map(this::createRestMethod)
                                      .collect(Collectors.toList()))
                              .addAttribute(new CDAttributeBuilder()
                                      .type("string")
                                      .name("baseUrl")
                                      .initialValue("'/api/v1/" + StringUtil.firstLower(domain.getName()) + "'")
                                      .build())
                              .build())
                      .build()));
    });

    elements.add(new WebElement(Targets.FRONTEND, "index", "services/api", new ArrayList<>(),
            new CDArtifactBuilder()
                    .name("index")
                    .addExports(domains.stream()
                            .filter(domain -> domain.getRestMethods().size() > 0)
                            .map(domain -> "* from './" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, domain.getName()) + "-api.service'")
                            .collect(Collectors.toList()))
                    .build()));
  }

  private CDConstructor createConstructor(RawDomain domain) {
    var constructor = new CDConstructorBuilder()
            .name(domain.getName() + "ApiService")
            .addArgument(new CDArgumentBuilder()
                    .addModifier("protected")
                    .type("HttpClient")
                    .name("http")
                    .build())
            .addArgument(new CDArgumentBuilder()
                    .addModifier("protected")
                    .type("AuthService")
                    .name("authService")
                    .build())
            .build();
//    TemplateManager.getInstance().setTemplate(constructor, "ts/methods/api/Constructor.ftl",
//            domain.getName());
    return constructor;
  }

  private CDMethod createRestMethod(RawRestMethod restMethod) {
    var methodBuilder = new CDMethodBuilder()
            .name(restMethod.getName())
            .returnType("Observable<" + TypeUtil.javaToTypescript(restMethod.getReturnType()) + ">")
            .addArguments(RestUtil.getRouteVariables(restMethod.getRoute()).entrySet().stream()
                    .map(entry -> new CDArgumentBuilder()
                            .type(TypeUtil.javaToTypescript(entry.getValue()))
                            .name(entry.getKey())
                            .build())
                    .collect(Collectors.toList()))
            .addArguments(restMethod.getQueryParams().entrySet().stream()
                    .map(entry -> new CDArgumentBuilder()
                            .type(TypeUtil.javaToTypescript(entry.getValue()))
                            .name(entry.getKey())
                            .build())
                    .collect(Collectors.toList()));
    if (restMethod.getBodyType() != null) {
      methodBuilder.addArgument(new CDArgumentBuilder()
              .type(TypeUtil.javaToTypescript(restMethod.getBodyType()))
              .name(RestUtil.getBodyName(restMethod))
              .build());
    }
    var method = methodBuilder.build();
    TemplateManager.getInstance().setTemplate(method, "ts/methods/api/RestMethod.ftl",
            restMethod, new RestUtil(), new TypeUtil());
    return method;
  }
}
