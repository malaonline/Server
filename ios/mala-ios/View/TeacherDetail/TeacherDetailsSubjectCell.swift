//
//  TeacherDetailsSubjectCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class TeacherDetailsSubjectCell: MalaBaseCell {

    // MARK: - Property
    /// 授课年级字符串数据
    var gradeStrings: [String] = [] {
        didSet {
            
            if gradeStrings != oldValue {
                
                var elementarySchools = [String]()
                var juniorSchools = [String]()
                var seniorSchools = [String]()
                
                
                for gradeName in gradeStrings {
                    
                    // 过滤无用数据
                    if gradeName == "小学" || gradeName == "初中" || gradeName == "高中" {
                        continue
                    }
                    
                    // 截取首个字符
                    let firstCharacter = gradeName.substringToIndex(gradeName.startIndex.advancedBy(1))
                    
                    // 根据字符分隔显示
                    if firstCharacter == "高" {
                        seniorSchools.append(gradeName)
                    }else if firstCharacter == "初" {
                        juniorSchools.append(gradeName)
                    }else {
                        elementarySchools.append(gradeName)
                    }
                }

                // 排序
                elementarySchools.sortInPlace()
                juniorSchools.sortInPlace()
                seniorSchools.sortInPlace()
                
                // 添加label
                elementarySchool.setTags(elementarySchools)
                juniorSchool.setTags(juniorSchools)
                seniorSchool.setTags(seniorSchools)
            }
        }
    }
    
    // MARK: - Components
    /// 小学
    private lazy var elementarySchool: MATabListView = {
        let elementarySchool = MATabListView()
        return elementarySchool
    }()
    /// 初中
    private lazy var juniorSchool: MATabListView = {
        let juniorSchool = MATabListView()
        return juniorSchool
    }()
    /// 高中
    private lazy var seniorSchool: MATabListView = {
        let elementarySchool = MATabListView()
        return elementarySchool
    }()
    
    
    // MARK: - Constructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        setupUserInterface()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        
        
        // SubViews
        content.addSubview(elementarySchool)
        content.addSubview(juniorSchool)
        content.addSubview(seniorSchool)
        
        // Autolayout
        elementarySchool.snp_makeConstraints { (make) in
            make.left.equalTo(content.snp_left)
            make.right.equalTo(content.snp_right)
            make.top.equalTo(content.snp_top)
        }
        juniorSchool.snp_makeConstraints { (make) in
            make.left.equalTo(content.snp_left)
            make.right.equalTo(content.snp_right)
            make.top.equalTo(elementarySchool.snp_bottom).offset(MalaLayout_Margin_12)
        }
        seniorSchool.snp_makeConstraints { (make) in
            make.left.equalTo(content.snp_left)
            make.right.equalTo(content.snp_right)
            make.top.equalTo(juniorSchool.snp_bottom).offset(MalaLayout_Margin_12)
            make.bottom.equalTo(content.snp_bottom)
        }
    }
}