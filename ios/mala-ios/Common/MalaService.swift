//
//  MalaService.swift
//  mala-ios
//
//  Created by 王新宇 on 2/25/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import Foundation
import Alamofire

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


// MARK: - typealias
typealias nullDictionary = [String: AnyObject]


// MARK: - Model
///  登陆用户信息结构体
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

///  SMS验证结果结构体
struct VerifyingSMS: CustomStringConvertible {
    let verified: String
    let first_login: String
    let token: String?
    let parent_id: String
    let reason: String?
    
    var description: String {
        return "VerifyingSMS(verified: \(verified), first_login: \(first_login), token: \(token), parent_id: \(parent_id), reason: \(reason))"
    }
}

///  个人账号信息结构体
struct profileInfo: CustomStringConvertible {
    let id: Int
    let gender: String?
    let avatar: String?
    
    var description: String {
        return "parentInfo(id: \(id), gender: \(gender), avatar: \(avatar)"
    }
}

///  家长账号信息结构体
struct parentInfo: CustomStringConvertible {
    let id: Int
    let studentName: String?
    let schoolName: String?
    
    var description: String {
        return "parentInfo(id: \(id), studentName: \(studentName), schoolName: \(schoolName)"
    }
}


// MARK: - Support Method
///  登陆成功后，获取个人信息和家长信息并保存到UserDefaults
func getInfoWhenLoginSuccess() {
    
    // 个人信息
    getAndSaveProfileInfo()
    
    // 家长信息
    getAndSaveParentInfo()
}

func getAndSaveProfileInfo() {
    let profileID = MalaUserDefaults.profileID.value ?? 0
    getProfileInfo(profileID, failureHandler: { (reason, errorMessage) -> Void in
        defaultFailureHandler(reason, errorMessage: errorMessage)
        // 错误处理
        if let errorMessage = errorMessage {
            println("MalaService - getProfileInfo Error \(errorMessage)")
        }
        },completion: { (profile) -> Void in
            println("保存Profile信息: \(profile)")
            saveProfileInfoToUserDefaults(profile)
    })
}

func getAndSaveParentInfo() {
    let parentID = MalaUserDefaults.parentID.value ?? 0
    getParentInfo(parentID, failureHandler: { (reason, errorMessage) -> Void in
        defaultFailureHandler(reason, errorMessage: errorMessage)
        // 错误处理
        if let errorMessage = errorMessage {
            println("MalaService - getParentInfo Error \(errorMessage)")
        }
        },completion: { (parent) -> Void in
            println("保存Parent信息: \(parent)")
            saveParentInfoToUserDefaults(parent)
    })
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
////  保存个人信息到UserDefaults
///
///  - parameter profile: 个人信息模型
func saveProfileInfoToUserDefaults(profile: profileInfo) {
    MalaUserDefaults.gender.value = profile.gender
    MalaUserDefaults.avatar.value = profile.avatar
}
///  保存家长信息到UserDefaults
///
///  - parameter parent: 家长信息模型
func saveParentInfoToUserDefaults(parent: parentInfo) {
    MalaUserDefaults.studentName.value = parent.studentName
    MalaUserDefaults.schoolName.value = parent.schoolName
}

///  获取验证码
///
///  - parameter mobile:         手机号码
///  - parameter failureHandler: 失败处理闭包
///  - parameter completion:     成功处理闭包
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

///  验证手机号
///
///  - parameter mobile:         手机号码
///  - parameter verifyCode:     验证码
///  - parameter failureHandler: 失败处理闭包
///  - parameter completion:     成功处理闭包
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

///  根据个人id获取个人信息
///
///  - parameter parentID:       个人
///  - parameter failureHandler: 失败处理闭包
///  - parameter completion:     成功处理闭包
func getProfileInfo(profileID: Int, failureHandler: ((Reason, String?) -> Void)?, completion: profileInfo -> Void) {
    let parse: JSONDictionary -> profileInfo? = { data in
        return parseProfile(data)
    }
    
    let resource = authJsonResource(path: "/profiles/\(profileID)", method: .GET, requestParameters: nullDictionary(), parse: parse)
    
    if let failureHandler = failureHandler {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: failureHandler, completion: completion)
    } else {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: defaultFailureHandler, completion: completion)
    }
}

///  根据家长id获取家长信息
///
///  - parameter parentID:       家长id
///  - parameter failureHandler: 失败处理闭包
///  - parameter completion:     成功处理闭包
func getParentInfo(parentID: Int, failureHandler: ((Reason, String?) -> Void)?, completion: parentInfo -> Void) {
    let parse: JSONDictionary -> parentInfo? = { data in
        return parseParent(data)
    }
    
    let resource = authJsonResource(path: "/parents/\(parentID)", method: .GET, requestParameters: nullDictionary(), parse: parse)
    
    if let failureHandler = failureHandler {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: failureHandler, completion: completion)
    } else {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: defaultFailureHandler, completion: completion)
    }
}

///  上传用户头像
///
///  - parameter imageData:      头像
///  - parameter failureHandler: 失败处理闭包
///  - parameter completion:     成功处理闭包
func updateAvatarWithImageData(imageData: NSData, failureHandler: ((Reason, String?) -> Void)?, completion: Bool -> Void) {
    
    guard let token = MalaUserDefaults.userAccessToken.value else {
        println("updateAvatarWithImageData error - no token")
        return
    }
    
    guard let profileID = MalaUserDefaults.profileID.value else {
        println("updateAvatarWithImageData error - no profileID")
        return
    }
    
    let parameters: [String: String] = [
        "Authorization": "Token \(token)",
    ]
    
    let fileName = "avatar.jpg"
    
    Alamofire.upload(.PATCH, MalaBaseUrl + "/profiles/\(profileID)", headers: parameters, multipartFormData: { multipartFormData in
        
        multipartFormData.appendBodyPart(data: imageData, name: "avatar", fileName: fileName, mimeType: "image/jpeg")
        
        }, encodingCompletion: { encodingResult in
            println("encodingResult: \(encodingResult)")
            
            switch encodingResult {
                
            case .Success(let upload, _, _):
                
                upload.responseJSON(completionHandler: { response in
                    
                    guard let
                        data = response.data,
                        json = decodeJSON(data),
                        uploadResult = json["done"] as? String else {
                            failureHandler?(.CouldNotParseJSON, nil)
                            return
                    }
                    let result = (uploadResult == "true" ? true : false)
                    completion(result)
                })
                
            case .Failure(let encodingError):
                
                failureHandler?(.Other(nil), "\(encodingError)")
            }
    })
}

///  保存学生姓名
///
///  - parameter name:           姓名
///  - parameter failureHandler: 失败处理闭包
///  - parameter completion:     成功处理闭包
func saveStudentName(name: String, failureHandler: ((Reason, String?) -> Void)?, completion: Bool -> Void) {
    
    guard let parentID = MalaUserDefaults.parentID.value else {
        println("saveStudentSchoolName error - no profileID")
        return
    }
    
    let requestParameters = [
        "student_name": name,
    ]
    
    let parse: JSONDictionary -> Bool? = { data in
        return true
    }
    
    let resource = authJsonResource(path: "/parents/\(parentID)", method: .PATCH, requestParameters: requestParameters, parse: parse)
    
    if let failureHandler = failureHandler {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: failureHandler, completion: completion)
    } else {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: defaultFailureHandler, completion: completion)
    }
}

///  保存学生学校名称
///
///  - parameter name:           学校名称
///  - parameter failureHandler: 失败处理闭包
///  - parameter completion:     成功处理闭包
func saveStudentSchoolName(name: String, failureHandler: ((Reason, String?) -> Void)?, completion: Bool -> Void) {
    
    guard let parentID = MalaUserDefaults.parentID.value else {
        println("saveStudentSchoolName error - no profileID")
        return
    }
    
    let requestParameters = [
        "student_school_name": name,
    ]
    
    let parse: JSONDictionary -> Bool? = { data in
        return true
    }
    
    let resource = authJsonResource(path: "/parents/\(parentID)", method: .PATCH, requestParameters: requestParameters, parse: parse)
    
    if let failureHandler = failureHandler {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: failureHandler, completion: completion)
    } else {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: defaultFailureHandler, completion: completion)
    }
}

///  优惠券列表解析函数
///
///  - parameter failureHandler: 失败处理闭包
///  - parameter completion:     成功处理闭包
func getCouponList(failureHandler: ((Reason, String?) -> Void)?, completion: [CouponModel] -> Void) {
    
    let parse: [JSONDictionary] -> [CouponModel] = { couponData in
        /// 解析优惠券JSON数组
        var coupons = [CouponModel]()
        for couponInfo in couponData {
            if let coupon = parseCoupon(couponInfo) {
                coupon.setupStatus()
                coupons.append(coupon)
            }
        }
        return coupons
    }
    
    ///  获取优惠券列表JSON对象
    headBlockedCoupons(failureHandler) { (jsonData) -> Void in
        if let coupons = jsonData["results"] as? [JSONDictionary] where coupons.count != 0 {
            completion(parse(coupons))
        }else {
            completion([])
        }
    }
}

///  获取优惠券列表
///
///  - parameter failureHandler: 失败处理闭包
///  - parameter completion:     成功处理闭包
func headBlockedCoupons(failureHandler: ((Reason, String?) -> Void)?, completion: JSONDictionary -> Void) {
    
    let parse: JSONDictionary -> JSONDictionary? = { data in
        return data
    }
    
    let resource = authJsonResource(path: "/coupons", method: .GET, requestParameters: nullDictionary(), parse: parse)
    
    apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: defaultFailureHandler, completion: completion)
}

///  判断用户是否第一次购买此学科的课程
///
///  - parameter subjectID:      学科id
///  - parameter failureHandler: 失败处理闭包
///  - parameter completion:     成功处理闭包
func isHasBeenEvaluatedWithSubject(subjectID: Int, failureHandler: ((Reason, String?) -> Void)?, completion: Bool -> Void) {

    let parse: JSONDictionary -> Bool = { data in
        if let result = data["evaluated"] as? Bool {
            // 服务器返回结果为：用户是否已经做过此学科的建档测评，是则代表非首次购买。故取反处理。
            return !result
        }
        return true
    }
    
    let resource = authJsonResource(path: "/subject/\(subjectID)/record", method: .GET, requestParameters: nullDictionary(), parse: parse)
    
    if let failureHandler = failureHandler {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: failureHandler, completion: completion)
    } else {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: defaultFailureHandler, completion: completion)
    }
}

///  获取学生上课时间表
///
///  - parameter page:           页数
///  - parameter failureHandler: 失败处理闭包
///  - parameter completion:     成功处理闭包
func getStudentCourseTable(page: Int = 1, failureHandler: ((Reason, String?) -> Void)?, completion: [StudentCourseModel] -> Void) {
    
    let parse: JSONDictionary -> [StudentCourseModel] = { data in
        return parseStudentCourse(data)
    }
    
    let resource = authJsonResource(path: "/timeslots", method: .GET, requestParameters: nullDictionary(), parse: parse)
    
    if let failureHandler = failureHandler {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: failureHandler, completion: completion)
    } else {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: defaultFailureHandler, completion: completion)
    }
}


// MARK: - Teacher
///  获取[指定老师]在[指定上课地点]的可用时间表
///
///  - parameter teacherID:      老师id
///  - parameter schoolID:       上课地点id
///  - parameter failureHandler: 失败处理闭包
///  - parameter completion:     成功处理闭包
func getTeacherAvailableTimeInSchool(teacherID: Int, schoolID: Int, failureHandler: ((Reason, String?) -> Void)?, completion: [[ClassScheduleDayModel]] -> Void) {
    
    let requestParameters = [
        "school_id": schoolID,
    ]
    
    let parse: JSONDictionary -> [[ClassScheduleDayModel]] = { data in
        return parseClassSchedule(data)
    }
    
    let resource = authJsonResource(path: "teachers/\(teacherID)/weeklytimeslots", method: .GET, requestParameters: requestParameters, parse: parse)
    
    if let failureHandler = failureHandler {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: failureHandler, completion: completion)
    } else {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: defaultFailureHandler, completion: completion)
    }
}

func loadTeachersWithConditions(conditions: JSONDictionary?, failureHandler: ((Reason, String?) -> Void)?, completion: [TeacherModel] -> Void) {
    
}


// MARK: - Payment
///  创建订单
///
///  - parameter orderForm:      订单对象字典
///  - parameter failureHandler: 失败处理闭包
///  - parameter completion:     成功处理闭包
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
func getChargeTokenWithChannel(channel: MalaPaymentChannel, orderID: Int, failureHandler: ((Reason, String?) -> Void)?, completion: JSONDictionary -> Void) {
    let requestParameters = [
        "action": PaymentMethod.Pay.rawValue,
        "channel": channel.rawValue
    ]
    
    let parse: JSONDictionary -> JSONDictionary = { data in
        return data
    }
    
    let resource = authJsonResource(path: "/orders/\(orderID)", method: .PATCH, requestParameters: requestParameters, parse: parse)
    
    if let failureHandler = failureHandler {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: failureHandler, completion: completion)
    } else {
        apiRequest({_ in}, baseURL: MalaBaseURL, resource: resource, failure: defaultFailureHandler, completion: completion)
    }
}

///  获取订单信息
///
///  - parameter orderID:        订单id
///  - parameter failureHandler: 失败处理闭包
///  - parameter completion:     成功处理闭包
func getOrderInfo(orderID: Int, failureHandler: ((Reason, String?) -> Void)?, completion: OrderForm -> Void) {
    /// 返回值解析器
    let parse: JSONDictionary -> OrderForm? = { data in
        return parseOrderForm(data)
    }
    
    let resource = authJsonResource(path: "/orders/\(orderID)", method: .GET, requestParameters: nullDictionary(), parse: parse)
    
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
        // coupon = orderInfo["coupon"] as? Int, // 返回优惠券id暂时无用，返回可能为null值。暂不处理
        hours = orderInfo["hours"] as? Int,
        weekly_time_slots = orderInfo["weekly_time_slots"] as? [Int],
        price = orderInfo["price"] as? Int,
        total = orderInfo["total"] as? Int,
        status = orderInfo["status"] as? String,
        order_id = orderInfo["order_id"] as? String {
            return OrderForm(id: id, name: "", teacher: teacher, school: school, grade: grade,
                subject: subject, coupon: nil, hours: hours, timeSchedule: weekly_time_slots,
                order_id: order_id, parent: parent, total: total, price: price, status: status)
    }
    return nil
}
/// SMS验证结果JSON解析器
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
/// 个人信息JSON解析器
let parseProfile: JSONDictionary -> profileInfo? = { profileData in
    /// 判断验证结果是否正确
    guard let profileID = profileData["id"] else {
        return nil
    }
    
    if let
        id = profileData["id"] as? Int,
        gender = profileData["gender"] as? String? {
            let avatar = (profileData["avatar"] as? String) ?? ""
            return profileInfo(id: id, gender: gender, avatar: avatar)
    }
    return nil
}
/// 家长信息JSON解析器
let parseParent: JSONDictionary -> parentInfo? = { parentData in
    /// 判断验证结果是否正确
    guard let parentID = parentData["id"] else {
        return nil
    }
    
    if let
        id = parentData["id"] as? Int,
        studentName = parentData["student_name"] as? String?,
        schoolName = parentData["student_school_name"] as? String? {
            return parentInfo(id: id, studentName: studentName, schoolName: schoolName)
    }
    return nil
}
/// 优惠券JSON解析器
let parseCoupon: JSONDictionary -> CouponModel? = { couponInfo in

    /// 检测返回值有效性
    guard let id = couponInfo["id"] else {
        return nil
    }
    
    if let
        id = couponInfo["id"] as? Int,
        name = couponInfo["name"] as? String,
        amount = couponInfo["amount"] as? Int,
        expired_at = couponInfo["expired_at"] as? NSTimeInterval,
        used = couponInfo["used"] as? Bool {
            return CouponModel(id: id, name: name, amount: amount, expired_at: expired_at, used: used)
    }
    return nil
}
/// 可用上课时间表JSON解析器
let parseClassSchedule: JSONDictionary -> [[ClassScheduleDayModel]] = { scheduleInfo in
    
    // 本周时间表
    var weekSchedule: [[ClassScheduleDayModel]] = []
    
    // 循环一周七天的可用时间表
    for index in 1...7 {
        if let day = scheduleInfo[String(index)] as? [[String: AnyObject]] {
            var daySchedule: [ClassScheduleDayModel] = []
            for dict in day {
                daySchedule.append(ClassScheduleDayModel(dict: dict))
            }
            weekSchedule.append(daySchedule)
        }
    }
    return weekSchedule
}
/// 学生上课时间表JSON解析器
let parseStudentCourse: JSONDictionary -> [StudentCourseModel] = { courseInfos in
    
    /// 学生上课时间数组
    var courseList: [StudentCourseModel] = []
    
    /// 确保相应格式正确，且存在数据
    guard let courses = courseInfos["results"] as? [JSONDictionary] where courses.count != 0 else {
        return courseList
    }
    
    ///  遍历字典数组，转换为模型
    for course in courses {
        if let
            id = course["id"] as? Int,
            end = course["end"] as? NSTimeInterval,
            subject = course["subject"] as? String,
            is_passed = course["is_passed"] as? Bool,
            is_commened = course["is_commened"] as? Bool {
                courseList.append(StudentCourseModel(dict: course))
        }
    }
    return courseList
}