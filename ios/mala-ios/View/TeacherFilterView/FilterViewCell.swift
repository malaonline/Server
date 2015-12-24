//
//  FilterViewCell.swift
//  mala-ios
//
//  Created by Elors on 12/23/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

public class FilterViewCell: UICollectionViewCell {
    
    // MARK: - Variables
    var indexPath = NSIndexPath(forItem: 0, inSection: 0)
    var model: GradeModel {
        didSet{
            self.titleLabel.text = model.name
            self.titleLabel.textAlignment = .Center
            self.titleLabel.center = self.contentView.center
            self.tag = model.id
        }
    }
    override public var selected: Bool {
        didSet {
            if selected {
                self.titleLabel.backgroundColor = UIColor.redColor()
            }else {
                self.titleLabel.backgroundColor = UIColor.lightGrayColor()
            }
        }
    }
    
    
    // MARK: - Components
    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(18)
        label.textColor = UIColor.whiteColor()
        label.text = "筛选"
        label.sizeToFit()
        return label
    }()
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        self.model = GradeModel()
        super.init(frame: frame)
        titleLabel.frame.size.width = self.bounds.size.width*0.75
        titleLabel.layer.cornerRadius = titleLabel.frame.height*0.5
        titleLabel.clipsToBounds = true
        addSubview(titleLabel)
    }
    
    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
}
