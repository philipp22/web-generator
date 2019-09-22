${tc.signature("domain", "domains", "util")}
{
	return new ${domain.name}DTO(
<#list domain.attributes as attr>
	<#if (attr_index > 0)>,</#if>
	<#if util.isTypeDomain(attr.type, domains)>
		domain.get${attr.name?cap_first}().getId()
	<#else>
		domain.get${attr.name?cap_first}()
	</#if>
</#list>
	);
}
