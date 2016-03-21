//
//  CourseInfoView.swift
//  mala-ios
//
//  Created by 王新宇 on 3/21/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class CourseInfoView: UIView {

    // MARK: - Property
    /// 课程数据模型
    var model: CourseModel? {
        didSet {
            subjectString.text = model?.subject
            schoolString.attributedText = getLineSpacingAttrString(model?.school ?? "")
            classtimeString.text = String(format: "%@-%@", getTimeString(model?.start ?? 0), getTimeString(model?.end ?? 0))
        }
    }
    
    
    // MARK: - Components
    /// 学习科目label
    private lazy var subjectLabel: UILabel = {
        let subjectLabel = UILabel()
        subjectLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        subjectLabel.textColor = MalaColor_939393_0
        subjectLabel.text = "学习科目:"
        return subjectLabel
    }()
    /// 科目
    private lazy var subjectString: UILabel = {
        let subjectString = UILabel()
        subjectString.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        subjectString.textColor = MalaColor_939393_0
        return subjectString
    }()
    /// 上课地点label
    private lazy var schoolLabel: UILabel = {
        let schoolLabel = UILabel()
        schoolLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        schoolLabel.textColor = MalaColor_939393_0
        schoolLabel.numberOfLines = 0
        schoolLabel.text = "上课地点:"
        return schoolLabel
    }()
    /// 地址
    private lazy var schoolString: UILabel = {
        let schoolString = UILabel()
        schoolString.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        schoolString.textColor = MalaColor_939393_0
        schoolString.numberOfLines = 0
        return schoolString
    }()
    /// 上课时间label
    private lazy var classtimeLabel: UILabel = {
        let classtimeLabel = UILabel()
        classtimeLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        classtimeLabel.textColor = MalaColor_939393_0
        classtimeLabel.text = "上课时间:"
        return classtimeLabel
    }()
    /// 上课时间
    private lazy var classtimeString: UILabel = {
        let classtimeString = UILabel()
        classtimeString.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        classtimeString.textColor = MalaColor_939393_0
        return classtimeString
    }()
    /*
    /// 剩余课时label
    private lazy var residualLabel: UILabel = {
        let residualLabel = UILabel()
        residualLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        residualLabel.textColor = MalaColor_939393_0
        residualLabel.text = "剩余课时:"
        return residualLabel
    }()
    /// 剩余课时
    private lazy var residualString: UILabel = {
        let residualString = UILabel()
        residualString.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        residualString.textColor = MalaColor_939393_0
        return residualString
    }()
    */
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private
    private func setupUserInterface() {
        // Style
        backgroundColor = UIColor.whiteColor()
        
        // SubViews
        addSubview(subjectLabel)
        addSubview(subjectString)
        
        addSubview(schoolLabel)
        addSubview(schoolString)
        
        addSubview(classtimeLabel)
        addSubview(classtimeString)
        /*
        addSubview(residualLabel)
        addSubview(residualString)
        */
        
        // Autolayout
        subjectLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.snp_top).offset(MalaLayout_Margin_12)
            make.left.equalTo(self.snp_left)
            make.width.equalTo(65)
            make.height.equalTo(MalaLayout_FontSize_13)
        }
        subjectString.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(subjectLabel.snp_top)
            make.left.equalTo(subjectLabel.snp_right).offset(MalaLayout_Margin_8)
            make.right.equalTo(self.snp_right)
            make.height.equalTo(MalaLayout_FontSize_13)
        }
        
        schoolLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(subjectLabel.snp_bottom).offset(MalaLayout_Margin_22)
            make.left.equalTo(self.snp_left)
            make.height.equalTo(MalaLayout_FontSize_13)
            make.width.equalTo(65)
        }
        schoolString.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(schoolLabel.snp_top)
            make.left.equalTo(schoolLabel.snp_right).offset(MalaLayout_Margin_8)
            make.right.equalTo(self.snp_right)
        }
        
        classtimeLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(schoolString.snp_bottom).offset(MalaLayout_Margin_22)
            make.left.equalTo(self.snp_left)
            make.height.equalTo(MalaLayout_FontSize_13)
            make.width.equalTo(65)
        }
        classtimeString.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(classtimeLabel.snp_top)
            make.left.equalTo(classtimeLabel.snp_right).offset(MalaLayout_Margin_8)
            make.right.equalTo(self.snp_right)
            make.height.equalTo(MalaLayout_FontSize_13)
        }
        /*
        residualLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(classtimeLabel.snp_bottom).offset(MalaLayout_Margin_22)
            make.left.equalTo(self.snp_left)
            make.height.equalTo(MalaLayout_FontSize_13)
            make.width.equalTo(65)
        }
        residualString.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(residualLabel.snp_top)
            make.left.equalTo(residualLabel.snp_right).offset(MalaLayout_Margin_8)
            make.right.equalTo(self.snp_right)
            make.height.equalTo(MalaLayout_FontSize_13)
        }
        */
    }
}