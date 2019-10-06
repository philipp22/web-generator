package com.philipp_kehrbusch.events.gen.trafos.frontend;

import com.google.common.base.CaseFormat;
import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawDomain;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDArgumentBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDArtifactBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDConstantBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDMethodBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDConstant;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDMethod;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.SingleTrafo;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;
import com.philipp_kehrbusch.gen.webdomain.trafos.WebElements;
import com.philipp_kehrbusch.gen.webdomain.util.StringUtil;

import java.util.ArrayList;

@SingleTrafo(includeAnnotated = "Domain")
public class ReducerTrafo {

  @Transform
  public void transform(RawDomain domain, WebElements elements) {
    var name = domain.getName() + "Reducer";
    var imports = new ArrayList<String>();
    imports.add("import {createEntityAdapter, EntityState} from '@ngrx/entity'");
    imports.add("import {" + domain.getName() + "} from '@domain/" +
            CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, domain.getName()) + "'");
    imports.add(String.format("import {%sActions, %sActionTypes, %sLoadedAction, %sCreatedAction, %sUpdatedAction, " +
                    "%sDeletedAction} from '@redux/actions/%s.actions'",
            domain.getName(),
            domain.getName(),
            domain.getName(),
            domain.getName(),
            domain.getName(),
            domain.getName(),
            CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, domain.getName())));

    elements.add(new WebElement(Targets.FRONTEND, name, "redux/reducers", imports,
            new CDArtifactBuilder()
                    .name(name)
                    .addConstant(createAdapter(domain))
                    .addConstant(createInitialState(domain))
                    .addFunction(createReducer(domain))
                    .build()));
  }

  private CDConstant createAdapter(RawDomain domain) {
    var constant = new CDConstantBuilder()
            .name(StringUtil.firstLower(domain.getName()) + "Adapter")
            .addModifier("export")
            .build();
    TemplateManager.getInstance().setTemplate(constant, "ts/redux/reducer/Adapter.ftl", domain.getName());
    return constant;
  }

  private CDConstant createInitialState(RawDomain domain) {
    var constant = new CDConstantBuilder()
            .name("initial" + domain.getName() + "State")
            .addModifier("export")
            .build();
    TemplateManager.getInstance().setTemplate(constant, "ts/redux/reducer/InitialState.ftl",
            domain.getName());
    return constant;
  }

  private CDMethod createReducer(RawDomain domain) {
    var reducer = new CDMethodBuilder()
            .addModifier("export")
            .name(StringUtil.firstLower(domain.getName()) + "Reducer")
            .returnType("EntityState<" + domain.getName() + ">")
            .addArgument(new CDArgumentBuilder()
                    .name("state")
                    .defaultValue("initial" + domain.getName() + "State")
                    .build())
            .addArgument(new CDArgumentBuilder()
                    .name("action")
                    .type(domain.getName() + "Actions")
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(reducer, "ts/redux/reducer/Reducer.ftl", domain.getName());
    return reducer;
  }
}
