//
//  ClassScheduleViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 3/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let ClassScheduleViewCellReuseID = "ClassScheduleViewCellReuseID"
private let ClassScheduleViewHeaderReuseID = "ClassScheduleViewHeaderReuseID"

public class ClassScheduleViewController: PDTSimpleCalendarViewController, PDTSimpleCalendarViewDelegate {

//    override 
    
    // MARK: - Life Cycle
    override public func viewDidLoad() {
        super.viewDidLoad()

        configure()
    }

    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    

    // MARK: - Private Method
    private func configure() {
        delegate = self
        weekdayHeaderEnabled = true
        
        collectionView?.registerClass(ClassScheduleViewCell.self, forCellWithReuseIdentifier: ClassScheduleViewCellReuseID)
        collectionView?.registerClass(ClassScheduleViewHeader.self, forSupplementaryViewOfKind: UICollectionElementKindSectionHeader, withReuseIdentifier: ClassScheduleViewHeaderReuseID)
    }
    
    
    // MARK: - DataSource
    
    
    
    // MARK: - Delegate
}