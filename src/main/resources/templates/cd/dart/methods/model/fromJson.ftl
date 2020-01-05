${tc.signature("view", "attributes", "util", "domains", "views")}
{
return ${view.name}(
<#list attributes as attr>
	<#if (attr_index > 0)>, </#if>${attr.name}:
	<#if util.isTypeDomain(attr.type, domains) || util.isTypeView(attr.type, views)>
		<#if attr.type?starts_with("List<")>
			${util.getPrimitiveType(attr.type)}.fromJsonArray(json['${attr.name}'])
		<#else>
			${util.getPrimitiveType(attr.type)}.fromJson(json['${attr.name}'])
		</#if>
	<#elseif attr.type?starts_with("List<")>
		${attr.type}.from(json['${attr.name}'])
	<#else>
		json['${attr.name}']
	</#if>
</#list>
	);
}