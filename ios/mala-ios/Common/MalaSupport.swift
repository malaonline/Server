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


// MARK: - Compare
///  根据时间戳(优惠券有效期)判断优惠券是否过期
///
///  - parameter timeStamp: 有效期时间戳
///
///  - returns: Bool
func couponIsExpired(timeStamp: NSTimeInterval) -> Bool {
    let date = NSDate(timeIntervalSince1970: timeStamp)
    let result = NSDate().compare(date)
    
    switch result {
    // 有效期大于当前时间，未过期
    case .OrderedAscending:
        return false
        
    // 时间相同，已过期（考虑到后续操作所消耗的时间）
    case .OrderedSame:
        return true
        
    // 当前时间大于有效期，已过期
    case .OrderedDescending:
        return true
    }
}


///  根据支付方式获取AppURLScheme
///
///  - parameter channel: 支付手段
///
///  - returns: URLScheme
func getURLScheme(channel: MalaPaymentChannel) -> String {
    switch channel {
    case .Alipay:
        return MalaAppURLScheme.Alipay.rawValue
        
    case .Wechat :
        return MalaAppURLScheme.Wechat.rawValue
    }
}