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
    "next": "http://127.0.0.1:8000/api/v1/grades/?page=2",
    "previous": null,
    "results": [
        {
            "id": 1,
            "name": "小学",
            "superset": null,
            "leaf": false,
            "subjects": [
                1,
                2,
                3
            ]
        },
        {
            "id": 2,
            "name": "一年级",
            "superset": 1,
            "leaf": true,
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
