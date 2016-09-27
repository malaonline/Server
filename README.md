# Malalaoshi ![Building status](https://travis-ci.org/malaonline/Server.svg?branch=master)

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

### Dependencies

Install less compiler:

```
npm install -g less
```

### Web Servers

- Dev server <https://dev.malalaoshi.com>
- Stage server <https://stage.malalaoshi.com>
- Prd server <https://www.malalaoshi.com>
