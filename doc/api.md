## API 

### Subject List

```
GET /api/v1/subjects/
```

```
{
    "count": 9,
    "next": null,
    "previous": null,
    "results": [
        {
            "id": 1,
            "name": "语文"
        },
        {
            "id": 2,
            "name": "数学"
        },
        ...
    ]
}
```

### Grade List

```
GET /api/v1/grades/
```

```

{
    "count": 16,
    "next": null,
    "previous": null,
    "results": [
        {
            "id": 1,
            "name": "小学",
            "subset": [
                {
                    "id": 2,
                    "name": "一年级",
                    "subset": [],
                    "subjects": [
                        1,
                        2,
                        3
                    ]
                },
                ...
            ],
            "subjects": [
                1,
                2,
                3
            ]
        },
        ...
    ]
}
```

### Tag List

```
GET /api/v1/tags/

```

```
{
    "count": 16,
    "next": null,
    "previous": null,
    "results": [
        {
            "id": 1,
            "name": "幽默"
        },
        ...
    ]
}

```

### Teacher list

```
GET /api/v1/teachers/
```

parameters:

```
grade=3
subject=4
tags=1+3+2
```

```
{
    "count": 2,
    "next": null,
    "previous": null,
    "results": [
        {
            "id": 1,
            "avatar": "https:/stnhsh.sths.....",
            "gender": "m",
            "name": "lll",
            "degree": "s",
            "min_price": 90,
            "max_price": 200,
            "subject": 1,
            "grades": [1, 2, 3],
            "tags": [1, 3]
        },
        ...
    ]
}
```

### Teacher Instance

```
GET /api/v1/teachers/{teacher id}/
```

```
{
    "id": 1,
    "avatar": "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/avatars/DSC_2134_l27BkVs.jpg?X-Amz-Expires=3600&X-Amz-Signature=156e575392ae9089afc1ec135bffd67fc3d8a152d5af2cd41815743ca31aeb53&X-Amz-SignedHeaders=host&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAP22CWKUZDOMHLFGA%2F20151211%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-Date=20151211T022237Z"
    "gender": "m",
    "name": "lll",
    "degree": "s",
    "teaching_age": 0,
    "level": {
        "id": 3,
        "name": "麻辣合伙人"
    },
    "subject": 1,
    "grades": [1, 2, 3],
    "tags": [1, 3],
    "highscore_set": [
        {
            "name": "123",
            "increased_scores": 100,
            "school_name": "洛阳一中",
            "admitted_to": "北京大学",
        },
        ...
    ],
    "prices": [
        {
            "grade": 1,
            "price": 200
        },
        {
            "grade": 2,
            "price": 300
        },
        ...
    ]
}
```


### Member service list

```
GET /api/v1/memberservices/
```

```
{
    "count": 2,
    "next": null,
    "previous": null,
    "results": [
        {
            "id": 1,
            "name": "11",
            "detail": "bla bla...",
            "enbaled": "true"
        },
        ...
    ]
}

```

### Sending SMS

```
POST /api/v1/sms
```

parameters:

```
action=send
phone=150123456
```

result:

```
{
    "sent": "true"
}
```

```
{
    "sent": "false",
    "reason": "Exceed max retry.'
}
```

### Verifying SMS

```
POST /api/v1/sms
```

parameters:

```
action=verify
phone=150123456
```

result:

```
{
    "verified": "true",
    "first_login": "true",
    "token": "189841301....7438741938"
}
```

```
{
    "verified": "false",
    "reason": "SMS not match'
}
```

