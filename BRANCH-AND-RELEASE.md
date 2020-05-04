# Branch model & Release process

## Branch model

Follow [successful git branch model](https://nvie.com/posts/a-successful-git-branching-model/)

## Release process

1. `git branch release/vx.x.x && git checkout release/vx.x.x`
2. Check protobuf version tag to target aergo version and update it if necessary.
3. Check annotations and `package-info.java`.
4. Update `CHANGELOG.MD`, docs directory.
5. Change aergo version of test (in `./test/aergo.properties`) and integration test with it.
6. Update version to x.x.x in `gradle.properties`, `README.md` && `git commit -m "Prepare for vx.x.x" && git push origin`.
7. If 6 success in travis ci, `git tag vx.x.x && git push origin vx.x.x`
8. Upload to bintray central.
```sh
# make sure bintray info(systemProp.bintrayUser, systemProp.bintrayKey)
# is ready on ~/.gradle/gradle.properties
# after deploy it, login to bintray and click publish button
> ./gradlew deploy
```
9. Upload heraj-x.x.x-all.jar file to releases.
```sh
# Making heraj-x.x.x-all.jar
> ./gradlew clean shadowJar
```
10. `git checkout develop && git merge release/vx.x.x && git checkout master && git merge release/vx.x.x`
11. Update version to x.x.x-SNAPSHOT in gradle.properties and `git commit -m "Start new version" && git push origin"`
