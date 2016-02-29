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
public let orders = "/orders"


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
//func createOrderWith