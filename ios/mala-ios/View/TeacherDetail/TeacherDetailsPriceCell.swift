//
//  TeacherDetailsPriceCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class TeacherDetailsPriceCell: TeacherDetailsBaseCell {

    // MARK: - Variables
    var prices: [GradePriceModel] {
        didSet {
            tableView.prices = prices
            tableView.snp_updateConstraints { (make) -> Void in
                make.height.equalTo(Int(MalaLayout_DetailPriceTableViewCellHeight) * prices.count)
            }
        }
    }
    
    // MARK: - Components
    private lazy var tableView: TeacherDetailsPriceTableView = {
        let tableView = TeacherDetailsPriceTableView(frame: CGRectZero, style: .Plain)
        return tableView
    }()
    
    
    
    // MARK: - Life Cycle
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        prices = []
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        subTitle = "新生奖学金抵扣400元"
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    
    private func setupUserInterface() {
        
        // SubViews
        content.addSubview(tableView)
        
        // Autolayout
        // Remove margin
        content.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(self.title.snp_bottom)
            make.bottom.equalTo(self.contentView.snp_bottom)
        }
        
        tableView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.content.snp_top)
            make.left.equalTo(self.content.snp_left)
            make.bottom.equalTo(self.content.snp_bottom)
            make.right.equalTo(self.content.snp_right)
        }
        
    }
    
}