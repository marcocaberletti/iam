#!/bin/bash

set -xe

VERSION="0.6.0"

war_dest_dir="debian/var/lib/indigo/iam-login-service/"
warfile="iam-login-service/target/iam-login-service.war"

if [ ! -f ${warfile} ]; then
  mvn clean package -U
fi

mkdir -p ${war_dest_dir}
cp ${warfile} ${war_dest_dir}

dpkg --build debian

mv debian.deb iam-login-service-${VERSION}_all.deb
