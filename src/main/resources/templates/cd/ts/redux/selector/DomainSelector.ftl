${tc.signature("domainName")}
createSelector(
	selectDomain,
	(state: DomainState) => state.${domainName?uncap_first},
)