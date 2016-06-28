//
//  TeacherDetailBaseCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/6/27.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class TeacherDetailBaseCell: UITableViewCell {

    // MARK: - Property
    /// 标题文字
    var title: String? {
        didSet {
            titleLabel.text = title
        }
    }
    /// 标签字符串数组
    var labels: [String] = [] {
        didSet {
            if labels != oldValue {
                for view in self.tagsView.subviews {
                    view.removeFromSuperview()
                }
                self.tagsView.setTags(labels)
            }
        }
    }
    
    
    // MARK: - Components
    /// 标题标签
    lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "",
            fontSize: 14,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    /// 真正的控件容器，若有需求要添加新的子控件，请添加于此内部（注意区别于 UITableViewCell 中的 contentView）
    lazy var content: UIView = UIView()
    /// 头部视图
    private lazy var headerView: UIView = {
        let view = UIView()
        view.backgroundColor = MalaColor_F6F6F6_96
        return view
    }()
    /// 标签容器
    lazy var tagsView: MATabListView = {
        let tagsView = MATabListView(frame: CGRect(x: 0, y: 0, width: UIScreen.mainScreen().bounds.size.width, height: 12))
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
    
    
    // MARK: - Private
    private func setupUserInterface() {
        // Style
//        content.backgroundColor = UIColor.orangeColor()
        
        // SubViews
        contentView.addSubview(headerView)
        contentView.addSubview(content)
        content.addSubview(tagsView)
        headerView.addSubview(titleLabel)
        
        // Autolayout
        headerView.snp_makeConstraints { (make) in
            make.top.equalTo(contentView.snp_top)
            make.height.equalTo(34)
            make.left.equalTo(contentView.snp_left)
            make.right.equalTo(contentView.snp_right)
        }
        content.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(headerView.snp_bottom).offset(15)
            make.left.equalTo(contentView.snp_left).offset(12)
            make.right.equalTo(contentView.snp_right).offset(-12)
            make.bottom.equalTo(contentView.snp_bottom).offset(-15)
        }
        titleLabel.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(headerView.snp_centerY)
            make.left.equalTo(headerView.snp_left).offset(12)
            make.height.equalTo(14)
        }
        tagsView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.content.snp_top)
            make.left.equalTo(self.content.snp_left)
            make.bottom.equalTo(self.content.snp_bottom)
            make.right.equalTo(self.content.snp_right)
        }
    }
    
    @objc func accessoryViewDidTap(sender: UIButton) {
        
    }
}