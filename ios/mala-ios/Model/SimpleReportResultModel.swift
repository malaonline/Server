//
//  SimpleReportResultModel.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/23.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class SimpleReportResultModel: BaseObjectModel {

    // MARK: - Property
    /// 科目id
    var subject_id: Int = 0
    /// 支持情况
    var supported: Bool = false
    /// 是否报名
    var purchased: Bool = false
    /// 练习总数（可能为空）
    var total_nums: Int = 0
    /// 练习正确数（可能为空）
    var right_nums: Int = 0
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    override init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(subjectId: Int, supported: Bool, purchased: Bool, totalNums: Int, rightNums: Int) {
        self.init()
        self.subject_id = subjectId
        self.supported = supported
        self.purchased = purchased
        self.total_nums = totalNums
        self.right_nums = rightNums
    }
    
    
    // MARK: - Override
    override func setValue(value: AnyObject?, forUndefinedKey key: String) {
        println("SimpleReportResultModel - Set for UndefinedKey: \(key)")
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["subject_id", "supported", "total_nums", "right_nums"]
        return "\n"+dictionaryWithValuesForKeys(keys).description+"\n"
    }
}