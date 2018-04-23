# h2schematest
Test for activeJDBC concerning schemas in H2 database

For testing, execute `RestServiceIT` via JUnit. Schema `a` is the default schema so the test `accessSchemaA()` should be working. `accessSchemaB()` should throw an `InitException` and return a status of `500` from the REST call.
