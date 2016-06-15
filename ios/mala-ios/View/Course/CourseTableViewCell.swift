//
//  CourseTableViewCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/6/14.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class CourseTableViewCell: UITableViewCell {

    
    // MARK: - Property
    var model: [StudentCourseModel]? {
        didSet {
            if let courseModel = model?[0] {
                dateLabel.text = getDateString(courseModel.start, format: "d")
                weekLabel.text = getWeekString(courseModel.start)
            }
            
            for courseModel in model ?? [] {
                
                let view = SingleCourseView()
                view.model = courseModel
                
                courseLayoutView.addSubview(view)
                
                if let lastCourseView = lastCourseView {
                    view.snp_makeConstraints { (make) in
                        make.top.equalTo(lastCourseView.snp_bottom)
                        make.left.equalTo(courseLayoutView.snp_left)
                        make.right.equalTo(courseLayoutView.snp_right)
                        make.height.equalTo(102)
                    }
                }else {
                    view.snp_makeConstraints { (make) in
                        make.top.equalTo(courseLayoutView.snp_top)
                        make.left.equalTo(courseLayoutView.snp_left)
                        make.right.equalTo(courseLayoutView.snp_right)
                        make.height.equalTo(102)
                    }
                }
                lastCourseView = view
            }
        }
    }
    var lastCourseView: SingleCourseView?
    
    
    // MARK: - Components
    /// 日期标签
    private lazy var  dateLabel: UILabel = {
        let label = UILabel(
            text: "1",
            fontSize: 24,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    /// 星期标签
    private lazy var  weekLabel: UILabel = {
        let label = UILabel(
            text: "周一",
            fontSize: 14,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    /// 课程布局视图
    private lazy var courseLayoutView: UIView = {
        let view = UIView()
        return view
    }()
    
    
    
    // MARK: - Instance Method
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
        selectionStyle = .None
        
        // SubViews
        contentView.addSubview(dateLabel)
        contentView.addSubview(weekLabel)
        contentView.addSubview(courseLayoutView)
        
        // AutoLayout
        dateLabel.snp_makeConstraints { (make) in
            make.top.equalTo(self.contentView.snp_top).offset(20)
            make.left.equalTo(self.contentView.snp_left).offset(20)
            make.height.equalTo(27)
        }
        weekLabel.snp_makeConstraints { (make) in
            make.top.equalTo(dateLabel.snp_bottom).offset(5)
            make.left.equalTo(dateLabel.snp_left)
            make.width.equalTo(30)
            make.height.equalTo(14)
        }
        courseLayoutView.snp_makeConstraints { (make) in
            make.top.equalTo(dateLabel.snp_top)
            make.left.equalTo(weekLabel.snp_right).offset(20)
            make.right.equalTo(self.contentView.snp_right).offset(-12)
            make.bottom.equalTo(self.contentView.snp_bottom)
        }
    }
    
    
    override func prepareForReuse() {
        super.prepareForReuse()
        
        lastCourseView = nil
        
        for view in courseLayoutView.subviews {
            view.removeFromSuperview()
        }
    }
}