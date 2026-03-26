#!/bin/bash

#
# Bump the version for release
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
# Extract the current version
#
# $version = "1.54.3"
#
function extract_version {
  version=`cat version.txt`
}

#
# Update the current version
#
function write_version {
  echo "Updating version to ${new_version}"
  echo "${new_version}" > version.txt
}

extract_version
bump $1 ${version}
write_version
