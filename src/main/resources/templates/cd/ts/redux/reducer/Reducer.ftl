${tc.signature("domainName")}
{
  switch (action.type) {
      case ${domainName}ActionTypes.${domainName}Loaded:
          return ${domainName?uncap_first}Adapter.upsertMany((action as ${domainName}LoadedAction).domains, state);
      case ${domainName}ActionTypes.${domainName}Created:
          return ${domainName?uncap_first}Adapter.addOne((action as ${domainName}CreatedAction).domain, state);
      case ${domainName}ActionTypes.${domainName}Updated:
          return ${domainName?uncap_first}Adapter.updateOne({
            id: (action as ${domainName}UpdatedAction).domain.id,
            changes: (action as ${domainName}UpdatedAction).domain,
          }, state);
      case ${domainName}ActionTypes.${domainName}Deleted:
          return ${domainName?uncap_first}Adapter.removeOne((action as ${domainName}DeletedAction).id, state);
      default:
          return state;
  }
}
