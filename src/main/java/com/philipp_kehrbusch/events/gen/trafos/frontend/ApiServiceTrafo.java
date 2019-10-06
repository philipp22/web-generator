package com.philipp_kehrbusch.events.gen.trafos.frontend;

import com.google.common.base.CaseFormat;
import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawAttribute;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawDomain;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.*;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDAttribute;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDClass;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDConstructor;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDMethod;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.SingleTrafo;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;
import com.philipp_kehrbusch.gen.webdomain.trafos.WebElements;
import com.philipp_kehrbusch.gen.webdomain.util.StringUtil;
import com.philipp_kehrbusch.gen.webdomain.util.TypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SingleTrafo(includeAnnotated = "Domain")
public class ApiServiceTrafo {

  @Transform
  public void transform(RawDomain domain, WebElements elements, GeneratorSettings settings) {
    var handcoded = TrafoUtils.hasHandcodedServiceClass(domain, settings.getTargets().get(Targets.FRONTEND));
    var name = (handcoded ? "Super" : "") + domain.getName() + "ApiService";
    var imports = new ArrayList<String>();
    imports.add("import {Inject, Injectable} from '@angular/core'");
    imports.add("import {HttpClient} from '@angular/common/http'");
    imports.add("import {ApiService} from '@services/api/api.service'");
    imports.add("import {Observable} from 'rxjs'");
    imports.add("import {" + domain.getName() + "} from '@domain/" +
            CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, domain.getName()) + "'");

    var superClass = "ApiService<" + domain.getName();
    if (handcoded) {
      superClass += ", GET, CREATE";
    }
    superClass += ">";

    elements.add(new WebElement(Targets.FRONTEND, name, "services/api", imports,
            new CDArtifactBuilder()
                    .name(name)
                    .addClass(new CDClassBuilder()
                            .addModifier("export")
                            .name(name + (handcoded ? "<GET=User, CREATE=User>" : ""))
                            .superClass(superClass)
                            .addAnnotation("@Injectable({ providedIn: 'root' })")
                            .addConstructor(createConstructor(domain))
                            .addMethods(domain.getAttributes().stream()
                                    .filter(attr -> TrafoUtils.hasAnnotation(attr, "@GetAllBy"))
                                    .map(attr -> createGetAllBy(domain, attr))
                                    .collect(Collectors.toList()))
                            .addMethods(domain.getAttributes().stream()
                                    .filter(attr -> TrafoUtils.hasAnnotation(attr, "@GetOneBy"))
                                    .map(attr -> createGetOneBy(domain, attr))
                                    .collect(Collectors.toList()))
                            .build())
                    .build()));
  }

  private CDConstructor createConstructor(RawDomain domain) {
    var constructor = new CDConstructorBuilder()
            .name(domain.getName() + "ApiService")
            .addArgument(new CDArgumentBuilder()
                    .addAnnotation("@Inject(HttpClient)")
                    .type("HttpClient")
                    .name("http")
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(constructor, "ts/methods/api/Constructor.ftl",
            domain.getName());
    return constructor;
  }

  private CDMethod createGetAllBy(RawDomain domain, RawAttribute attr) {
    var getBy = new CDMethodBuilder()
            .name("getAllBy" + StringUtil.firstUpper(attr.getName()))
            .returnType("Observable<" + domain.getName() + "[]>")
            .addArgument(new CDArgumentBuilder()
                    .type(TypeUtil.javaToTypescript(attr.getType()))
                    .name(attr.getName())
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(getBy, "ts/methods/api/GetAllBy.ftl",
            domain.getName(), attr.getName());
    return getBy;
  }

  private CDMethod createGetOneBy(RawDomain domain, RawAttribute attr) {
    var getBy = new CDMethodBuilder()
            .name("getOneBy" + StringUtil.firstUpper(attr.getName()))
            .returnType("Observable<" + domain.getName() + ">")
            .addArgument(new CDArgumentBuilder()
                    .type(TypeUtil.javaToTypescript(attr.getType()))
                    .name(attr.getName())
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(getBy, "ts/methods/api/GetOneBy.ftl",
            domain.getName(), attr.getName());
    return getBy;
  }
}
