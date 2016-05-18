## API 

### Subject List

```
GET /api/v1/subjects
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
GET /api/v1/grades
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
GET /api/v1/tags

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
GET /api/v1/teachers
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
    "next": "http://127.0.0.1:8000/api/v1/teachers?page=2",
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
            "subject": "语文",
            "grades_shortname": "小初"
            "tags": ["幽默", "亲切"],
        },
        ...
    ]
}
```

### Teacher Instance

```
GET /api/v1/teachers/{teacher id}
```

```
{
    "id": 1,
    "avatar": "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/avatars/DSC_2134_l27BkVs.jpg?X-Amz-Expires=3600&X-Amz-Signature=156e575392ae9089afc1ec135bffd67fc3d8a152d5af2cd41815743ca31aeb53&X-Amz-SignedHeaders=host&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAP22CWKUZDOMHLFGA%2F20151211%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-Date=20151211T022237Z"
    "gender": "m",
    "name": "lll",
    "degree": "s",
    "teaching_age": 0,
    "level": "麻辣合伙人",
    "subject": "语文",
    "grades": ["小学", "初一"],
    "tags": ["幽默", "亲切"]
    "photo_set": ["https://s3.cn-north-1.amazonaws.com.cn/dev-upload/avatars/DSC_2134_l27BkVs.jpg",
                "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/avatars/DSC_2134_l27BkVs.jpg",
                "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/avatars/DSC_2134_l27BkVs.jpg"],
    "achievement_set": [
        {
            "title": "特级教师",
            "img": "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/avatars/DSC_2134_l27BkVs.jpg"
        },
        {
            "title": "全国奥数总冠军",
            "img": "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/avatars/DSC_2134_l27BkVs.jpg"
        }
    ],
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
            "grade": {
                "id": 1,
                "name": "小学"
            },
            "price": 89
        },
        {
            "grade": {
                "id": 8,
                "name": "初中"
            },
            "price": 99
        }
    ],
    "min_price": 90,
    "max_price": 200
}
```

### School list

```
GET /api/v1/schools
```

parameters:
```
region=266
```

```
{
    "count": 4,
    "next": null,
    "previous": null,
    "results": [
        {
            "id": 8,
            "name": "洛阳社区三店",
            "address": "南京路121号",
            "thumbnail": "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/schools/img3_nv5pGLS.jpg?X-Amz-Signature=4322ee92a99c41c37e758de711fd4f5c6c70a6154d3490f4db673ac319eee377&X-Amz-Expires=3600&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIAP22CWKUZDOMHLFGA%2F20160105%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-Date=20160105T022337Z",
            "region": 266,
            "center": false,
            "longitude": 710,
            "latitude": 820
        },
        {
            "id": 7,
            "name": "洛阳社区二店",
            "address": "南京路89号",
            "thumbnail": "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/schools/img2_63QAijp.jpg?X-Amz-Signature=9b1c00ac36470dad42f4284fc3da3c3da9791263be05a9fa8e61d2debfd5fc19&X-Amz-Expires=3600&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIAP22CWKUZDOMHLFGA%2F20160105%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-Date=20160105T022337Z",
            "region": 266,
            "center": false,
            "longitude": 990,
            "latitude": 980
        },
        ...
    ]
}
```

### Member service list

```
GET /api/v1/memberservices
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
code=1234
```

result:

```
{
    "verified": "true",
    "first_login": "true",
    "token": "189841301....7438741938",
    "parent_id": 51,
    "user_id": 231,
    "profile_id": 333
}
```

```
{
    "verified": "false",
    "reason": "SMS not match"
}
```

### Save child name

```
PATCH /api/v1/parents/(\d+)
```

header data:

```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

```
content_type: application/json
```

body data:

```
{
    student_name=XYZ
}
```

result:

```
{
    "done": "true"
}
```

### Save child school name

```
PATCH /api/v1/parents/(\d+)
```

header data:

```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

```
content_type: application/json
```

body data:

```
{
    student_school_name=XYZ
}
```

result:

```
{
    "done": "true"
}
```

### Save user profile avatar

```
PATCH /api/v1/profiles/{profile_id}
```

header data:

```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

```
content_type: multipart/form-data
```

body data:

```
avatar=image_file
```

result:

```
{
    "done": "true"
}
```

If user role is not correct, return HTTP code 409


### Get user policy

```
GET /api/v1/policy
```

```
{
    "result": "<html>abc...nhnhsh</html>",
    "updated_at": 13450887
}
```


### Get Token
```
POST /api/v1/token-auth
```

parameters:

```
username=username
password=password
```

result:

```
{
    "token":"f8f4a20ce8b6c6e74bb6542933ed79242e9f0658"
}
```

Above token's value is just a example not actually value.


### Get teacher's available time

```
GET /api/v1/teachers/TEACHER_ID/weeklytimeslots
```

parameters:
```
school_id=1
```

header data:

```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

result:
```
{
    "1": [
    {
        "id": 1,
        "start": "08:00",
        "end": "10:00",
        "available": true
    },
    {
        "id": 2,
        "start": "10:10",
        "end": "12:10",
        "available": false
    },
    ...
    ],
    "2": [{
        "id": 6,
        "start": "08:00",
        "end": "10:00",
        "available": true
    },
    ...
    ],
    ...
}
```

### Get Coupon List

```
GET /api/v1/coupons
```

header data:

```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

parameters:
```
only_valid=true
```

When `only_valid` is `true`, only return valid coupons


result:
```
{
    "count": 2,
    "next": null,
    "previous": null,
    "results": [
        {
            "id": 1,
            "name": "新生奖学金",
            "amount": 120,
            "expired_at": 1453343547,
            "used": false
        },
        {
            "id": 2,
            "name": "优惠奖学金",
            "amount": 100,
            "expired_at": 1453343547,
            "used": true
        }
    ]
}
```

### Concrete Time Slot

```
GET /api/v1/concrete/timeslots
```

header data:
```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```


parameters:
```
hours=14
weekly_time_slots=3+8
teacher=1
```

result:
```
{
    "data": [
        [
            123456890,
            123459890
        ],
        [
            143456890,
            143459890
        ],
        ...
    ]
}
```

### Create Order

```
POST /api/v1/orders
```

header data:

```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

data:
```
{
    'teacher': 1,
    'school': 1,
    'grade': 1,
    'subject': 1,
    'coupon': 2,
    'hours': 14,
    'weekly_time_slots': [3, 8]
}
```

result:

(Success)HTTP Status Code 201
```
{
    'teacher': 1,
    'order_id': '1234123412341234',
    'parent': 1,
    'grade': 1,
    'id': 15,
    'total': 360,
    'price': 180,
    'hours': 14,
    'coupon': None,
    'status': 'u',
    'weekly_time_slots': [3, 8],
    'subject': 1,
    'school': 1
}
```

(Fail)HTTP Status Code 200
(One or more course in the order has been assigned to other parent)
```
{
    'ok': false,
    'code': -1
}
```

### Cancel Order

```
DELETE /api/v1/orders/1
```

header data:
```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

result:
```
{
    "ok": true
}
```

### Get student course table

```
GET /api/v1/timeslots
```

header data:
```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

data:
```
{
    "count": 14,
    "next": "http://127.0.0.1:8000/api/v1/timeslots?page=2",
    "previous": null,
    "results": [
        {
            "id": 1,
            "end": 1234567890,
            "subject": "数学",
            "is_passed": false
        },
        ...
    ]
}
```

### Get one course

```
GET /api/v1/timeslots/1
```

header data:
```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

data:
```
{
    "id": 6,
    "start": 12341234123,
    "end": 1457883000,
    "subject": "语文",
    "school": "洛阳社区三店",
    "is_passed": false,
    "teacher": {
        "id": 20,
        "avatar": "http://127.0.0.1:8000/upload/avatars/img3_8LmhD21.jpg",
        "name": "李老师"
    },
    "comment": null
}
```
or
```
{
    "id": 6,
    "start": 12341234123,
    "end": 1457883000,
    "subject": "语文",
    "school": "洛阳社区三店",
    "is_passed": false,
    "teacher": {
        "id": 20,
        "avatar": "http://127.0.0.1:8000/upload/avatars/img3_8LmhD21.jpg",
        "name": "李老师"
    },
    "comment": {
        "id": 1,
        "timeslot": 6,
        "score": 4,
        "content": "rt"
    }
}
```

### Create Comment

```
POST /api/v1/comments
```

header data:

```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

data:
```
{
    'timeslot': 12,
    'score': 4,
    'content': 'Good.'
}
```

result:

HTTP Status Code 201

```
{
    "id": 4,
    "timeslot": 12,
    "score": 4,
    "content": "Good."
}
```

### Get Comment

```
GET /api/v1/comments/123
```

result:
```
{
    "id": 1,
    "timeslot": 6,
    "score": 4,
    "content": "rt"
}
```


### Get Charge Token

```
PATCH /api/v1/orders/123
```

header data:

```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

data:
```
{
    "action": "pay",
    "channel": "alipay"
}
```

result:

(Success)
```
{
  "id": "ch_Hm5uTSifDOuTy9iLeLPSurrD",
  "object": "charge",
  "created": 1425095528,
  "livemode": true,
  "paid": false,
  "refunded": false,
  "app": "app_1Gqj58ynP0mHeX1q",
  "channel": "alipay",
  "order_no": "123456789",
  "client_ip": "127.0.0.1",
  "amount": 100,
  "amount_settle": 0,
  "currency": "cny",
  "subject": "Your Subject",
  "body": "Your Body",
  "extra":{},
  "time_paid": null,
  "time_expire": 1425181928,
  "time_settle": null,
  "transaction_no": null,
  "refunds": {
    "object": "list",
    "url": "/v1/charges/ch_Hm5uTSifDOuTy9iLeLPSurrD/refunds",
    "has_more": false,
    "data": []
  },
  "amount_refunded": 0,
  "failure_code": null,
  "failure_msg": null,
  "credential": {
    "object": "credential",
    "alipay":{
      "orderInfo": "_input_charset=\"utf-8\"&body=\"tsttest\"&it_b_pay=\"1440m\"¬ify_url=\"https%3A%2F%2Fapi.pingxx.com%2Fnotify%2Fcharges%2Fch_jH8uD0aLyzHG9Oiz5OKOeHu9\"&out_trade_no=\"1234dsf7uyttbj\"&partner=\"2008451959385940\"&payment_type=\"1\"&seller_id=\"2008451959385940\"&service=\"mobile.securitypay.pay\"&subject=\"test\"&total_fee=\"1.23\"&sign=\"dkxTeVhMMHV2dlRPNWl6WHI5cm56THVI\"&sign_type=\"RSA\""
    }
  },
  "description": null
}
```
(Fail)

(One or more course in the order has been assigned to other parent)
```
{
    'ok': false,
    'code': -1
}
```
### Get order list

```
GET /api/v1/orders
```

header data:
```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

result:
```
{
    "count": 2,
    "next": null,
    "previous": null,
    "results": [
        {
            "id": 31,
            "teacher": 1,
            "teacher_name": "赵老师",
            "teacher_avatar": "http://127.0.0.1:8000/upload/avatars/img0_2uPu2TK.jpg",
            "school": "洛阳中心店",
            "grade": "初一",
            "subject": "化学",
            "hours": 6,
            "status": "u",
            "order_id": "1216664871711940",
            "to_pay": 1140,
            "evaluated": true
        },
        {
            "id": 32,
            "teacher": 4,
            "teacher_name": "李老师",
            "teacher_avatar": "http://127.0.0.1:8000/upload/avatars/img3_6bygJpA.jpg",
            "school": "洛阳中心店",
            "grade": "一年级",
            "subject": "语文",
            "hours": 4,
            "status": "p",
            "order_id": "1216670256607750",
            "to_pay": 840,
            "evaluated": true
        }
    ]
}
```

### Get order info

```
GET /api/v1/orders/123
```

header data:
```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

result:
```
{
    "id": 32,
    "teacher": 4,
    "teacher_name": "李老师",
    "teacher_avatar": "http://127.0.0.1:8000/upload/avatars/img3_6bygJpA.jpg",
    "school": "洛阳中心店",
    "grade": "一年级",
    "subject": "语文",
    "hours": 4,
    "status": "u",
    "order_id": "1216670256607750",
    "to_pay": 840,
    "created_at": 1462528779,
    "paid_at": null,
    "charge_channel": null,
    "evaluated": true,
    "is_timeslot_allocated": false,
    "timeslots": [
        [
            1464134400,
            1464141600
        ],
        [
            1464220800,
            1464228000
        ],
        [
            1464307200,
            1464314400
        ]
    ]
}
```
```
For get order info after pay
```
First, if `status` is `d`, should notify user order has been canceled.

If `status` is `p` but `is_timeslot_allocated` is `false`, should notify user courses have been preempted.

Both `status` is `p` and `is_timeslot_allocated` is `true` indicate success.

If no `timeslots`, the `timeslots` will be `[]`
```
For get order info from order list
```
`paid_at` and `charge_channel` only available on the order which is paid(`status` is `p`)
### Get if user has been evaluated for this subject

```
GET /api/v1/subject/(?P<subject_id>\d+)/record
```

header data:
```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

result:
```
{
    "evaluated": true
}
```

### Get user unpaid orders count

```
GET /api/v1/unpaid_count
```

header data:
```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

result:
```
{
    "count": 1
}
```
