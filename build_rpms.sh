#!/bin/bash

warfile="iam-login-service/target/iam-login-service.war"

if [ ! -f ${warfile} ]; then
	mvn -U clean package
fi

cp iam-login-service/target/iam-login-service.war rpm/SOURCES/

rpmbuild --buildroot=rpm/ --define "_basedir `pwd`" -bb rpm/SPECS/iam-login-service.spec

