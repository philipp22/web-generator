${tc.signature("attributes")}
=> {
<#list attributes as attr>
	<#if (attr_index > 0)>, </#if>'${attr.name}': ${attr.name}
</#list>
};