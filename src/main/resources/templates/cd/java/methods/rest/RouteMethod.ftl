${tc.signature("restMethod")}
{
<#assign first = true>
<#if restMethod.returnType != 'void'>return </#if>bl.${restMethod.name}(
<#if restMethod.bodyType??>${restMethod.bodyTypeName}<#assign first = false></#if>
<#list restMethod.routeVariables?keys as varName><#if !first>, </#if>${varName}<#assign first = false></#list>
	);
}