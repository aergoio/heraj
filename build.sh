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
readonly VERSION=$(grep " version '" "$PROJECT_HOME/build.gradle" | cut -d"'" -f2)
readonly BUILD_WORKSPACE=$PROJECT_HOME/build
readonly PROJECT_PREFIX="heraj"
readonly LIB_CORES=("wallet" "smart-contract" "transport")

function print-usage() {
  echo "build.sh [command]"
  echo "Commands: "
  echo "  clean       delete and reset intermediate obj in build"
  echo "  gradle      compile source to executable using gradle"
  echo "  install     install to maven local"
  echo "  deploy      upload to jcenter"
  echo "  test        test built executable"
  echo "  docs        generate documents"
  echo "  pack        pack jar into single fat jar file"
}

function clean-workspace() {
  rm -rf $BUILD_WORKSPACE
  $PROJECT_HOME/gradlew clean
}
function execute-gradle() {
  $PROJECT_HOME/gradlew clean build test alljacoco
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
  local -r fat_postfix=$(grep "def shadowPostFix" < "$PROJECT_HOME/build.gradle" | awk '{print $4}' | tr -d "'")
  local -r dest="$PROJECT_HOME/fat"

  rm -rf "$dest" > /dev/null
  mkdir -p "$dest"

  $PROJECT_HOME/gradlew clean build shadowJar
  for lib in "${LIB_CORES[@]}"; do
    local file=$(find . -name "$PROJECT_PREFIX-$lib-$fat_postfix*")
    cp "$file" "$dest"
  done
  echo "  Fat files have been copied to $dest"
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
      "docs")
        execute-documentation
        ;;
      "pack")
        execute-pack
        ;;
      *)
        print-usage
        ;;
    esac
    [[ $? != 0 ]] && exit 1
    shift
  done
fi
