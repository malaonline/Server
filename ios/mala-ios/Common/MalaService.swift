//
//  MalaService.swift
//  mala-ios
//
//  Created by 王新宇 on 2/25/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import Foundation

// MARK: Api
public let MalaBaseUrl = "https://dev.malalaoshi.com/api/v1"
public let MalaBaseURL = NSURL(string: MalaBaseUrl)!
public let gradeList = "/grades"
public let subjectList = "/subjects"
public let tagList = "/tags"
public let memberServiceList = "/memberservices"
public let teacherList = "/teachers"
public let sms = "/sms"
public let schools = "/schools"
public let weeklytimeslots = "/weeklytimeslots"


// MARK: - Model
struct LoginUser: CustomStringConvertible {
    let accessToken: String
    let userID: String
    let username: String?
    let nickname: String
    let avatarURLString: String?
    
    var description: String {
        return "LoginUser(accessToken: \(accessToken), userID: \(userID), nickname: \(nickname), avatarURLString: \(avatarURLString))"
    }
}

struct VerifyingSMS: CustomStringConvertible {
    let verified: String
    let first_login: String
    let token: String?
    let parent_id: String
    let reason: String?
    
    var description: String {
        return "LoginUser(verified: \(verified), first_login: \(first_login), token: \(token), parent_id: \(parent_id), reason: \(reason))"
    }
}


// MARK: - Handler
typealias failureHandler = (Reason, String?) -> Void // 当前Swift版本Bug,无法识别这个typealias
typealias loginUserCompletion = LoginUser -> Void
typealias verifyResult = Bool -> Void


// MARK: - User
enum VerifyCodeMethod: String {
    case Send = "send"
    case Verify = "verify"
}

func sendVerifyCodeOfMobile(mobile: String, failureHandler: ((Reason, String?) -> Void)?, completion: verifyResult) {
    /// 参数字典
    let requestParameters = [
        "phone": mobile,
        "action": VerifyCodeMethod.Send.rawValue
    ]
    /// 返回值解析器
    let parse: JSONDictionary -> Bool? = { data in
        return true
    }
    
    let resource = jsonResource(path: sms, method: .POST, requestParameters: requestParameters, parse: parse)
    
    /// 若未实现请求错误处理，进行默认的错误处理
    if let failureHandler = failureHandler {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: failureHandler, completion: completion)
    } else {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: defaultFailureHandler, completion: completion)
    }
}


// MARK: - Teahcer
func loadTeachersWithConditions(conditions: JSONDictionary?, failureHandler: ((Reason, String?) -> Void)?, completion: [TeacherModel] -> Void) {
    
}


// MARK: - Order
func createOrderWithForm(orderForm: JSONDictionary, failureHandler: ((Reason, String?) -> Void)?, completion: OrderForm -> Void) {
    // teacher              老师id
    // school               上课地点id
    // grade                年级(&价格)id
    // subject              学科id
    // coupon               优惠卡券id
    // hours                用户所选课时数
    // weekly_time_slots    用户所选上课时间id数组
    
    /// 返回值解析器
    let parse: JSONDictionary -> OrderForm? = { data in
        return parseOrderForm(data)
    }
    
    let resource = authJsonResource(path: "/orders", method: .POST, requestParameters: orderForm, parse: parse)
    
    /// 若未实现请求错误处理，进行默认的错误处理
    if let failureHandler = failureHandler {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: failureHandler, completion: completion)
    } else {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: defaultFailureHandler, completion: completion)
    }
}

/// 订单JSON解析器
let parseOrderForm: JSONDictionary -> OrderForm? = { orderInfo in
    if let
        id = orderInfo["id"] as? Int,
        teacher = orderInfo["teacher"] as? Int,
        parent = orderInfo["parent"] as? Int,
        school = orderInfo["school"] as? Int,
        grade = orderInfo["grade"] as? Int,
        subject = orderInfo["subject"] as? Int,
        coupon = orderInfo["coupon"] as? Int,
        hours = orderInfo["hours"] as? Int,
        weekly_time_slots = orderInfo["weekly_time_slots"] as? [Int],
        price = orderInfo["price"] as? Int,
        total = orderInfo["total"] as? Int,
        status = orderInfo["status"] as? String,
        order_id = orderInfo["order_id"] as? String {
            return OrderForm(id: id, name: "", teacher: teacher, school: school, grade: grade,
                subject: subject, coupon: coupon, hours: hours, timeSchedule: weekly_time_slots,
                order_id: order_id, parent: parent, total: total, price: price, status: status)
    }
    return nil
}