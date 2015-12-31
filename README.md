# Malalaoshi

### Getting Started

1. Fork this repository with Permissions `Inherit repository user/group permissions`.
2. Follow [this link](https://confluence.atlassian.com/bitbucket/add-an-ssh-key-to-an-account-302811853.html) to add an SSH key to your bitbucket account.
3. Utilize `git clone` to download forked repo to local machine

### To Create a Pull Request

1. Do needed modifications
2. Utilize `git add` to add modified files
3. `git commit -m 'Your commit messages.'`
4. `git push -u origin dev`
5. On bitbucket, create a pull request

### To Synchronize with Upstream

Your forked repo auto synchronizes with upstream, so you just need `git pull origin dev`.

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
