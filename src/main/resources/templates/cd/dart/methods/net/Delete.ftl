${tc.signature("domain", "url")}
{
	http.delete(baseUrl${url}, headers: Auth.getAuthHeaders());
}