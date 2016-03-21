//
//  CommentModel.swift
//  mala-ios
//
//  Created by 王新宇 on 3/21/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

public class CommentModel: BaseObjectModel {

    // MARK: - Property
    /// 课时数
    var timeslot: Int = 0
    /// 评分
    var score: Int = 0
    /// 评价内容
    var content: String = ""
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    override init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(id: Int, timeslot: Int, score: Int, content: String) {
        self.init()
        self.id = id
        self.timeslot = timeslot
        self.score = score
        self.content = content
    }
    
    // MARK: - Override
    override public func setValue(value: AnyObject?, forUndefinedKey key: String) {
        debugPrint("CommentModel - Set for UndefinedKey: \(key)")
    }
    
    
    // MARK: - Description
    override public var description: String {
        let keys = ["id", "timeslot", "score", "content"]
        return "\n"+dictionaryWithValuesForKeys(keys).description+"\n"
    }
}