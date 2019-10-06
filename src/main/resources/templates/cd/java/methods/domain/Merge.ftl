${tc.signature("domain", "domains", "util")}
{
<#list domain.attributes as attr>
	<#if attr.name != "id">
		<#if !util.hasAnnotation(attr, "@Readonly") && !util.isTypeDomain(attr.type, domains)>
		this.${attr.name} = other.get${attr.name?cap_first}();
		<#elseif !util.hasAnnotation(attr, "@Readonly")>
			<#if !util.isTypeCollection(attr.type)>
				this.${attr.name}.merge(other.get${attr.name?cap_first}());
			</#if>
		</#if>
	</#if>
</#list>
}