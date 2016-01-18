//
//  FilterViewCell.swift
//  mala-ios
//
//  Created by Elors on 12/23/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

public class FilterViewCell: UICollectionViewCell {
    
    // MARK: - Property
    /// Cell所属indexPath
    var indexPath = NSIndexPath(forItem: 0, inSection: 0)
    /// 筛选条件数据模型
    var model: GradeModel = GradeModel() {
        didSet{
            self.button.setTitle(model.name, forState: .Normal)
            self.tag = model.id
        }
    }
    /// 选中状态
    override public var selected: Bool {
        didSet {
            button.selected = selected
        }
    }
    
    
    // MARK: - Components
    private lazy var button: UIButton = {
        let button = UIButton()
        button.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        button.titleLabel?.textAlignment = .Center
        button.setTitleColor(MalaDetailsCellSubTitleColor, forState: .Normal)
        button.setTitle("小学一年级", forState: .Normal)
        button.setImage(UIImage(named: "radioButton_normal"), forState: .Normal)
        button.setImage(UIImage(named: "radioButton_selected"), forState: .Selected)
        button.titleEdgeInsets = UIEdgeInsets(top: 0, left: 12, bottom: 0, right: -12)
        button.sizeToFit()
        // 冻结按钮交互功能，其只作为视觉显示效果使用
        button.userInteractionEnabled = false
        return button
    }()

    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUserInterface()
    }
    
    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // SubViews
        contentView.addSubview(button)
        
        // AutoLayout
        button.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(self.contentView.snp_centerY)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_4)
        }
    }
}
