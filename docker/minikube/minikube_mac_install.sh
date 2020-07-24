#!/bin/bash

which -s brew

if [ $? != 0 ]
then
	echo " ========================"
	echo "| Installing Homebrew... |"
	echo " ========================"

	/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"

else
	echo " ========================"
	echo "| Updating Homebrew...   |"
	echo " ========================"

	brew update
fi

which -s kubectl

if [ $? != 0 ]
then
	echo " ========================"
	echo "| Installing Kubectl...  |"
	echo " ========================"

	brew install kubernetes-cli

	if [ $? == 0 ] ; then
		echo
		echo " ++++++++++++++++++++++++++++++++"
		echo "| Kubectl installed successfully |"
		echo " ++++++++++++++++++++++++++++++++"
	fi
fi

echo
echo " ++++++++++++++++++++++++++++++++"
echo "| Installing VirtualBox |"
echo " ++++++++++++++++++++++++++++++++"

brew cask install --force virtualbox

echo
echo " ++++++++++++++++++++++++++++++++"
echo "| Installing Minikube |"
echo " ++++++++++++++++++++++++++++++++"
brew cask install minikube

echo
echo " ++++++++++++++++++++++++++++++++"
echo "| Minikube Installed|"
echo " ++++++++++++++++++++++++++++++++"