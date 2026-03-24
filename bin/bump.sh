#!/bin/bash

#
# Update the artifact version in pom.xml
# Script arguments:
# $1 = mode (major, minor, bugfix)
#

#
# Takes a version number, and the mode to bump it, and increments/resets
# the proper components so that the result is placed in the variable `new_version`.
#
# $1 = mode (major, minor, bugfix)
# $2 = version (x.y.z)
#
function bump {
  local mode="$1"
  local old="$2"
  # find out the three components of the current version
  local parts=( ${old//./ } )
  # now bump it up based on the mode
  case "$1" in
    major)
      local bv=$((parts[0] + 1))
      new_version="${bv}.0.0"
      ;;
    minor)
      local bv=$((parts[1] + 1))
      new_version="${parts[0]}.${bv}.0"
      ;;
    bugfix)
      local bv=$((parts[2] + 1))
      new_version="${parts[0]}.${parts[1]}.${bv}"
      ;;
  esac
}

#
# Read the existing project name and version number from pom.xml and populate the following variables:
#
# $version = "1.54.3"
#
function pull_values {
  local fline=`head -1 ${pom}`
  version=`cat pom.xml | grep "<version>.*</version>" | head -1 | awk -F'[><]' '{print $3}'`
}

#
# Update the pom.xml file with with maven
#
function update_pom {
  mvn versions:commit -DnewVersion="${new_version}"
}

# Set up the defaults for the script
pom="pom.xml"

pull_values
bump $1 ${version}
update_pom
