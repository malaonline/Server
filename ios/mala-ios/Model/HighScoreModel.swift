//
//  HighScoreModel.swift
//  mala-ios
//
//  Created by Elors on 12/29/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class HighScoreModel: NSObject {

    // MARK: - Property
    var name: String?
    var increased_scores: Int = 0
    var school_name: String?
    var admitted_to: String?
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }

    init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(name: String, score: Int, school: String, admitted: String) {
        self.init()
        self.name = name
        self.increased_scores = score
        self.school_name = school
        self.admitted_to = admitted
    }
    
    
    // MARK: - Override
    override func setValue(value: AnyObject?, forUndefinedKey key: String) {
        debugPrint("HighScoreModel - Set for UndefinedKey: \(key)")
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["name", "increased_scores", "school_name", "admitted_to"]
        return dictionaryWithValuesForKeys(keys).description
    }
}
