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
            self.tableView.prices = prices
        }
    }
    
    // MARK: - Components
    private lazy var tableView: TeacherDetailsPriceTableView = {
        let tableView = TeacherDetailsPriceTableView(frame: CGRectZero, style: .Plain)
        
        return tableView
    }()
    
    
    
    // MARK: - Life Cycle
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        self.prices = []
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        self.subTitle = "新生奖学金抵扣400元"
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

}