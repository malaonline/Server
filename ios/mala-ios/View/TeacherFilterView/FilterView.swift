//
//  FilterCollectionView.swift
//  mala-ios
//
//  Created by Elors on 1/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class FilterView: UIScrollView {
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        configuration()
        setupUserInterface()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func configuration() {
        self.pagingEnabled = true
    }
    
    private func setupUserInterface() {
        
    }
}
