# Malalaoshi

### Getting Started

1. Add an SSH key to your bitbucket account.
2. `git clone` to local machine.

### To Create a Pull Request

0. `git checkout -b BRANCH-NAME`. BRANCH-NAME should match regular expression `(AN|IOS|SERVER|WEC|BE|PM|OW|TWEB)-\d+`.
1. Do modifications
2. Utilize `git add` to add modified files
3. `git commit -m 'Your commit messages.'`
4. `git push -u origin BRANCH-NAME`
5. On bitbucket website (or with bitbucket cli), create a pull request

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
LOCAL_SERVER=https://dev.malalaoshi.com
```
Modify variables if needed.
