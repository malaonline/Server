//
//  FilterCollectionView.swift
//  mala-ios
//
//  Created by Elors on 1/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class FilterView: UIScrollView {
    
    // MARK: - Property
    var grades: [GradeModel]? = []
    
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


// MARK: - Condition Object
class ConditionObject: NSObject {
    var grade: GradeModel = GradeModel()
    var subject: GradeModel = GradeModel()
    var tag: GradeModel = GradeModel()
    
    var gradeIndexPath = NSIndexPath(forItem: 0, inSection: 0)
    var subjectIndexPath = NSIndexPath(forItem: 0, inSection: 3)
    var tagIndexPath = NSIndexPath(forItem: 0, inSection: 4)
}
