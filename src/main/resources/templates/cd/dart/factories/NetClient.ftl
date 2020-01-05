${tc.signature("name")}

factory ${name}() {
  if (_singleton == null) {
    _singleton = ${name}._();
  }
  return _singleton;
}
${name}._();
static ${name} _singleton;
