package com.philipp_kehrbusch.events.gen.trafos.frontend;

import com.google.common.base.CaseFormat;
import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawAttribute;
import com.philipp_kehrbusch.gen.webdomain.source.view.RawDerivedAttribute;
import com.philipp_kehrbusch.gen.webdomain.source.view.RawView;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDArtifactBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDAttributeBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDInterfaceBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDAttribute;
import com.philipp_kehrbusch.gen.webdomain.trafos.*;
import com.philipp_kehrbusch.gen.webdomain.util.TypeUtil;
import com.philipp_kehrbusch.gen.webdomain.util.ViewUtil;

import java.util.ArrayList;
import java.util.stream.Collectors;

@GlobalViewTrafo
public class ViewTrafo {

  @Transform
  public void transform(RawDomains domains, RawViews views, WebElements elements, GeneratorSettings settings)
          throws WebDomainGeneratorException {
    for(var view : views) {
      var name = view.getName();
      var attributes = new ArrayList<CDAttribute>();

      if (view.getDerivedAttributes().size() > 0) {
        for (var attr : view.getDerivedAttributes()) {
          var cdAttr = createViewAttribute(domains, view, attr);
          attributes.add(cdAttr);
        }
      }

      var builder = new CDInterfaceBuilder()
              .name(name)
              .addModifier("export");

      attributes.addAll(view.getAttributes().stream()
              .map(attr -> new CDAttributeBuilder()
                      .type(TypeUtil.javaToTypescript(attr.getType()))
                      .name(attr.getName())
                      .build())
              .collect(Collectors.toList()));
      builder.addAttributes(attributes);

      var imports = attributes.stream()
              .map(CDAttribute::getType)
              .filter(type -> TrafoUtils.isTypeDomain(TrafoUtils.getPrimitiveType(type), domains) ||
                      TrafoUtils.isTypeView(TrafoUtils.getPrimitiveType(type), views))
              .map(type -> {
                type = TrafoUtils.getPrimitiveType(type);
                return String.format("import {%s} from './%s'",
                        type,
                        CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, type));
              }).distinct().collect(Collectors.toCollection(ArrayList::new));

      elements.add(new WebElement(Targets.FRONTEND, name, "domain", imports, new CDArtifactBuilder()
              .name(name)
              .addInterface(builder.build())
              .build()));
    }
  }

  private CDAttribute createViewAttribute(RawDomains domains, RawView view, RawDerivedAttribute attr)
          throws WebDomainGeneratorException {
    var attrName = ViewUtil.getAttributeName(attr);
    var pathElements = ViewUtil.getAttributePathSplitted(attr);
    var attrType = ViewUtil.getAttributePathElementType(domains, view, attr, pathElements[pathElements.length - 1]);

    return new CDAttributeBuilder()
            .name(attrName)
            .type(TypeUtil.javaToTypescript(attrType))
            .build();
  }
}
