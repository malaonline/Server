# Malalaoshi ![Building status](https://travis-ci.org/malaonline/Server.svg?branch=master) [![Coverage Status](https://coveralls.io/repos/github/malaonline/Server/badge.svg?branch=master)](https://coveralls.io/github/malaonline/Server?branch=master)

### Getting Started

0. Install less compiler: `npm install -g less`
1. Install Python 3 and Postgres.
2. Add an SSH key to your Github account.
3. `git clone` to local machine.
4. `cd server`
5. `cp server/local_settings.sample server/local_settings.py`
6. `pip install -r pip_install.txt --upgrade`
7. `python manage.py migrate`
8. `python manage.py runserver`

### To Create a Pull Request

0. `git checkout -b BRANCH-NAME`. BRANCH-NAME should match regular expression `(AN|IOS|SERVER|WEC|BE|PM|OW|TWEB)-\d+`.
1. Do modifications
2. Read [git add](https://confluence.atlassian.com/bitbucket/add-an-ssh-key-to-an-account-302811853.html) to add modified files
3. `git commit -m 'Your commit messages.'`
4. `git push -u origin BRANCH-NAME`
5. Create a pull request

### Web Servers

- Dev server <https://dev.malalaoshi.com>
- Stage server <https://stage.malalaoshi.com>
- Prd server <https://www.malalaoshi.com>
