${tc.signature("domain", "domains", "util")}
{
	var domain = new ${domain.name}(
<#assign first=true>
<#list domain.attributes as attr>
	<#if attr.name != "id">
		<#if !first>,</#if>
		<#if util.isTypeDomain(attr.type, domains)>
			${attr.type?uncap_first}DAO.findById(dto.get${attr.name?cap_first}Id()).orElse(null)
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
