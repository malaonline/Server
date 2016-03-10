//
//  ProfileViewCell.swift
//  mala-ios
//
//  Created by 王新宇 on 3/10/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class ProfileViewCell: UITableViewCell {

    // MARK: - Property
    /// Cell标题
    var title: String = "" {
        didSet {
            self.textLabel?.text = title
        }
    }
    /// Cell信息
    var detail: String = "" {
        didSet {
            self.detailTextLabel?.text = detail
        }
    }
    

    // MARK: - Constructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: .Value1, reuseIdentifier: reuseIdentifier)
        
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    
    // MARK: - Private Metho
    private func setupUserInterface() {
        // Style
        self.accessoryType = .DisclosureIndicator
        
        self.textLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        self.textLabel?.textColor = MalaDetailsCellLabelColor
        
        self.detailTextLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        self.detailTextLabel?.textColor = MalaProfileCellDetailTextColor
    }
}
