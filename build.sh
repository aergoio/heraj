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
VERSION=$(grep ' version ' $PROJECT_HOME/build.gradle | cut -d"'" -f2)
export BUILD_WORKSPACE=$PROJECT_HOME/build

if [ -z "$VERSION" ]; then
  echo "The version not detected. Check build script or environment."
  exit 1
fi

function print-usage() {
  echo "build.sh [command]"
  echo "Commands: "
  echo "  clean       delete and reset intermediate obj in build"
  echo "  protobuf    generate code from protobuf declaration"
  echo "  gradle      compile source to executable using gradle"
  echo "  test        test built executable"
}

function update-protobuf() {
  PROTO_TARGET=$PROJECT_HOME/core/transport/src/main/proto
  git clone https://github.com/aergoio/aergo.git $BUILD_WORKSPACE/aergo
  rm -rf $PROTO_TARGET
  mkdir -p $PROTO_TARGET

  pushd $BUILD_WORKSPACE/aergo/types
  tar -cf - `find . -name "*.proto" -print` | ( cd $PROTO_TARGET && tar xBf - )
  popd
}
function clean-workspace() {
  $PROJECT_HOME/gradlew clean
  rm -rf $BUILD_WORKSPACE
}
function execute-gradle() {
  $PROJECT_HOME/gradlew build
}
function execute-test() {
  $PROJECT_HOME/gradlew test jmh jacocoTestReport
}
function execute-documentation() {
  gem install bundler
  bundle install --gemfile $PROJECT_HOME/assembly/doc/gh-pages/Gemfile
  git clone $(git remote get-url origin) $BUILD_WORKSPACE/heraj-doc -b gh-pages
  git -C $BUILD_WORKSPACE/heraj-doc config user.email $(git config user.email)
  git -C $BUILD_WORKSPACE/heraj-doc config user.name $(git config user.name)
  
  jekyll build -s $PROJECT_HOME/assembly/doc/gh-pages -d $BUILD_WORKSPACE/heraj-doc
  $PROJECT_HOME/gradlew build
  $PROJECT_HOME/gradlew alljavadoc alljacoco
  rm -rf $BUILD_WORKSPACE/heraj-doc/javadoc $BUILD_WORKSPACE/heraj-doc/coverage
  mv $BUILD_WORKSPACE/docs/javadoc $BUILD_WORKSPACE/heraj-doc/javadoc
  mv $BUILD_WORKSPACE/reports/jacoco/alljacoco/html $BUILD_WORKSPACE/heraj-doc/coverage
}

function execute-assemble() {
  $PROJECT_HOME/gradlew assemble
  cd $PROJECT_HOME/assembly/build/distributions && tar -xvf hera-0.1-SNAPSHOT.tar && cd -
}


if [ 0 == $# ]; then
  clean-workspace
  update-protobuf
  execute-gradle
else
  while (( $# )); do
    case $1 in
      "clean")
	clean-workspace
        ;;
      "protobuf")
        update-protobuf
        ;;
      "gradle")
        execute-gradle
        ;;
      "test")
        execute-test
        ;;
      "docs")
        execute-documentation
        ;;
      "assemble")
        execute-assemble
        ;;
      *)
        print-usage
	;;
    esac
    shift
  done
fi
