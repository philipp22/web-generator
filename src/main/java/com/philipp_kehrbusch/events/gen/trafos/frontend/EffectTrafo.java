package com.philipp_kehrbusch.events.gen.trafos.frontend;

import com.google.common.base.CaseFormat;
import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawDomain;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.*;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDAttribute;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;
import com.philipp_kehrbusch.gen.webdomain.trafos.WebElements;

import java.util.ArrayList;

//@SingleTrafo(includeAnnotated = "Domain")
public class EffectTrafo {

  @Transform
  public void transform(RawDomain domain, WebElements elements) {
    var name = domain.getName() + "ApiEffects";
    var imports = new ArrayList<String>();
    imports.add(String.format("import {%sApiService} from '@services/api/%s-api.service'",
            domain.getName(),
            CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, domain.getName())));
    imports.add("import {Actions, Effect, ofType} from '@ngrx/effects'");
    imports.add("import {Injectable} from '@angular/core'");
    imports.add("import {map, switchMap} from 'rxjs/operators'");
    imports.add(String.format("import {" +
                    "Create%sAction, LoadAll%sAction, Load%sByIdAction, Update%sAction, %sActionTypes, %sLoadedAction, " +
                    "%sCreatedAction, %sUpdatedAction} from '@redux/actions/%s.actions'",
            domain.getName(),
            domain.getName(),
            domain.getName(),
            domain.getName(),
            domain.getName(),
            domain.getName(),
            domain.getName(),
            domain.getName(),
            CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, domain.getName())));

    elements.add(new WebElement(Targets.FRONTEND, name, "redux/effects", imports,
            new CDArtifactBuilder()
                    .name(name)
                    .addClass(new CDClassBuilder()
                            .name(name)
                            .addModifier("export")
                            .addAnnotation("@Injectable()")
                            .addAttribute(createCreate(domain))
                            .addAttribute(createLoadAll(domain))
                            .addAttribute(createLoadById(domain))
                            .addAttribute(createUpdate(domain))
                            .addConstructor(new CDConstructorBuilder()
                                    .addArgument(new CDArgumentBuilder()
                                            .addModifier("private")
                                            .name("actions$")
                                            .type("Actions")
                                            .build())
                                    .addArgument(new CDArgumentBuilder()
                                            .addModifier("private")
                                            .name("apiService")
                                            .type(domain.getName() + "ApiService")
                                            .build())
                                    .build())
                            .build())
                    .build()));
  }

  private CDAttribute createCreate(RawDomain domain) {
    var create = new CDAttributeBuilder()
            .name("create$")
            .addAnnotation("@Effect()")
            .initialValue("")
            .build();
    TemplateManager.getInstance().setTemplate(create, "ts/redux/effect/ApiCreate.ftl", domain);
    return create;
  }

  private CDAttribute createLoadAll(RawDomain domain) {
    var loadAll = new CDAttributeBuilder()
            .name("loadAll$")
            .addAnnotation("@Effect()")
            .initialValue("")
            .build();
    TemplateManager.getInstance().setTemplate(loadAll, "ts/redux/effect/ApiLoadAll.ftl", domain);
    return loadAll;
  }

  private CDAttribute createLoadById(RawDomain domain) {
    var loadById = new CDAttributeBuilder()
            .name("loadById$")
            .addAnnotation("@Effect()")
            .initialValue("")
            .build();
    TemplateManager.getInstance().setTemplate(loadById, "ts/redux/effect/ApiLoadById.ftl", domain);
    return loadById;
  }

  private CDAttribute createUpdate(RawDomain domain) {
    var loadById = new CDAttributeBuilder()
            .name("update$")
            .addAnnotation("@Effect()")
            .initialValue("")
            .build();
    TemplateManager.getInstance().setTemplate(loadById, "ts/redux/effect/ApiUpdate.ftl", domain);
    return loadById;
  }
}
