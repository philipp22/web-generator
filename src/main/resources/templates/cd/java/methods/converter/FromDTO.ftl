${tc.signature("domain", "domains", "util")}
{
	if (dto == null) {
		return null;
	}

	var domain = new ${domain.name}(
<#assign first=true>
<#list domain.attributes as attr>
	<#if attr.name != "id">
		<#if !first>,</#if>
		<#assign primitiveType = util.getPrimitiveType(attr.type)>
		<#if util.isTypeDomain(primitiveType, domains)>
			<#if util.isTypeCollection(attr.type)>
				dto.get${attr.name?cap_first}().stream()
					.map(id -> ${primitiveType?uncap_first}DAO.findById(id).orElse(null))
					.collect(Collectors.toList())
			<#else>
				${primitiveType?uncap_first}DAO.findById(dto.get${attr.name?cap_first}()).orElse(null)
			</#if>
		<#else>
		dto.get${attr.name?cap_first}()
		</#if>
		<#assign first=false>
	</#if>
</#list>
	);
	domain.setId(dto.getId());
	return domain;
}
