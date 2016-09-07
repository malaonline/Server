# Malalaoshi ![Building status](https://travis-ci.org/malaonline/Server.svg?branch=dev)

### Getting Started

1. Add an SSH key to your Github account.
2. `git clone` to local machine.

### To Create a Pull Request

0. `git checkout -b BRANCH-NAME`. BRANCH-NAME should match regular expression `(AN|IOS|SERVER|WEC|BE|PM|OW|TWEB)-\d+`.
1. Do modifications
2. Read [git add](https://confluence.atlassian.com/bitbucket/add-an-ssh-key-to-an-account-302811853.html) to add modified files
3. `git commit -m 'Your commit messages.'`
4. `git push -u origin BRANCH-NAME`
5. Create a pull request

### Dev server

<https://dev.malalaoshi.com/admin>

username: test

password: mala-test

### For Android

1. `git submodule update --init --recursive`
2. Create a file named `gradle.properties` with following content:

```
KEYSTORE_FILE_PATH=/Users/.../keystore
KEYSTORE_PASSWORD=123456
KEY_PASSWORD=123456
DEV_SERVER=https://dev.malalaoshi.com
STAGE_SERVER=https://stage.malalaoshi.com
PRD_SERVER=https://malalaoshi.com
PARENT_JPUSH_KEY_DEBUG=xxxxxxxxxx
PARENT_JPUSH_KEY_RELEASE=xxxxxxxxxxxxx
```
Modify variables if needed.

### For Server

Install less compiler:

```
npm install -g less
```
