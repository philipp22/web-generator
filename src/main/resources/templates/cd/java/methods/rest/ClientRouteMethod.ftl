${tc.signature("restMethod", "restUtil")}
{
<#if restMethod.returnType != 'void'>return </#if>rest.exchange(
${restUtil.createRestUrl(restMethod)},
	HttpMethod.${restMethod.method},
<#if restMethod.bodyType??>
	new HttpEntity<>(${restUtil.getBodyName(restMethod)}),
<#else>
	HttpEntity.EMPTY,
</#if>
<#if restMethod.returnType?starts_with("List<")>
new ParameterizedTypeReference<${restMethod.returnType}>() {}
<#else>
	${restMethod.returnType}.class
</#if>
).getBody();
}