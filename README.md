
# ofsted-forms-frontend

This is a placeholder README.md for a new repository

### Login

#### Prerequisits

Start whole stack by service-manager

```
sm --start AUTH_LOGIN_API AUTH_LOGIN_STUB AUTH USER_DETAILS ASSETS_FRONTEND IDENTITY_VERIFICATION -f
```

#### Login actions

To make login enter [auth-login-stub](http://localhost:9949/auth-login-stub/gg-sign-in) providing good redirect link in auth-login-stub form

### Ofsted mongo connector

Mongo is exposed through [eve](http://docs.python-eve.org/en/latest/) REST framework.

#### Instalation of eve
Install `pipenv` tool - [instlation instructuion](https://pipenv.readthedocs.io/en/latest/install/#installing-pipenv)

Script assume shell current working directory is in project root
```bash
cd eve
pipenv install
```

#### Running eve server

Run this in `eve` directory
```bash
pipenv run python main.py
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
