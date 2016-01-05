//
//  TeacherDetailsBaseCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class TeacherDetailsBaseCell: UITableViewCell {

    // MARK: - Variables
    var title: String? {
        set {
            cellTitle.text = title
        }
        get {
            return cellTitle.text
        }
    }
    
    // MARK: - Components
    private lazy var cellTitle: UILabel = {
        let label = UILabel(title: MalaCommonString_Title)
        label.textColor = MalaDetailsCellTitleColor
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_21)
        return label
    }()
    
    
    // MARK: - Life Cycle
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUserInterface()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private
    private func setupUserInterface() {
        
        // SubViews
        addSubview(cellTitle)
        
        // Autolayout
        cellTitle.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_10)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_8)
            make.height.equalTo(MalaLayout_FontSize_21)
            make.width.equalTo(100)
        }
        
        
    }
    
    
}
