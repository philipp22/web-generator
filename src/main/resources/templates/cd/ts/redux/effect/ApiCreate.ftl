${tc.signature("domain")}
<#assign generic = "Create" + domain.name + "Action">
this.actions$.pipe(
	ofType<${generic}>(${domain.name}ActionTypes.Create${domain.name}),
	switchMap(action => this.apiService.create(action.domain).pipe(
		map(domain => new ${domain.name}CreatedAction(domain)),
	)),
)