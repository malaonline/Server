//
//  ProfileElementModel.swift
//  mala-ios
//
//  Created by 王新宇 on 3/11/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class ProfileElementModel: BaseObjectModel {
    
    // MARK: - Property
    /// 标题
    var title: String = ""
    /// 信息
    var detail: String = ""
    /// 跳转控制器
    var controller: AnyClass?
    /// 跳转控制器标题
    var controllerTitle: String?
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    override init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(id: Int, title: String, detail: String, controller: AnyClass?, controllerTitle: String) {
        self.init()
        self.id = id
        self.title = title
        self.detail = detail
        self.controller = controller
        self.controllerTitle = controllerTitle
    }
    
    // MARK: - Override
    override func setValue(value: AnyObject?, forUndefinedKey key: String) {
        debugPrint("ProfileElementModel - Set for UndefinedKey: \(key)")
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["id", "title", "detail", "controller"]
        return dictionaryWithValuesForKeys(keys).description
    }
}