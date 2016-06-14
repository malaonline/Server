//
//  CourseTableViewSectionHeader.swift
//  mala-ios
//
//  Created by 王新宇 on 16/6/14.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class CourseTableViewSectionHeader: UITableViewHeaderFooterView {

    // MARK: - Constructed
    override init(reuseIdentifier: String?) {
        super.init(reuseIdentifier: reuseIdentifier)
        
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private
    private func setupUserInterface() {
        
    }
}