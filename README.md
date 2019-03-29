# ofsted-forms-frontend

This is service is not complete. The project has been shelved until further instruction.

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

Script assume shell current working directory is `eve` directory form project root
```bash
pipenv install
```

#### Running eve server

Run this in `eve` directory
```bash
pipenv run python main.py
```

#### Add form document

```bash
curl -X POST http://localhost:5000/forms -H 'Content-Type: application/json' -d'{ "formId" : "d929102d-6db3-494a-a789-f955d4b2b034", "data" : {}}
```

expected output - formatted using [jq]

```json
{
  "_updated": "Fri, 22 Mar 2019 10:48:57 GMT",
  "_created": "Fri, 22 Mar 2019 10:48:57 GMT",
  "_etag": "6dd99c8de8b8a18518a62ab9dd07b1e4b2dfc015",
  "_id": "5c94bd999dc6d61166a23383",
  "_status": "OK"
}
```

#### Get form document

```bash 
curl http://localhost:5000/forms/5c94bd999dc6d61166a23383
```

expected output - formatted using [jq]

```json
{
  "_id": "5c94bd999dc6d61166a23383",
  "formId": "d929102d-6db3-494a-a789-f955d4b2b034",
  "data": {},
  "_updated": "Fri, 22 Mar 2019 10:48:57 GMT",
  "_created": "Fri, 22 Mar 2019 10:48:57 GMT",
  "_etag": "6dd99c8de8b8a18518a62ab9dd07b1e4b2dfc015"
}
```

#### Update form document

`If-Match` header need to have same value as `Etag` for requested document

```
curl http://localhost:5000/forms/5c94bd999dc6d61166a23383 -X PUT -H 'If-Match: 6dd99c8de8b8a18518a62ab9dd07b1e4b2dfc015' -H 'Content-Type: application/json' -d'{ "formId": "d929102d-6db3-494a-a789-f955d4b2b034", "data" : {"fizz": "buzz"}}'
```

expected output - formatted using [jq]

```json
{
  "_updated": "Fri, 22 Mar 2019 10:50:58 GMT",
  "_created": "Fri, 22 Mar 2019 10:48:57 GMT",
  "_id": "5c94bd999dc6d61166a23383",
  "_etag": "75bd0bbb4cbf5a07ed35b3c2412f4bd7b76da221",
  "_status": "OK"
}
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

[jq]: https://stedolan.github.io/jq/