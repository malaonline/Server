//
//  MalaSupport.swift
//  mala-ios
//
//  Created by 王新宇 on 2/25/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import Foundation

// MARK: - Task
typealias CancelableTask = (cancel: Bool) -> Void

///  延迟执行任务
///
///  - parameter time: 延迟秒数(s)
///  - parameter work: 任务闭包
///
///  - returns: 任务对象闭包
func delay(time: NSTimeInterval, work: dispatch_block_t) -> CancelableTask? {
    
    var finalTask: CancelableTask?
    
    let cancelableTask: CancelableTask = { cancel in
        if cancel {
            finalTask = nil // key
            
        } else {
            dispatch_async(dispatch_get_main_queue(), work)
        }
    }
    
    finalTask = cancelableTask
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, Int64(time * Double(NSEC_PER_SEC))), dispatch_get_main_queue()) {
        if let task = finalTask {
            task(cancel: false)
        }
    }
    
    return finalTask
}

func cancel(cancelableTask: CancelableTask?) {
    cancelableTask?(cancel: true)
}


// MARK: - unregister

///  注销推送消息
func unregisterThirdPartyPush() {
    dispatch_async(dispatch_get_main_queue()) {
        //TODO: 注销推送消息
        UIApplication.sharedApplication().applicationIconBadgeNumber = 0
    }
}

///  清空缓存
func cleanCaches() {
    
}


// MARK: - Alert Message
func showSaveResult(viewController: UIViewController?, result: Bool, property: String) {
    guard viewController != nil else {
        return
    }
    
    dispatch_async(dispatch_get_main_queue()) { () -> Void in
        let string = result ? "成功" : "失败"
        _ = JSSAlertView().show(viewController!,
            title: String(format: "%@保存%@",
                property, string)
        )
    }
}