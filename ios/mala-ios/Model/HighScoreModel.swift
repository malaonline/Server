//
//  HighScoreModel.swift
//  mala-ios
//
//  Created by Elors on 12/29/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class HighScoreModel: NSObject {

    // MARK: - Variables
    var name: String?
    var increased_scores: Int?
    var school_name: String?
    var admitted_to: String?
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    convenience init(name: String, score: Int, school: String, admitted: String) {
        self.init()
        self.name = name
        self.increased_scores = score
        self.school_name = school
        self.admitted_to = admitted
    }
    
    init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
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
