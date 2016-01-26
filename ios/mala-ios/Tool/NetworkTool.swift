//
//  NetworkTool.swift
//  mala-ios
//
//  Created by Elors on 12/21/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit
import Alamofire

class NetworkTool {

    // MARK: - Property
    private let baseUrl = "https://dev.malalaoshi.com/api/v1"
    private let gradeList = "/grades/"
    private let subjectList = "/subjects/"
    private let tagList = "/tags/"
    private let memberServiceList = "/memberservices/"
    private let teacherList = "/teachers/"
    private let sms = "/sms/"
    private let schools = "/schools/"
    private let weeklytimeslots = "/weeklytimeslots/"
    // Result Closure
    typealias RequestCallBack = (result: AnyObject?, error: NSError?)->()
    // Singleton
    private init() {}
    static let sharedTools = NetworkTool()
}


// MARK: - Request Method
extension NetworkTool {
    
    ///  Request for GradeList
    ///
    ///  - parameter finished: Closure for Finished
    func loadGrades(finished: RequestCallBack) {
        request(.GET, URLString: baseUrl+gradeList, parameters: nil, finished: finished)
    }
    
    ///  Request for SubjectList
    ///
    ///  - parameter finished: Closure for Finished
    func loadSubjects(finished: RequestCallBack) {
        request(.GET, URLString: baseUrl+subjectList, parameters: nil, finished: finished)
    }
    
    ///  Request for TagList
    ///
    ///  - parameter finished: Closure for Finished
    func loadTags(finished: RequestCallBack) {
        request(.GET, URLString: baseUrl+tagList, parameters: nil, finished: finished)
    }
    
    ///  Request for MemberserviceList
    ///
    ///  - parameter finished: Closure for Finished
    func loadMemberServices(finished: RequestCallBack) {
        request(.GET, URLString: baseUrl+memberServiceList, parameters: nil, finished: finished)
    }
    
    ///  Request for TeacherList
    ///
    ///  - parameter finished: Closure for Finished
    func loadTeachers(parameters: [String: AnyObject]?, finished: RequestCallBack) {
        request(.GET, URLString: baseUrl+teacherList, parameters: parameters, finished: finished)
    }
    
    ///  Request for Teacher Detail
    ///
    ///  - parameter id:       id of teacher
    ///  - parameter finished: Closure for Finished
    func loadTeacherDetail(id: Int, finished: RequestCallBack) {
        request(.GET, URLString: baseUrl+teacherList+String(id), parameters: nil, finished: finished)
    }
    
    ///  Request for send SMS
    ///
    ///  - parameter number:   string for phone number
    ///  - parameter finished: Closure for Finished
    func sendSMS(number: String, finished: RequestCallBack) {
        var params = [String: AnyObject]()
        params["action"] = "send"
        params["phone"] = "0001" //TODO: Delete Test Param
        request(.POST, URLString: baseUrl+sms, parameters: params, finished: finished)
    }
    
    ///  Request for verify SMS
    ///
    ///  - parameter number:   string for phone number
    ///  - parameter code:     string for verify code
    ///  - parameter finished: Closure for Finished
    func verifySMS(number: String, code: String, finished: RequestCallBack) {
        var params = [String: AnyObject]()
        params["action"] = "verify"
        params["phone"] = "0001" //TODO: Delete Test Param
        params["code"] = "1111" //TODO: Delete Test Param
        request(.POST, URLString: baseUrl+sms, parameters: params, finished: finished)
    }
    
    ///  Request for SchoolList
    ///
    ///  - parameter finished: Closure for Finished
    func loadSchools(finished: RequestCallBack) {
        request(.GET, URLString: baseUrl+schools, parameters: nil, finished: finished)
    }
    
    ///  Request for ClassSchedule With TeacherId and SchoolId
    ///
    ///  - parameter teacherId: Int for teacherId
    ///  - parameter schoolId:  Int for schoolId
    ///  - parameter finished:  Closure for Finished
    func loadClassSchedule(teacherId: Int, schoolId: Int, finished: RequestCallBack) {
        var params = [String: AnyObject]()
        params["school_id"] = schoolId
        request(.GET, URLString: baseUrl+teacherList+String(teacherId)+weeklytimeslots, parameters: params, finished: finished)
    }
}


// MARK: - Encapsulation Alamofire Framework
extension NetworkTool {
    
    ///  NetWork Request
    ///
    ///  - parameter method:     OPTIONS, GET, HEAD, POST, PUT, PATCH, DELETE, TRACE, CONNECT
    ///  - parameter URLString:  String for URL
    ///  - parameter parameters: Dictionary for Parameters
    ///  - parameter finished:   Closure for Finished
    private func request(method: Alamofire.Method, URLString: String, parameters: [String: AnyObject]?, finished: RequestCallBack) {
        // Show Networking Symbol
        UIApplication.sharedApplication().networkActivityIndicatorVisible = true
        
        // Request
        Alamofire.request(method, URLString, parameters: parameters).responseJSON { (response) -> Void in
            
            // hide Networking Symbol
            UIApplication.sharedApplication().networkActivityIndicatorVisible = false
            
            if response.result.isFailure {
                debugPrint("Network Request Failure - \(response.result.error)")
            }
            // Finished
            finished(result: response.result.value, error: response.result.error)
        }
    }
}


