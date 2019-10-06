package com.philipp_kehrbusch.events.gen.trafos.frontend;

import com.google.common.base.CaseFormat;
import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDArtifactBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDAttributeBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDConstantBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDInterfaceBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDConstant;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.GlobalTrafo;
import com.philipp_kehrbusch.gen.webdomain.trafos.RawDomains;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;
import com.philipp_kehrbusch.gen.webdomain.trafos.WebElements;
import com.philipp_kehrbusch.gen.webdomain.util.StringUtil;

import java.util.ArrayList;
import java.util.stream.Collectors;

@GlobalTrafo
public class StateTrafo {

  @Transform
  public void transform(RawDomains domains, WebElements elements, GeneratorSettings settings) {
    domains = TrafoUtils.getDomains(domains);
    var name = "DomainState";
    var imports = new ArrayList<String>();
    imports.add("import {EntityState} from '@ngrx/entity'");
    imports.add("import {DomainState} from '@redux/state/domain.state'");
    imports.add("import {ActionReducer, combineReducers} from '@ngrx/store'");
    imports.addAll(domains.stream()
            .map(domain -> String.format("import {%s} from '@domain/%s'",
                    domain.getName(),
                    CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, domain.getName())))
            .collect(Collectors.toList()));
    imports.addAll(domains.stream()
            .map(domain -> String.format("import {%sReducer, initial%sState} from '@redux/reducers/%s.reducer'",
                    CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, domain.getName()),
                    domain.getName(),
                    CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, domain.getName())))
            .collect(Collectors.toList()));

    elements.add(new WebElement(Targets.FRONTEND, name, "redux/state", imports,
            new CDArtifactBuilder()
                    .name(name)
                    .addInterface(new CDInterfaceBuilder()
                            .name(name)
                            .addModifier("export")
                            .addAttributes(domains.stream()
                                    .map(domain -> new CDAttributeBuilder()
                                            .name(StringUtil.firstLower(domain.getName()))
                                            .type("EntityState<" + domain.getName() + ">")
                                            .build())
                                    .collect(Collectors.toList()))
                            .build())
                    .addConstant(createDomainReducers(domains))
                    .addConstant(createInitialDomainState(domains))
                    .build()));
  }

  private CDConstant createDomainReducers(RawDomains domains) {
    var reducers = new CDConstantBuilder()
            .name("domainReducers")
            .type("ActionReducer<DomainState>")
            .addModifier("export")
            .build();
    TemplateManager.getInstance().setTemplate(reducers, "ts/redux/state/DomainReducers.ftl", domains);
    return reducers;
  }

  private CDConstant createInitialDomainState(RawDomains domains) {
    var initialState = new CDConstantBuilder()
            .name("initialDomainState")
            .type("DomainState")
            .addModifier("export")
            .build();
    TemplateManager.getInstance().setTemplate(initialState, "ts/redux/state/InitialDomainState.ftl", domains);
    return initialState;
  }
}
