${tc.signature("domain", "domains", "util")}
{
	if (domain == null) {
		return null;
	}

	var dto = new ${domain.name}DTO(
<#list domain.attributes as attr>
	<#if (attr_index > 0)>,</#if>
	<#assign primitiveType = util.getPrimitiveType(attr.type)>
	<#if util.isTypeDomain(primitiveType, domains)>
		<#if util.isTypeCollection(attr.type)>
				domain.get${attr.name?cap_first}().stream().map(${primitiveType}::getId).collect(Collectors.toList())
		<#else>
			domain.get${attr.name?cap_first}().getId()
		</#if>
	<#else>
		domain.get${attr.name?cap_first}()
	</#if>
</#list>
	);
	dto.setId(domain.getId());
	return dto;
}
