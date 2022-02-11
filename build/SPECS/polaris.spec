Name:		    polaris
Version:        0.0.1
Release:        1
Summary:        Container Scheduler for RACTF

License:        AGPLv3
URL:            https://ractf.co.uk
Source0:	    polaris-%{version}.tar.gz
ExclusiveArch:	x86_64
BuildRoot:	    %{_tmppath}/%{name}-buildroot

%define _build_id_links none

%description
Container Scheduler for RACTF

%prep
%setup -q

%install
mkdir -p "$RPM_BUILD_ROOT"
cp -R * "$RPM_BUILD_ROOT"

%clean
rm -rf %{buildroot}

%files
%defattr(-,root,root,-)
/usr/bin/polaris
/usr/lib/systemd/system/polaris.service
%config(noreplace) /etc/polaris.toml
