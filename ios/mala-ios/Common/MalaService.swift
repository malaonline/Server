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
public let coupons = "/coupons"


// MARK: - Model
struct LoginUser: CustomStringConvertible {
    let accessToken: String
    let userID: Int
    let parentID: Int?
    let profileID: Int
    let firstLogin: Bool?
    let avatarURLString: String?
    
    var description: String {
        return "LoginUser(accessToken: \(accessToken), userID: \(userID), parentID: \(parentID), profileID: \(profileID))" +
        ", firstLogin: \(firstLogin)), avatarURLString: \(avatarURLString))"
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


// MARK: - User
///  保存用户信息到UserDefaults
///  - parameter loginUser: 登陆用户模型
func saveTokenAndUserInfo(loginUser: LoginUser) {
    MalaUserDefaults.userID.value = loginUser.userID
    MalaUserDefaults.parentID.value = loginUser.parentID
    MalaUserDefaults.profileID.value = loginUser.profileID
    MalaUserDefaults.firstLogin.value = loginUser.firstLogin
    MalaUserDefaults.userAccessToken.value = loginUser.accessToken
}

func sendVerifyCodeOfMobile(mobile: String, failureHandler: ((Reason, String?) -> Void)?, completion: Bool -> Void) {
    /// 参数字典
    let requestParameters = [
        "action": VerifyCodeMethod.Send.rawValue,
        "phone": mobile
    ]
    /// 返回值解析器
    let parse: JSONDictionary -> Bool? = { data in
        return true
    }
    
    /// 请求资源对象
    let resource = jsonResource(path: "/sms", method: .POST, requestParameters: requestParameters, parse: parse)
    
    /// 若未实现请求错误处理，进行默认的错误处理
    if let failureHandler = failureHandler {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: failureHandler, completion: completion)
    } else {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: defaultFailureHandler, completion: completion)
    }
}

func verifyMobile(mobile: String, verifyCode: String, failureHandler: ((Reason, String?) -> Void)?, completion: LoginUser -> Void) {
    let requestParameters = [
        "action": VerifyCodeMethod.Verify.rawValue,
        "phone": mobile,
        "code": verifyCode
    ]
    
    let parse: JSONDictionary -> LoginUser? = { data in
        return parseLoginUser(data)
    }
    
    let resource = jsonResource(path: "/sms", method: .POST, requestParameters: requestParameters, parse: parse)
    
    if let failureHandler = failureHandler {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: failureHandler, completion: completion)
    } else {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: defaultFailureHandler, completion: completion)
    }
}


// MARK: - Teacher
func loadTeachersWithConditions(conditions: JSONDictionary?, failureHandler: ((Reason, String?) -> Void)?, completion: [TeacherModel] -> Void) {
    
}


// MARK: - Payment
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

///  获取支付信息
///
///  - parameter channel:        支付方式
///  - parameter orderID:        订单id
///  - parameter failureHandler: 失败处理闭包
///  - parameter completion:     成功处理闭包
func getChargeTokenWithChannel(channel: MalaPaymentChannel, orderID: Int,failureHandler: ((Reason, String?) -> Void)?, completion: JSONDictionary -> Void) {
    let requestParameters = [
        "action": PaymentMethod.Pay.rawValue,
        "channel": channel.rawValue
    ]
    
    let parse: JSONDictionary -> JSONDictionary = { data in
        return data
    }
    
    let resource = authJsonResource(path: "/orders/\(orderID)", method: .PATCH, requestParameters: requestParameters, parse: parse)
    
    /// 若未实现请求错误处理，进行默认的错误处理
    if let failureHandler = failureHandler {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: failureHandler, completion: completion)
    } else {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: defaultFailureHandler, completion: completion)
    }
}


// MARK: - Parse
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
/// SMS验证结果解析器
let parseLoginUser: JSONDictionary -> LoginUser? = { userInfo in
    /// 判断验证结果是否正确
    guard let verified = userInfo["verified"] where (verified as? Bool) == true else {
        return nil
    }
    
    if let
        firstLogin = userInfo["first_login"] as? Bool,
        accessToken = userInfo["token"] as? String,
        parentID = userInfo["parent_id"] as? Int,
        userID = userInfo["user_id"] as? Int,
        profileID = userInfo["profile_id"] as? Int {
            return LoginUser(accessToken: accessToken, userID: userID, parentID: parentID, profileID: profileID, firstLogin: firstLogin, avatarURLString: "")
    }
    return nil
}