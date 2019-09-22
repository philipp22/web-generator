${tc.signature("domainName", "attrName")}
{
	return this.http.get<${domainName}[]>(`${r"${this.baseUrl}"}/${attrName}/${r"${"}${attrName}}`);
}