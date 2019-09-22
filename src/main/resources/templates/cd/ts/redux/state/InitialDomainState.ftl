${tc.signature("domains")}
{
<#list domains as domain>
	${domain.name?uncap_first}: initial${domain.name}State,
</#list>
}