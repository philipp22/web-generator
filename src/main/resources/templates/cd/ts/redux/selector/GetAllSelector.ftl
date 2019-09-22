${tc.signature("domainName")}
createSelector(
	select${domainName},
	(state: EntityState<${domainName}>) => Object.values(state.entities),
)