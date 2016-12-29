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
region=1450
school=30
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
            "level": 2,
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
    "level": 6,
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
    "max_price": 200,
    "published": true,
    "favorite": false
}
```

### Region list

```
GET /api/v1/regions
```

```
{
    "count": 3,
    "next": null,
    "previous": null,
    "results": [
        {
            "id": 1281,
            "name": "许昌市"
        },
        {
            "id": 1311,
            "name": "洛阳市"
        },
        {
            "id": 1450,
            "name": "郑州市"
        }
    ]
}
```

### School list

```
GET /api/v1/schools
```

parameters:
```
region=1450
teacher=23
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
            "region": 1450,
            "center": false,
            "longitude": 710,
            "latitude": 820
        },
        {
            "id": 7,
            "name": "洛阳社区二店",
            "address": "南京路89号",
            "thumbnail": "https://s3.cn-north-1.amazonaws.com.cn/dev-upload/schools/img2_63QAijp.jpg?X-Amz-Signature=9b1c00ac36470dad42f4284fc3da3c3da9791263be05a9fa8e61d2debfd5fc19&X-Amz-Expires=3600&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIAP22CWKUZDOMHLFGA%2F20160105%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-Date=20160105T022337Z",
            "region": 1450,
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
        "available": true,
        "reserved": false,
        "last_occupied_end": null
    },
    {
        "id": 2,
        "start": "10:10",
        "end": "12:10",
        "available": false,
        "reserved": false,
        "last_occupied_end": 1467012600
    },
    ...
    ],
    "2": [{
        "id": 6,
        "start": "08:00",
        "end": "10:00",
        "available": true,
        "reserved": true,
        "last_occupied_end": 1467012600
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
            "mini_total_price": 100,
            "used": false
        },
        {
            "id": 2,
            "name": "优惠奖学金",
            "amount": 100,
            "expired_at": 1453343547,
            "mini_total_price": 50,
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
    'live_class': 12,  // only for live class
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
    "teacher": 1,
    "order_id": "1234123412341234",
    "parent": 1,
    "grade": 1,
    "id": 15,
    "total": 360,
    "price": 180,
    "hours": 14,
    "coupon": null,
    "status": "u",
    "weekly_time_slots": [3, 8],
    "subject": 1,
    "school": 1,
    "to_pay": 360,
    "is_timeslot_allocated": false,
    "live_class": 1
}
```

(Fail)HTTP Status Code 200
```
{
    "ok": false,
    "code": -1 // One or more course in the order has been assigned to other parent
            -2 // Coupon limitation validate Failed
            -3 // Live course, class room is full
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
    "ok": true(false if order status is not pending)
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
            "id": 16,
            "start": 1465179000,
            "end": 1465186200,
            "subject": "英语",
            "grade": "六年级",
            "school": "洛阳社区三店",
            "school_address": "南京路21号",
            "is_passed": false,
            "teacher": {
                "id": 9,
                "avatar": "http://127.0.0.1:8000/upload/avatars/img0_f7jKV3b.jpg",
                "name": "冯老师"
            },
            "lecturer": {
                "id": 1,
                "avatar": "http://127.0.0.1:8000/upload/avatars/img0_f7jKV3b.jpg",
                "name": "刘冠奇"
            },
            "comment": null,
            "is_expired": false,
            "is_live": true
        },
        ...
    ]
}


```
### Get student's passed courses

```
GET /api/v1/timeslots?for_review=true
```

All other parts are the same as the previous interface.


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
            "school_id": 3,
            "school": "洛阳中心店",
            "school_address": "南京路21号",
            "grade": "初一",
            "subject": "化学",
            "hours": 6,
            "status": "u",
            "order_id": "1216664871711940",
            "to_pay": 1140,
            "evaluated": true,
            "is_teacher_published": true,
            "live_class": null,
            "is_live": false
        },
        {
            "id": 32,
            "teacher": 4,
            "teacher_name": "李老师",
            "teacher_avatar": "http://127.0.0.1:8000/upload/avatars/img3_6bygJpA.jpg",
            "school_id": 3,
            "school": "洛阳中心店",
            "school_address": "南京路21号",
            "grade": "小学",  // always "小学" for live class
            "subject": "语文",
            "hours": 4,
            "status": "p",
            "order_id": "1216670256607750",
            "to_pay": 840,
            "evaluated": true,
            "is_teacher_published": true,
            "live_class": {
                "id": 1,
                "course_name": "小学新概念英语A班",
                "lecturer_name": "测试主讲老师1",
                "lecturer_avatar": "http://127.0.0.1:8000/upload/lecturers/1034716_jej5wlhj.jpeg1e_189w_219h_1c_0i_1o_90Q_1xjpeg.jpeg",
                "assistant_name": "尚尔迪",
                "assistant_avatar": "http://127.0.0.1:8000/upload/lecturers/1034716_jej5wlhj.jpeg1e_189w_219h_1c_0i_1o_90Q_1xjpeg.jpeg"
            },
            "is_live": true
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

One To One:
```
{
    "id": 32,
    "teacher": 4,
    "teacher_name": "李老师",
    "teacher_avatar": "http://127.0.0.1:8000/upload/avatars/img3_6bygJpA.jpg",
    "school_id": 3,
    "school": "洛阳中心店",
    "school_address": "南京路21号",
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
    "is_teacher_published": true,
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
    ],
    "live_class": null,
    "is_live": false
}
```
Live Course:
```
{
    "id": 32,
    "teacher": 4,
    "teacher_name": "李老师",
    "teacher_avatar": "http://127.0.0.1:8000/upload/avatars/img3_6bygJpA.jpg",
    "school_id": 3,
    "school": "洛阳中心店",
    "school_address": "南京路21号",
    "grade": "小学",  // always "小学" for live class
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
    "is_teacher_published": true,
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
    ],
    "live_class": {
        "id": 1,
        "course_name": "小学新概念英语A班",
        "course_start": 1477101600,
        "course_end": 1477713600,
        "course_period": "每周六 10:00-12:00",
        "course_fee": 48000,
        "course_lessons": 2,
        "course_grade": "小学四-六年级",
        "course_description": "课程介绍 blah blah",
        "room_capacity": 20,
        "students_count": 12,
        "lecturer_name": "测试主讲老师1",
        "lecturer_title": "新东方特级教师",
        "lecturer_bio": "多年海外留学经历",
        "lecturer_avatar": "http://127.0.0.1:8000/upload/lecturers/1034716_jej5wlhj.jpeg1e_189w_219h_1c_0i_1o_90Q_1xjpeg.jpeg",
        "assistant_name": "尚尔迪",
        "assistant_avatar": "http://127.0.0.1:8000/upload/lecturers/1034716_jej5wlhj.jpeg1e_189w_219h_1c_0i_1o_90Q_1xjpeg.jpeg"
    },
    "is_live": true
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

### Favorite Teachers List

```
GET /api/v1/favorites
```

header data:
```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

```
return same as "Teacher List"
```

### Add Favorite Teacher

```
POST /api/v1/favorites
```

header data:
```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

data:
```
{
    'teacher': {teacher id},
}
```

result:

HTTP Status Code 201
```
{
    'teacher': 1
}
```

### Cancel Favorite Teacher

```
DELETE /api/v1/favorites/{teacher id}
```

header data:
```
HTTP_AUTHORIZATION: Token 438728ef5e888bfbecbabdad189363afb28b52b3
```

result:

HTTP Status Code 200
```
{
    "ok": true
}
```

### Get Teacher Prices

```
GET /api/v1/teacher/{tid}/school/{sid}/prices/
```

result:

HTTP Status Code 200
```
{
    "count": 2,
    "next": null,
    "previous": null,
    "results": [
        {
            "grade": 2,
            "grade_name": "一年级",
            "prices": [
                {
                    "min_hours": 1,
                    "max_hours": 10,
                    "price": 17000
                },
                {
                    "min_hours": 11,
                    "max_hours": 20,
                    "price": 16500
                },
                {
                    "min_hours": 21,
                    "max_hours": 50,
                    "price": 16000
                },
                {
                    "min_hours": 51,
                    "max_hours": 100,
                    "price": 15000
                }
            ]
        },
        {
            "grade": 3,
            "grade_name": "二年级",
            "prices": [
                {
                    "min_hours": 1,
                    "max_hours": 10,
                    "price": 17000
                },
                {
                    "min_hours": 11,
                    "max_hours": 20,
                    "price": 16500
                },
                {
                    "min_hours": 21,
                    "max_hours": 50,
                    "price": 16000
                },
                {
                    "min_hours": 51,
                    "max_hours": 100,
                    "price": 15000
                }
            ]
        }
    ],
    ...
}
```

### Live Class List

```
GET /api/v1/liveclasses
```

parameters:

```
school=2
```

```
{
    "count": 2,
    "next": null,
    "previous": null,
    "results": [
        {
            "id": 1,
            "course_name": "小学新概念英语A班",
            "course_start": 1477101600,
            "course_end": 1477713600,
            "course_period": "每周六 10:00-12:00",
            "course_fee": 48000,
            "course_lessons": 2,
            "course_grade": "小学四-六年级",
            "course_description": "课程介绍 blah blah",
            "room_capacity": 20,
            "students_count": 14,
            "lecturer_name": "测试主讲老师1",
            "lecturer_title": "新东方特级教师",
            "lecturer_bio": "多年海外留学经历",
            "lecturer_avatar": "http://127.0.0.1:8000/upload/lecturers/1034716_jej5wlhj.jpeg1e_189w_219h_1c_0i_1o_90Q_1xjpeg.jpeg",
            "assistant_name": "尚尔迪",
            "assistant_avatar": "http://127.0.0.1:8000/upload/lecturers/1034716_jej5wlhj.jpeg1e_189w_219h_1c_0i_1o_90Q_1xjpeg.jpeg",
            "assistant_phone": "13011111111",
            "school_name": "洛阳社区一店",
            "school_address": "南京路21号"
        },
        {
            "id": 2,
            "course_name": "新概念英语高级班",
            "course_start": 1477202400,
            "course_end": 1477814400,
            "course_period": "每周日 14:00-16:00",
            "course_fee": 58000,
            "course_lessons": 2,
            "course_grade": "全学年",
            "course_description": "课程介绍 blah blah",
            "room_capacity": 30,
            "students_count": 23,
            "lecturer_name": "刘冠奇",
            "lecturer_title": "新东方实力讲师",
            "lecturer_bio": "网红！网红！网红！",
            "lecturer_avatar": "http://127.0.0.1:8000/upload/lecturers/1034716_jej5wlhj.jpeg1e_189w_219h_1c_0i_1o_90Q_1xjpeg_CtfdqWd.jpeg",
            "assistant_name": "尚尔迪",
            "assistant_avatar": "http://127.0.0.1:8000/upload/lecturers/1034716_jej5wlhj.jpeg1e_189w_219h_1c_0i_1o_90Q_1xjpeg.jpeg",
            "assistant_phone": "13011111111",
            "school_name": "洛阳社区一店",
            "school_address": "南京路21号"
        },
        ...
    ]
}
```

### Live Class Instance

```
GET /api/v1/liveclasses/{id}
```

```
{
    "id": 1,
    "course_name": "小学新概念英语A班",
    "course_start": 1477101600,
    "course_end": 1477713600,
    "course_period": "每周六 10:00-12:00",
    "course_fee": 48000,
    "course_lessons": 2,
    "course_grade": "小学四-六年级",
    "course_description": "课程介绍 blah blah",
    "room_capacity": 20,
    "students_count": 14,
    "lecturer_name": "测试主讲老师1",
    "lecturer_title": "新东方特级教师",
    "lecturer_bio": "多年海外留学经历",
    "lecturer_avatar": "http://127.0.0.1:8000/upload/lecturers/1034716_jej5wlhj.jpeg1e_189w_219h_1c_0i_1o_90Q_1xjpeg.jpeg",
    "assistant_name": "尚尔迪",
    "assistant_avatar": "http://127.0.0.1:8000/upload/lecturers/1034716_jej5wlhj.jpeg1e_189w_219h_1c_0i_1o_90Q_1xjpeg.jpeg",
    "assistant_phone": "13011111111",
    "school_name": "洛阳社区一店",
    "school_address": "南京路21号"
}
```

### Pad Login

```
POST /api/v1/pad/login
```

parameters:

```
phone=15127525377
```

result:

```
{
    "code": 0,
    "msg": "登录成功",
    "data": {
        "token": "mQlgxIUC1512752537746883b9bdc314bf9a400",
        "phone": "15127525377",
        "name": "小张",
        "school_id": 2,
        "school": "洛阳社区一店",
        "live_course": {
            "id": 1,
            "course_no": "1001",
            "name": "小学新概念英语A班",
            "lecturer": "测试主讲老师1",
            "grade": "小学四-六年级",
            "subject": "英语"
        }
    },
}
```

```
{
    "code": -1,
    "msg": "当前账号未注册，请查证"
}
```

```
{
    "code": -2,
    "msg": "您好，暂无有效课程"
}
```