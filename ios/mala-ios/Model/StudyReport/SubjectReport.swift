//
//  SubjectReport.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/31.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class SubjectReport: NSObject {

    // MARK: - Property
    /// 学科id
    var subject_id: Int = 0
    /// 练习总数
    var total_nums: Int = 0
    /// 正确练习数
    var right_nums: Int = 0
    /// 累计答题次数
    var exercise_total_nums: Int = 0
    /// 累计答题完成数
    var exercise_fin_nums: Int = 0
    
    /// 错题分布
    var error_rates: [SingleHomeworkData] = []
    /// 练习量走势
    var month_trend: [SingleTimeIntervalData] = []
    /// 知识点正确率
    var knowledges_accuracy: [SingleTopicData] = []
    /// 能力结构分析
    var abilities: [SingleAbilityData] = []
    /// 提分点分析
    var score_analyses: [SingleTopicScoreData] = []
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    
    // MARK: - Override
    override func setValue(value: AnyObject?, forUndefinedKey key: String) {
        println("SubjectReport - Set for UndefinedKey: \(key)")
    }
    
    override func setValue(value: AnyObject?, forKey key: String) {
        ///  错题分布
        if key == "error_rates" {
            if let dicts = value as? [[String: AnyObject]] {
                var tempDict: [SingleHomeworkData] = []
                for dict in dicts {
                    let set = SingleHomeworkData(dict: dict)
                    tempDict.append(set)
                }
                error_rates = tempDict
            }
            return
        }
        /// 练习量走势
        if key == "month_trend" {
            if let dicts = value as? [[String: AnyObject]] {
                var tempDict: [SingleTimeIntervalData] = []
                for dict in dicts {
                    let set = SingleTimeIntervalData(dict: dict)
                    tempDict.append(set)
                }
                month_trend = tempDict
            }
            return
        }
        /// 知识点正确率
        if key == "knowledges_accuracy" {
            if let dicts = value as? [[String: AnyObject]] {
                var tempDict: [SingleTopicData] = []
                for dict in dicts {
                    let set = SingleTopicData(dict: dict)
                    tempDict.append(set)
                }
                knowledges_accuracy = tempDict
            }
            return
        }
        /// 能力结构分析
        if key == "abilities" {
            if let dicts = value as? [[String: AnyObject]] {
                var tempDict: [SingleAbilityData] = []
                for dict in dicts {
                    let set = SingleAbilityData(dict: dict)
                    tempDict.append(set)
                }
                abilities = tempDict
            }
            return
        }
        /// 提分点分析
        if key == "score_analyses" {
            if let dicts = value as? [[String: AnyObject]] {
                var tempDict: [SingleTopicScoreData] = []
                for dict in dicts {
                    let set = SingleTopicScoreData(dict: dict)
                    tempDict.append(set)
                }
                score_analyses = tempDict
            }
            return
        }
        
        super.setValue(value, forKey: key)
    }
}