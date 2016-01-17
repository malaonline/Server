//
//  StyleFilterView.swift
//  mala-ios
//
//  Created by Elors on 1/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class StyleFilterView: UICollectionView {

    // MARK: - Constructed
    override init(frame: CGRect, collectionViewLayout layout: UICollectionViewLayout) {
        super.init(frame: frame, collectionViewLayout: layout)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func loadTags() {
        // 获取风格标签数据
        NetworkTool.sharedTools.loadTags{ [weak self] (result, error) -> () in
            if error != nil {
                debugPrint("TeacherFilterView - loadTags Request Error")
                return
            }
            guard let dict = result as? [String: AnyObject] else {
                debugPrint("TeacherFilterView - loadTags Format Error")
                return
            }
            
            var dataDict: [GradeModel]? = []
            tempArray = ResultModel(dict: dict).results
            for object in tempArray! {
                if let dict = object as? [String: AnyObject] {
                    let set = GradeModel(dict: dict)
                    tempDict?.append(set)
                }
            }
            self?.reloadData()
        }
    }
}
