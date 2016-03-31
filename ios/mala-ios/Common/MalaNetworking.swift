//
//  MalaNetworking.swift
//  mala-ios
//
//  Created by Elors on 12/21/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit
import Alamofire

// MARK: - Enum
/// 请求方法类型
public enum Method: String, CustomStringConvertible {
    case OPTIONS = "OPTIONS"
    case GET = "GET"
    case HEAD = "HEAD"
    case POST = "POST"
    case PUT = "PUT"
    case PATCH = "PATCH"
    case DELETE = "DELETE"
    case TRACE = "TRACE"
    case CONNECT = "CONNECT"
    
    public var description: String {
        return self.rawValue
    }
}

///  内容编码集类型
public enum ContentType: String {
    case JSON = "application/json"
    case URLEncoded = "application/x-www-form-urlencoded; charset=utf-8"
}

///  错误原因
///
///  - CouldNotParseJSON:   无法解析JSON
///  - NoData:              无数据
///  - NoSuccessStatusCode: 状态码异常
///  - Other:               其他
public enum Reason: CustomStringConvertible {
    case CouldNotParseJSON
    case NoData
    case NoSuccessStatusCode(statusCode: Int)
    case Other(NSError?)
    
    public var description: String {
        switch self {
        case .CouldNotParseJSON:
            return "CouldNotParseJSON"
        case .NoData:
            return "NoData"
        case .NoSuccessStatusCode(let statusCode):
            return "NoSuccessStatusCode: \(statusCode)"
        case .Other(let error):
            return "Other, Error: \(error?.description)"
        }
    }
}

// MARK: - Property
/// 网络请求次数
var MalaNetworkActivityCount = 0 {
    didSet {
        UIApplication.sharedApplication().networkActivityIndicatorVisible = (MalaNetworkActivityCount > 0)
    }
}

// MARK: - Model
///  请求资源对象
public struct Resource<A>: CustomStringConvertible {
    let path: String
    let method: Method
    let requestBody: NSData?
    let headers: [String:String]
    let parse: NSData -> A?
    
    public var description: String {
        let decodeRequestBody: [String: AnyObject]
        if let requestBody = requestBody {
            decodeRequestBody = decodeJSON(requestBody)!
        } else {
            decodeRequestBody = [:]
        }
        
        return "Resource(Method: \(method), path: \(path), headers: \(headers), requestBody: \(decodeRequestBody))"
    }
}

// MARK: - Method
///  请求错误缺省处理方法
///
///  - parameter reason:       错误原因
///  - parameter errorMessage: 错误信息
func defaultFailureHandler(reason: Reason, errorMessage: String?) {
    println("\n***************************** MalaNetworking Failure *****************************")
    println("Reason: \(reason)")
    if let errorMessage = errorMessage {
        println("errorMessage: >>>\(errorMessage)<<<\n")
    }
}

///  解析出Data(JSON格式)中的error字符串信息
///
///  - parameter data: NSData对象
///
///  - returns: error字符串信息
func errorMessageInData(data: NSData?) -> String? {
    if let data = data {
        if let json = decodeJSON(data) {
            if let errorMessage = json["error"] as? String {
                return errorMessage
            }
        }
    }
    return nil
}

public func apiRequest<A>(modifyRequest: NSMutableURLRequest -> (), baseURL: NSURL, resource: Resource<A>, failure: (Reason, String?) -> Void, completion: A -> Void) {
    #if STAGING
        let sessionConfig = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: sessionConfig, delegate: _sessionDelegate, delegateQueue: nil)
    #else
        let session = NSURLSession.sharedSession()
    #endif
    
    let url = baseURL.URLByAppendingPathComponent(resource.path)
    let request = NSMutableURLRequest(URL: url)
    request.HTTPMethod = resource.method.rawValue
    
    
    func needEncodesParametersForMethod(method: Method) -> Bool {
        switch method {
        case .GET, .HEAD, .DELETE:
            return true
        default:
            return false
        }
    }
    
    func query(parameters: [String: AnyObject]) -> String {
        var components: [(String, String)] = []
        for key in Array(parameters.keys).sort(<) {
            let value: AnyObject! = parameters[key]
            components += queryComponents(key, value: value)
        }
        
        return (components.map{"\($0)=\($1)"} as [String]).joinWithSeparator("&")
    }
    
    if needEncodesParametersForMethod(resource.method) {
        if let requestBody = resource.requestBody {
            if let URLComponents = NSURLComponents(URL: request.URL!, resolvingAgainstBaseURL: false) {
                URLComponents.percentEncodedQuery = (URLComponents.percentEncodedQuery != nil ? URLComponents.percentEncodedQuery! + "&" : "") + query(decodeJSON(requestBody)!)
                request.URL = URLComponents.URL
            }
        }
        
    } else {
        request.HTTPBody = resource.requestBody
    }
    
    modifyRequest(request)
    
    for (key, value) in resource.headers {
        request.setValue(value, forHTTPHeaderField: key)
    }
    
    let task = session.dataTaskWithRequest(request) { (data, response, error) -> Void in
        
        if let httpResponse = response as? NSHTTPURLResponse {
            
            // 识别StatusCode并处理
            switch httpResponse.statusCode {
            // 成功, 订单创建
            case 200, 201:
                if let responseData = data {
                    
                    if let result = resource.parse(responseData) {
                        completion(result)
                        
                    } else {
                        let dataString = NSString(data: responseData, encoding: NSUTF8StringEncoding)
                        println(dataString)
                        
                        failure(Reason.CouldNotParseJSON, errorMessageInData(data))
                        println("\(resource)\n")
                        println(request.cURLString)
                    }
                    
                } else {
                    failure(Reason.NoData, errorMessageInData(data))
                    println("\(resource)\n")
                    println(request.cURLString)
                }
                break
                
            // 失败, 其他
            default:
                failure(Reason.NoSuccessStatusCode(statusCode: httpResponse.statusCode), errorMessageInData(data))
                println("\(resource)\n")
                println(request.cURLString)
                
                // 对于 401: errorMessage: >>>HTTP Token: Access denied<<<
                // 用户需要重新登录，所以
                if httpResponse.statusCode == 401 {
                    
                    // 确保是自家服务
                    if let requestHost = request.URL?.host where requestHost == NSURL(string: MalaBaseUrl)!.host {
                        dispatch_async(dispatch_get_main_queue()) {
                            //TODO: 重新登陆
                        }
                    }
                }
                break
            }
        } else {
            // 请求无响应, 错误处理
            failure(Reason.Other(error), errorMessageInData(data))
            println("\(resource)")
            println(request.cURLString)
        }
        
        ///  开启网络请求指示器
        dispatch_async(dispatch_get_main_queue()) {
            MalaNetworkActivityCount -= 1
        }
    }
    
    ///  执行任务
    task.resume()
    
    ///  关闭网络请求指示器
    dispatch_async(dispatch_get_main_queue()) {
        MalaNetworkActivityCount += 1
    }
}


func queryComponents(key: String, value: AnyObject) -> [(String, String)] {
    func escape(string: String) -> String {
        let legalURLCharactersToBeEscaped: CFStringRef = ":/?&=;+!@#$()',*"
        return CFURLCreateStringByAddingPercentEscapes(nil, string, nil, legalURLCharactersToBeEscaped, CFStringBuiltInEncodings.UTF8.rawValue) as String
    }
    
    var components: [(String, String)] = []
    if let dictionary = value as? [String: AnyObject] {
        for (nestedKey, value) in dictionary {
            components += queryComponents("\(key)[\(nestedKey)]", value: value)
        }
    } else if let array = value as? [AnyObject] {
        for value in array {
            components += queryComponents("\(key)[]", value: value)
        }
    } else {
        components.appendContentsOf([(escape(key), escape("\(value)"))])
    }
    
    return components
}


// MARK: - JSON Handle
/// 字典别名
public typealias JSONDictionary = [String: AnyObject]

///  解析NSData to JSONDictionary
///
///  - parameter data: NSData
///
///  - returns: JSONDictionary
func decodeJSON(data: NSData) -> JSONDictionary? {
    if data.length > 0 {
        guard let result = try? NSJSONSerialization.JSONObjectWithData(data, options: NSJSONReadingOptions()) else {
            return JSONDictionary()
        }
        
        if let dictionary = result as? JSONDictionary {
            return dictionary
        } else if let array = result as? [JSONDictionary] {
            return ["data": array]
        } else {
            return JSONDictionary()
        }
        
    } else {
        return JSONDictionary()
    }
}

///  编码JSONDictionary to NSData
///
///  - parameter dict: JSONDictionary
///
///  - returns: NSData
func encodeJSON(dict: JSONDictionary) -> NSData? {
    return dict.count > 0 ? (try? NSJSONSerialization.dataWithJSONObject(dict, options: NSJSONWritingOptions())) : nil
}

///  根据请求数据返回对应的Resource结构体（无Token）
///
///  - parameter path:              Api路径
///  - parameter method:            请求方法
///  - parameter requestParameters: 参数字典
///  - parameter parse:             JSON解析器
///
///  - returns: Resource结构体
public func jsonResource<A>(path path: String, method: Method, requestParameters: JSONDictionary, parse: JSONDictionary -> A?) -> Resource<A> {
    return jsonResource(token: nil, path: path, method: method, requestParameters: requestParameters, parse: parse)
}

///  根据请求数据返回对应的Resource结构体（有Token）
///
///  - parameter path:              Api路径
///  - parameter method:            请求方法
///  - parameter requestParameters: 参数字典
///  - parameter parse:             JSON解析器
///
///  - returns: Resource结构体
public func authJsonResource<A>(path path: String, method: Method, requestParameters: JSONDictionary, parse: JSONDictionary -> A?) -> Resource<A> {
    let token = MalaUserDefaults.userAccessToken.value
    return jsonResource(token: token, path: path, method: method, requestParameters: requestParameters, parse: parse)
}

///  根据请求数据返回对应的Resource结构体
///
///  - parameter token:             用户令牌
///  - parameter path:              Api路径
///  - parameter method:            请求方法
///  - parameter requestParameters: 参数字典
///  - parameter parse:             JSON解析器
///
///  - returns: Resource结构体
public func jsonResource<A>(token token: String?, path: String, method: Method, requestParameters: JSONDictionary, parse: JSONDictionary -> A?) -> Resource<A> {
    /// JSON解析器
    let jsonParse: NSData -> A? = { data in
        if let json = decodeJSON(data) {
            return parse(json)
        }
        return nil
    }
    /// 请求头
    var headers = [
        "Content-Type": "application/json",
    ]
    let locale = NSLocale.autoupdatingCurrentLocale()
    if let
        languageCode = locale.objectForKey(NSLocaleLanguageCode) as? String,
        countryCode = locale.objectForKey(NSLocaleCountryCode) as? String {
            headers["Accept-Language"] = languageCode + "-" + countryCode
    }
    /// 请求体
    let jsonBody = encodeJSON(requestParameters)
    /// 用户令牌
    if let token = token {
        headers["Authorization"] = "Token \(token)"
    }
    return Resource(path: path, method: method, requestBody: jsonBody, headers: headers, parse: jsonParse)
}











// Result Closure
typealias RequestCallBack = (result: AnyObject?, error: NSError?)->()

class MalaNetworking {
    // Singleton
    private init() {}
    static let sharedTools = MalaNetworking()
}


// MARK: - Request Method
extension MalaNetworking {
    
    ///  Request for GradeList
    ///
    ///  - parameter finished: Closure for Finished
    func loadGrades(finished: RequestCallBack) {
        request(.GET, URLString: MalaBaseUrl+gradeList, parameters: nil, finished: finished)
    }
    
    ///  Request for SubjectList
    ///
    ///  - parameter finished: Closure for Finished
    func loadSubjects(finished: RequestCallBack) {
        request(.GET, URLString: MalaBaseUrl+subjectList, parameters: nil, finished: finished)
    }
    
    ///  Request for TagList
    ///
    ///  - parameter finished: Closure for Finished
    func loadTags(finished: RequestCallBack) {
        request(.GET, URLString: MalaBaseUrl+tagList, parameters: nil, finished: finished)
    }
    
    ///  Request for MemberserviceList
    ///
    ///  - parameter finished: Closure for Finished
    func loadMemberServices(finished: RequestCallBack) {
        request(.GET, URLString: MalaBaseUrl+memberServiceList, parameters: nil, finished: finished)
    }
    
    ///  Request for TeacherList
    ///
    ///  - parameter parameters: Filter Dict
    ///  - parameter page:       page number
    ///  - parameter finished:   Closure for Finished
    func loadTeachers(parameters: [String: AnyObject]?, page: Int = 1, finished: RequestCallBack) {
        var params = parameters ?? [String: AnyObject]()
        params["page"] = page
        request(.GET, URLString: MalaBaseUrl+teacherList, parameters: params, finished: finished)
    }
    
    ///  Request for Teacher Detail
    ///
    ///  - parameter id:       id of teacher
    ///  - parameter finished: Closure for Finished
    func loadTeacherDetail(id: Int, finished: RequestCallBack) {
        request(.GET, URLString: MalaBaseUrl+teacherList+"/"+String(id), parameters: nil, finished: finished)
    }
    
    ///  Request for verify SMS
    ///
    ///  - parameter number:   string for phone number
    ///  - parameter code:     string for verify code
    ///  - parameter finished: Closure for Finished
    func verifySMS(number: String, code: String, finished: RequestCallBack) {
        var params = [String: AnyObject]()
        params["action"] = "verify"
        params["phone"] = number
        params["code"] = code
        request(.POST, URLString: MalaBaseUrl+sms, parameters: params, finished: finished)
    }
    
    ///  Request for SchoolList
    ///
    ///  - parameter finished: Closure for Finished
    func loadSchools(finished: RequestCallBack) {
        request(.GET, URLString: MalaBaseUrl+schools, parameters: nil, finished: finished)
    }
}


// MARK: - Encapsulation Alamofire Framework
extension MalaNetworking {
    
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
            
            println(response.request!.cURLString)
            if response.result.isFailure {
                debugPrint("Network Request Failure - \(response.result.error)")
            }
            // Finished
            finished(result: response.result.value, error: response.result.error)
        }
    }
}


