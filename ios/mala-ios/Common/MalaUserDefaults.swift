//
//  MalaUserDefaults.swift
//  mala-ios
//
//  Created by 王新宇 on 2/24/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

// MARK: - Keys
let userAccessTokenKey = "userAccessTokenKey"

///  监听者
struct Listener<T>: Hashable {
    /// 监听者名称
    let name: String
    /// 触发事件
    typealias Action = T -> Void
    let action: Action
    
    var hashValue: Int {
        return name.hashValue
    }
}

/// 可监听变量
class Listenable<T> {
    /// 变量值
    var value: T {
        didSet {
            setterAction(value)
            for listener in listenerSet {
                listener.action(value)
            }
        }
    }
    
    /// 触发事件
    typealias SetterAction = T -> Void
    var setterAction: SetterAction
    // 监听者数组
    var listenerSet = Set<Listener<T>>()
    
    ///  构造方法
    ///
    ///  - parameter v:      value
    ///  - parameter action: trigger action
    ///
    ///  - returns: The created listenable.
    init(_ v: T, setterAction action: SetterAction) {
        value = v
        setterAction = action
    }
    ///  绑定监听
    func bindListener(name: String, action: Listener<T>.Action) {
        let listener = Listener(name: name, action: action)
        //
        listenerSet.insert(listener)
    }
    ///  绑定监听并执行
    func bindAndFireListener(name: String, action: Listener<T>.Action) {
        bindListener(name, action: action)
        
        action(value)
    }
    
    func removeListenerWithName(name: String) {
        for listener in listenerSet {
            if listener.name == name {
                listenerSet.remove(listener)
                break
            }
        }
    }
    
    func removeAllListeners() {
        listenerSet.removeAll(keepCapacity: false)
    }
}


// MARK: - MalaUserDefaults
class MalaUserDefaults {
    
    /// 单例
    static let defaults = NSUserDefaults(suiteName: MalaConfig.appGroupID)!
    /// 登陆标识
    static var isLogined: Bool {
        
        if let _ = MalaUserDefaults.userAccessToken.value {
            return true
        } else {
            return false
        }
    }
    /// 令牌
    static var userAccessToken: Listenable<String?> = {
        let userAccessToken = defaults.stringForKey(userAccessTokenKey)
        
        return Listenable<String?>(userAccessToken) { userAccessToken in
            defaults.setObject(userAccessToken, forKey: userAccessTokenKey)
            
            if let appDelegate = UIApplication.sharedApplication().delegate as? AppDelegate {
                
            }
        }
    }()
}
