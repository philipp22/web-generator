${tc.signature("domains", "view", "attr", "viewUtil", "typeUtil")}
{
<#assign lastPathElement = viewUtil.getAttributePathSplitted(attr)[0]>
<#assign lastPathElementType = viewUtil.getAttributeSourceDomain(domains, view, attr).name>

<#list viewUtil.getAttributePathSplittedWithoutDomain(attr) as pathElement>
	<#if (pathElement_index > 0)>
		<#assign obj = "res" + pathElement_index>
	<#else>
		<#assign obj = "domain">
	</#if>
	<#assign nextObj = "res" + (pathElement_index + 1)>
	<#assign pathElementType = viewUtil.getAttributePathElementType(domains, view, attr, pathElement)>
	var ${nextObj} = ${typeUtil.getDefaultComparisonString(typeUtil.getPrimitiveType(lastPathElementType), obj)} ?
	<#if lastPathElementType?starts_with("List<")>
		${obj}.stream()
		.map(${typeUtil.getPrimitiveType(lastPathElementType)}::get${pathElement?cap_first})
		.collect(Collectors.toList()) : ${typeUtil.getDefaultValueString(pathElementType, obj)};
	<#else>
		${obj}.get${pathElement?cap_first}() : ${typeUtil.getDefaultValueString(pathElementType, obj)};
	</#if>
	<#assign lastPathElement = pathElement>
	<#assign lastPathElementType = pathElementType>
</#list>
	return ${nextObj};
}