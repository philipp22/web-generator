${tc.signature("domain", "url", "util", "views", "typeUtil")}
{
	return http.get(baseUrl${url}, headers: Auth.getAuthHeaders())
<#if domain != "void">
.then((res) =>
	<#if util.isTypeView(domain, views)>
		${domain}.fromJson(jsonDecode(res.body)))
	<#else>
	jsonDecode(res.body))
	</#if>
</#if>;
}