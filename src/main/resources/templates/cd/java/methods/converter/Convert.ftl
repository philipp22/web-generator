${tc.signature("domains", "view", "util", "viewFroms")}
{
	if (
<#list viewFroms as viewFrom>
	<#if (viewFrom_index > 0)> || </#if>
	${viewFrom?uncap_first} == null
</#list>
	) {
		return null;
	}

	return new ${view.name}(
<#list view.derivedAttributes as attr>
	<#if (attr_index > 0)>,</#if>
		get${util.joinAttributePathElements(attr)?cap_first}(${util.getAttributeSourceDomain(domains, view, attr).name?uncap_first})
</#list>
	);
}
