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
    var id: Int = 0
    /// 练习总数
    var totalNum: Int = 0
    /// 正确练习数
    var rightNum: Int = 0
    /// 累计答题次数
    var exerciseTotalNum: Int = 0
    /// 累计答题完成数
    var exerciseFinNum: Int = 0
    
    /// 错题分布
    var errorRates: [SingleHomeworkData] = []
    /// 练习量走势
    var monthTrend: [SingleTimeIntervalData] = []
    /// 知识点正确率
    var knowledgesAccuracy: [SingleTopicData] = []
    /// 能力结构分析
    var abilities: [SingleAbilityData] = []
    /// 提分点分析
    var scoreAnalyses: [SingleTopicScoreData] = []
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
}