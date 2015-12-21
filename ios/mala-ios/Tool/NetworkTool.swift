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

    // MARK: - Variable
    private let baseUrl = "http://dev.malalaoshi.com/api/v1"
    private let gradeList = "/grades"
    private let TagList = "/tags"
    
    // Result Closure
    typealias RequestCallBack = (result: AnyObject?, error: NSError?)->()
    
    // Singleton
    static let sharedTools = NetworkTool()
    
}


// MARK: - Request Function
extension NetworkTool {
    
    ///  Request for GradeList
    ///
    ///  - parameter finished: Closure for Finished
    func loadGrades(finished: RequestCallBack) {
        request(.GET, URLString: baseUrl+gradeList, parameters: nil, finished: finished)
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


