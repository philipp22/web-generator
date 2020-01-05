${tc.signature("domain", "url", "bodyType", "util", "domains", "views", "typeUtil")}
{
	return http.post(
		baseUrl${url},
		headers: Auth.getAuthHeaders()
<#if bodyType??>
	<#if util.isTypeDomain(bodyType, domains)>
		, body: jsonEncode(body.toJson())
	<#else>
		, body: jsonEncode(body)
	</#if>
</#if>
	)
<#if domain != "void">
.then((res) =>
	<#if util.isTypeView(domain, views)>
		${domain}.fromJson(jsonDecode(res.body)))
	<#else>
	jsonDecode(res.body))
	</#if>
</#if>;
}