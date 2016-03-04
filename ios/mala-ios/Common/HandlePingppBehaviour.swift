//
//  HandlePingppBehaviour.swift
//  mala-ios
//
//  Created by 王新宇 on 3/4/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class HandlePingppBehaviour: NSObject {

    
    func handleResult(result: String, error: PingppError?, currentViewController: UIViewController?) {
        
        guard currentViewController != nil else {
            println("handlePing++Result - controller==nil")
            return
        }
        
        // result : success, fail, cancel, invalid
        if error == nil {
            println("PingppError is nil")
        }else {
            println("PingppError: code=\(error!.code), msg=\(error!.getMsg())")
        }
        
        if result == "success" {
            println("HandlePingpp - success")
            
        }else if result == "cancel" {
            println("HandlePingpp - cancel")
            
        }else if result == "fail" {
            println("HandlePingpp - fail")
            
        }
    }
}
