//
//  TeacherDetailsSubjectCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class TeacherDetailsSubjectCell: TeacherDetailBaseCell {

    // MARK: - Property
    /// 授课年级字符串数据
    var gradeStrings: [String] = [] {
        didSet {
            
            if gradeStrings != oldValue {
                
                var elementarySchools = [String](count: 6, repeatedValue: "")
                var juniorSchools = [String](count: 3, repeatedValue: "")
                var seniorSchools = [String](count: 3, repeatedValue: "")
                
                
                for gradeName in gradeStrings {
                    
                    // 过滤无用数据
                    if gradeName == "小学" || gradeName == "初中" || gradeName == "高中" {
                        continue
                    }
                    
                    // 用年级名称获取对应下标
                    guard let index = MalaConfig.malaGradeShortName()[gradeName] else {
                        return
                    }
                    
                    // 截取首个字符
                    let firstCharacter = gradeName.substringToIndex(gradeName.startIndex.advancedBy(1))
                    
                    // 根据字符分隔显示
                    if firstCharacter == "高" {
                        seniorSchools[index] = gradeName
                    }else if firstCharacter == "初" {
                        juniorSchools[index] = gradeName
                    }else {
                        elementarySchools[index] = gradeName
                    }
                }
                
                println("小学年级数据 -> \(elementarySchools) \n")
                println("初中年级数据 -> \(juniorSchools) \n")
                println("高中年级数据 -> \(seniorSchools) \n")
                
                // 添加label
                self.setupTags(elementarySchool, strings: &elementarySchools)
                
                let height = (MalaScreenWidth <= 375 && elementarySchools.count > 4) ? 55 : 25
                elementarySchool.snp_updateConstraints { (make) in
                    make.height.equalTo(height)
                }
                
                self.setupTags(juniorSchool, strings: &juniorSchools)
                self.setupTags(seniorSchool, strings: &seniorSchools)
            }
        }
    }
    
    // MARK: - Components
    /// 小学
    private lazy var elementarySchool: ThemeTagListView = {
        let tagsView = ThemeTagListView()
        tagsView.imageName = "detail_class1"
        tagsView.labelBackgroundColor = MalaColor_F9B7B7_0
        tagsView.textColor = MalaColor_E25C5C_0
        return tagsView
    }()
    /// 初中
    private lazy var juniorSchool: ThemeTagListView = {
        let tagsView = ThemeTagListView()
        tagsView.imageName = "detail_class2"
        tagsView.labelBackgroundColor = MalaColor_B1D8F3_0
        tagsView.textColor = MalaColor_2B7BB4_0
        return tagsView
    }()
    /// 高中
    private lazy var seniorSchool: ThemeTagListView = {
        let tagsView = ThemeTagListView()
        tagsView.imageName = "detail_class3"
        tagsView.labelBackgroundColor = MalaColor_BFE7CA_0
        tagsView.textColor = MalaColor_259746_0
        return tagsView
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
            make.height.equalTo(25)
        }
        juniorSchool.snp_makeConstraints { (make) in
            make.left.equalTo(content.snp_left)
            make.right.equalTo(content.snp_right)
            make.top.equalTo(elementarySchool.snp_bottom).offset(12)
            make.height.equalTo(25)
        }
        seniorSchool.snp_makeConstraints { (make) in
            make.left.equalTo(content.snp_left)
            make.right.equalTo(content.snp_right)
            make.top.equalTo(juniorSchool.snp_bottom).offset(12)
            make.height.equalTo(25)
            make.bottom.equalTo(content.snp_bottom)
        }
    }
    
    private func setupTags(tagsView: ThemeTagListView, inout strings: [String]) {
        
        // 判断转入数组的所有元素是否都为空字符串
        // 是，将移除对应控件。
        // 否，将添加字符串到对应控件中
        var isEmpty = ""
        
        for string in strings {
            isEmpty += string
            if string == "", let index = strings.indexOf(string) {
                strings.removeAtIndex(index)
            }
        }
        
        if isEmpty == "" {
            tagsView.removeFromSuperview()
        }
        
        
        // 根据8种页面状态调整UI
        switch (elementarySchool.superview, juniorSchool.superview, seniorSchool.superview) {
            
        case (nil, nil, nil):
            // 均已移除不做处理
            break
        case (_, nil, nil):
            
            elementarySchool.snp_updateConstraints(closure: { (make) in
                make.bottom.equalTo(content.snp_bottom)
            })
            
            break
        case (nil, _, nil):
            
            juniorSchool.snp_updateConstraints(closure: { (make) in
                make.top.equalTo(content.snp_top)
                make.bottom.equalTo(content.snp_bottom)
            })
            
            break
        case (nil, nil, _):
            
            seniorSchool.snp_updateConstraints(closure: { (make) in
                make.top.equalTo(content.snp_top)
                make.bottom.equalTo(content.snp_bottom)
            })
            
            break
        case (_, _, nil):
            
            juniorSchool.snp_updateConstraints(closure: { (make) in
                make.bottom.equalTo(content.snp_bottom)
            })
            
            break
        case (_, nil, _):
            
            seniorSchool.snp_updateConstraints(closure: { (make) in
                make.top.equalTo(elementarySchool.snp_bottom).offset(12)
            })
            
            break
        case (nil, _, _):
            
            juniorSchool.snp_updateConstraints(closure: { (make) in
                make.top.equalTo(content.snp_top)
            })
            
            break
        case (_, _, _):
            // 均存在使用默认设置
            break
        }

        if isEmpty != "" {
            tagsView.labels = strings
        }
    }
}