#!/bin/bash
# resolve links - $0 may be a softlink
if [ -z "$PROJECT_HOME" ];then
  PRG="$0"
  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
      PRG="$link"
    else
      PRG=`dirname "$PRG"`/"$link"
    fi
  done
  
  cd $(dirname $PRG)
  export PROJECT_HOME=`pwd`
  cd -&>/dev/null
fi

################################################################################
# Definition
readonly VERSION=$(grep "version" "$PROJECT_HOME/gradle.properties" | cut -d"=" -f2)
readonly BUILD_WORKSPACE=$PROJECT_HOME/build
readonly PROJECT_PREFIX="heraj"
readonly LIB_CORES=("wallet" "smart-contract" "transport")
readonly LIB_ALL="smart-contract"
readonly FAT_POSTFIX="fat"
readonly ALL_POSTFIX="all"

function print-usage() {
  echo "build.sh [command]"
  echo "Commands: "
  echo "  clean       delete and reset intermediate obj in build"
  echo "  gradle      compile source to executable using gradle"
  echo "  install     install to maven local"
  echo "  deploy      upload to jcenter"
  echo "  test        test built executable"
  echo "  it          integraion test (with docker)"
  echo "  docs        generate documents"
  echo "  pack        pack generated jar files info *.zip, *.tar.gz"
  echo "  fat         make single fat jar file"
}

function clean-workspace() {
  rm -rf $BUILD_WORKSPACE
  $PROJECT_HOME/gradlew clean
}
function execute-gradle() {
  $PROJECT_HOME/gradlew clean build test alljacoco alljavadoc
}
function execute-install() {
  $PROJECT_HOME/gradlew install
}
function execute-deploy() {
  echo "Version: $VERSION"
  if [[ $VERSION == *SNAPSHOT ]]; then
    echo "Uploading SNAPSHOT is not supported"
    exit -1
  else
    $PROJECT_HOME/gradlew bintrayUpload
  fi
}
function execute-test() {
  $PROJECT_HOME/gradlew test jacocoTestReport
}
function execute-integration-test() {
  $PROJECT_HOME/test/run-it.sh
}
function execute-documentation() {
  gem install bundler
  bundle install --gemfile $PROJECT_HOME/assembly/doc/gh-pages/Gemfile
  git clone $(git remote get-url origin) $BUILD_WORKSPACE/heraj-doc -b gh-pages
  git -C $BUILD_WORKSPACE/heraj-doc config user.email "$(git config user.email)"
  git -C $BUILD_WORKSPACE/heraj-doc config user.name "$(git config user.name)"
  
  jekyll build -s $PROJECT_HOME/assembly/doc/gh-pages -d $BUILD_WORKSPACE/heraj-doc
  $PROJECT_HOME/gradlew build
  $PROJECT_HOME/gradlew alljavadoc alljacoco
  rm -rf $BUILD_WORKSPACE/heraj-doc/javadoc $BUILD_WORKSPACE/heraj-doc/coverage
  mv $BUILD_WORKSPACE/docs/javadoc $BUILD_WORKSPACE/heraj-doc/javadoc
  mv $BUILD_WORKSPACE/reports/jacoco/alljacoco/html $BUILD_WORKSPACE/heraj-doc/coverage
}
function execute-pack() {
  local -r dest="$PROJECT_HOME/pack"

  rm -rf "$dest" > /dev/null
  mkdir -p "$dest"

  execute-gradle
  local -r jar_files=$(find core client -name "heraj*.jar")

  for file in ${jar_files[@]}; do
    cp "$file" "$dest"
  done

  cd "$dest"
  tar -zcvf "heraj-$VERSION.tar.gz" heraj*.jar
  zip "heraj-$VERSION.zip" heraj*.jar
  rm "$dest"/*.jar

  echo -e "\nPacked files have been generated in $dest"
}
function execute-fat() {
  local -r gradle_fat_postfix=$(grep "def shadowPostFix" < "$PROJECT_HOME/build.gradle" | awk '{print $4}' | tr -d "'")
  local -r dest="$PROJECT_HOME/fat"

  rm -rf "$dest" > /dev/null
  mkdir -p "$dest"

  $PROJECT_HOME/gradlew clean build shadowJar
  for lib in "${LIB_CORES[@]}"; do
    local file=$(find . -name "$PROJECT_PREFIX-$lib-$gradle_fat_postfix*")
    cp "$file" "$dest/$PROJECT_PREFIX-$lib-$VERSION-$FAT_POSTFIX-.jar"

    if [ $lib = $LIB_ALL ]; then
      cp "$file" "$dest/$PROJECT_PREFIX-$VERSION-$ALL_POSTFIX.jar"
    fi
  done

  echo -e "\nFat files have been generated in $dest"
}

if [ -z "$VERSION" ]; then
  echo "The version not detected. Check build script or environment."
  exit 1
fi

if [ 0 == $# ]; then
  clean-workspace
  execute-gradle
else
  while (( $# )); do
    case $1 in
      "clean")
        clean-workspace
        ;;
      "gradle")
        execute-gradle
        ;;
      "install")
        execute-install
        ;;
      "deploy")
        execute-deploy
        ;;
      "test")
        execute-test
        ;;
      "it")
        execute-integration-test
        ;;
      "docs")
        execute-documentation
        ;;
      "pack")
        execute-pack
        ;;
      "fat")
        execute-fat
        ;;
      *)
        print-usage
        ;;
    esac
    [[ $? != 0 ]] && exit 1
    shift
  done
fi
