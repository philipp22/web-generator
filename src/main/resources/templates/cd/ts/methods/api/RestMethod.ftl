${tc.signature("restMethod", "restUtil", "typeUtil")}
{
	return this.http.${restMethod.method?lower_case}<${typeUtil.javaToTypescript(restMethod.returnType)}>(${restUtil.createRestUrl(restMethod)?replace("baseUrl", "this.baseUrl")}
<#if restMethod.bodyType??>
	, ${restUtil.getBodyName(restMethod)}
</#if>
<#if restMethod.authRequired>
	, {headers: this.authService.getAuthHeader()}
</#if>);
}
