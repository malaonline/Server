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
let UserIDKey = "UserIDKey"
let ParentIDKey = "ParentIDKey"
let ProfileIDKey = "ProfileIDKey"
let FirstLoginKey = "FirstLoginKey"

/// 已经登出标记 - 由于Listenable的Value不可为nil。
/// 所以每次注销后accessToken仍然会保存用户的Token, 导致isLogined返回结果不正确
var isLogouted = false

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
        // [用户Token为空] 或 [已经注销] 均判断为未登录情况
        if let _ = MalaUserDefaults.userAccessToken.value where !isLogouted {
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
    /// 用户id
    static var userID: Listenable<Int?> = {
        let userID = defaults.integerForKey(UserIDKey)
        
        return Listenable<Int?>(userID) { userID in
            defaults.setObject(userID, forKey: UserIDKey)
        }
    }()
    /// 家长id
    static var parentID: Listenable<Int?> = {
        let parentID = defaults.integerForKey(ParentIDKey)
        
        return Listenable<Int?>(parentID) { userID in
            defaults.setObject(userID, forKey: ParentIDKey)
        }
    }()
    /// 个人资料id
    static var profileID: Listenable<Int?> = {
        let profileID = defaults.integerForKey(ProfileIDKey)
        
        return Listenable<Int?>(profileID) { userID in
            defaults.setObject(userID, forKey: ProfileIDKey)
        }
    }()
    /// 登陆标示（以是否已填写学生姓名区分）
    static var firstLogin: Listenable<Bool?> = {
        let firstLogin = defaults.boolForKey(FirstLoginKey)
        
        return Listenable<Bool?>(firstLogin) { userID in
            defaults.setObject(userID, forKey: FirstLoginKey)
        }
    }()
    
    /// 清空UserDefaults
    class func cleanAllUserDefaults() {
        
        userAccessToken.removeAllListeners()
        userID.removeAllListeners()
        parentID.removeAllListeners()
        profileID.removeAllListeners()
        firstLogin.removeAllListeners()
        
        defaults.removeObjectForKey(userAccessTokenKey)
        defaults.removeObjectForKey(UserIDKey)
        defaults.removeObjectForKey(ParentIDKey)
        defaults.removeObjectForKey(ProfileIDKey)
        defaults.removeObjectForKey(FirstLoginKey)
        
        // 配置清空成功表示注销成功
        isLogouted = defaults.synchronize()
    }
}