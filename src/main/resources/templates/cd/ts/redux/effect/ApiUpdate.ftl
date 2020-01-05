${tc.signature("domain")}
<#assign generic = "Update" + domain.name + "Action">
this.actions$.pipe(
	ofType<${generic}>(${domain.name}ActionTypes.Update${domain.name}),
	switchMap(action => this.apiService.update(action.domain).pipe(
		map(domain => new ${domain.name}UpdatedAction(domain)),
	)),
)