//
//  ConditionObject.swift
//  mala-ios
//
//  Created by 王新宇 on 16/6/29.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class ConditionObject: NSObject {
    
    // MARK: - Property
    var grade: GradeModel = GradeModel()
    var subject: GradeModel = GradeModel()
    var tags: [BaseObjectModel] = []
    
    
    // MARK: - Public Method
    ///  根据筛选条件对象模型，返回条件字典
    ///
    ///  - returns: 条件字典
    func getParam() -> [String: AnyObject] {
        var param: [String: AnyObject] = [String: AnyObject]()
        
        // 过滤年级
        param["grade"] = grade.id
        // 过滤科目
        param["subject"] = subject.id
        // 过滤风格
        if tags.count != 0 {
            let tagsString: String = tags.reduce("", combine: { (string, model) -> String in
                let operation = (String(string) == "" ? "" : " ")
                return String(string)+operation+String(model.id)
            })
            param["tags"] = tagsString
        }
        return param
    }
    
    
    // MARK: - Override
    override var description: String {
        let tagsString = self.tags.map({ (object: BaseObjectModel) -> String in
            return object.name ?? ""
        })
        let string = String(
            format: "grade: %@, subject: %@ , tags: %@",
            self.grade.name ?? "",
            self.subject.name ?? "",
            tagsString.joinWithSeparator(" • ")
        )
        return string
    }
}