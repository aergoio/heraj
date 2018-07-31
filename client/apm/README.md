# What is Aergo Package Manager(apm)?
The apm makes your development rapid. It suggests the cycle to import, refer, and upload packages.

# Project structure
<pre>
$PROJECT_HOME/
  + .contract_modules/
  | + xxxx/
  + aergo.json
</pre>
* aergo.json - project description file
* .contract_modules - modules to be downloaded by apm   

## Command(Proposal)
* apm create ${PROJECT_NAME}
* apm install --save http://github.com/xxxx
* apm update
* apm build
* apm deploy
* apm publish
* apm test

