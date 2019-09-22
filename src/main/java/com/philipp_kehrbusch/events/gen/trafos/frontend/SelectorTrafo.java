package com.philipp_kehrbusch.events.gen.trafos.frontend;

import com.google.common.base.CaseFormat;
import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDArgumentBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDArtifactBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDConstantBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDMethodBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDClass;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDConstant;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDMethod;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.GlobalTrafo;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;

import java.util.ArrayList;
import java.util.List;

@GlobalTrafo
public class SelectorTrafo {

  @Transform
  public void transform(List<CDClass> domains, List<WebElement> elements, GeneratorSettings settings) {
    domains = TrafoUtils.getDomains(domains);
    domains.forEach(domain -> {
      var name = domain.getName() + "Selectors";
      var imports = new ArrayList<String>();
      imports.add("import {DomainState} from '@redux/state/domain.state'");
      imports.add("import {selectDomain} from '@redux/selectors/app.selectors'");
      imports.add("import {createSelector} from '@ngrx/store'");
      imports.add("import {EntityState} from '@ngrx/entity'");
      imports.add(String.format("import {%s} from '@domain/%s'",
              domain.getName(),
              CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, domain.getName())));

      elements.add(new WebElement(Targets.FRONTEND, name, "redux/selectors", imports,
              new CDArtifactBuilder()
                      .name(name)
                      .addConstant(createDomainSelector(domain))
                      .addFunction(createGetById(domain))
                      .addFunction(createGetByIds(domain))
                      .addConstant(createGetAll(domain))
                      .build()));
    });

    var imports = new ArrayList<String>();
    imports.add("import {DomainState} from '@redux/state/domain.state'");
    imports.add("import {createFeatureSelector} from '@ngrx/store'");

    elements.add(new WebElement(Targets.FRONTEND, "AppSelectors", "redux/selectors", imports,
            new CDArtifactBuilder()
                    .name("AppSelectors")
                    .addConstant(createFeatureSelector())
                    .build()));
  }

  private CDConstant createFeatureSelector() {
    var featureSelector = new CDConstantBuilder()
            .name("selectDomain")
            .addModifier("export")
            .build();
    TemplateManager.getInstance().setTemplate(featureSelector,
            "ts/redux/selector/DomainFeatureSelector.ftl");
    return featureSelector;
  }

  private CDConstant createDomainSelector(CDClass domain) {
    var domainSelector = new CDConstantBuilder()
            .name("select" + domain.getName())
            .addModifier("export")
            .build();
    TemplateManager.getInstance().setTemplate(domainSelector, "ts/redux/selector/DomainSelector.ftl",
            domain.getName());
    return domainSelector;
  }

  private CDMethod createGetById(CDClass domain) {
    var selector = new CDMethodBuilder()
            .name("get" + domain.getName() + "ById")
            .addModifier("export")
            .addArgument(new CDArgumentBuilder()
                    .name("id")
                    .type("number")
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(selector, "ts/redux/selector/GetByIdSelector.ftl",
            domain.getName());
    return selector;
  }

  private CDMethod createGetByIds(CDClass domain) {
    var selector = new CDMethodBuilder()
            .name("get" + domain.getName() + "ByIds")
            .addModifier("export")
            .addArgument(new CDArgumentBuilder()
                    .name("ids")
                    .type("number[]")
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(selector, "ts/redux/selector/GetByIdsSelector.ftl",
            domain.getName());
    return selector;
  }

  private CDConstant createGetAll(CDClass domain) {
    var selector = new CDConstantBuilder()
            .name("getAll" + domain.getName())
            .addModifier("export")
            .build();
    TemplateManager.getInstance().setTemplate(selector, "ts/redux/selector/GetAllSelector.ftl",
            domain.getName());
    return selector;
  }
}
