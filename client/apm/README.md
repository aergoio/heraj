# What is Aergo Package Manager(apm)?
The apm makes your development rapid. It suggests the cycle to import, refer, and upload packages.

# Project structure
<pre>
$USER_HOME/
  +.aergo_modules/

$PROJECT_HOME/
  | + xxxx/
  + aergo.json
</pre>
* aergo.json - project description file
* .aergo_modules - modules to be downloaded by apm   

## Command
* apm init
* apm install aergoio/athena-343
* apm build
* apm test
* apm publish

# Configuration(aergo.json)
* name: project name
* source: source file to build
* target: result when you type 'apm build'
* dependencies: package dependencies
* tests: test cases to run
* endpoint: endpoint to deploy or run (Default value is "localhost:7845")

## Examples
```json
{
  "name": "bylee/examples",
  "source": "src/main/lua/main.lua",
  "target": "app.lua",
  "dependencies": ["aergoio/athena-343"],
  "test": ["src/test/lua/test-main.lua"],
  "endpoint": "remotehost:3030"
}
```
