%define topdir          %{_basedir}/rpm
%define buildroot	    %{topdir}/build-rpm-root
%define _rpmdir         %{topdir}/RPMS

%define name            iam-login-service
%define warversion      0.6.0
%define user            iam

%define jdk_version     1.8.0
%define mvn_version     3.3.0

Name:		%{name}
Version:	%{warversion}
Release:	1%{?dist}
Summary:	INDIGO Identity and Access Management Service.

Group:		Applications/Web
License:	apache2
URL:		https://github.com/indigo-dc/iam

BuildArch: noarch
BuildRequires: java-%{jdk_version}-openjdk-devel
#BuildRequires: maven >= %{mvn_version}

Requires:	java-%{jdk_version}-openjdk

%description
The INDIGO IAM (Identity and Access Management service) provides 
user identity and policy information to services so that consistent 
authorization decisions can be enforced across distributed services.

%prep

%build
#cd %{_basedir}
#mvn -U clean package
#cp %{_basedir}/%{name}/target/%{name}.war %{topdir}/SOURCES

%install
mkdir -p %{buildroot}/var/lib/indigo/%{name}
mkdir -p %{buildroot}/usr/lib/systemd/system
mkdir -p %{buildroot}/etc/sysconfig
cp %{topdir}/SOURCES/%{name}.war %{buildroot}/var/lib/indigo/%{name}
cp %{topdir}/SOURCES/%{name}.service %{buildroot}/usr/lib/systemd/system
cp %{topdir}/SOURCES/%{name} %{buildroot}/etc/sysconfig

%clean

%pre

%post
/usr/bin/id -u %{user} > /dev/null 2>&1
if [ $? -eq 1 ]; then
  useradd --comment "INDIGO IAM" --system --user-group --home-dir /var/lib/indigo/%{name} --no-create-home --shell /sbin/nologin %{user}
fi
chown -R %{user}:%{user} /var/lib/indigo/%{name}
systemctl daemon-reload

%preun

%postun
systemctl daemon-reload

%files
%config(noreplace) /etc/sysconfig/iam-login-service
%dir /var/lib/indigo
%dir /var/lib/indigo/%{name}
/var/lib/indigo/%{name}/%{name}.war
/usr/lib/systemd/system/%{name}.service

%changelog
* Thu Apr 27 2017 Marco Caberletti <marco.caberletti@cnaf.infn.it> 0.6.0
- Initial IAM Login Service for Indigo 2.

