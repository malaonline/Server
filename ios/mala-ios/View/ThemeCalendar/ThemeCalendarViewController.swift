//
//  ThemeCalendarViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/4/8.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

private let reuseIdentifier = "Cell"

class ThemeCalendarViewController: UICollectionViewController {

    // MARK: - Property
    
    
    // MARK: - Components
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        self.collectionView!.registerClass(UICollectionViewCell.self, forCellWithReuseIdentifier: reuseIdentifier)

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }


}
