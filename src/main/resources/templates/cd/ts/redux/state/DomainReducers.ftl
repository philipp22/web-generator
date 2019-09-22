${tc.signature("domains")}
combineReducers({
<#list domains as domain>
	${domain.name?uncap_first}: ${domain.name?uncap_first}Reducer,
</#list>
})