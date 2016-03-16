//
//  MATabListView.swift
//  mala-ios
//
//  Created by Elors on 1/6/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let RightPadding: CGFloat = 12.0
private let BottomMargin: CGFloat = 5.0

class MATabListView: UIView {

    // MARK: - Property
    var layoutHeight: CGFloat = 0
    private var previousFrame = CGRectZero
    private var totalHeight: CGFloat = 0
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        totalHeight = 0
        self.frame = frame
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - API
    func setTags(tags: [String]?) {
        
        previousFrame = CGRectZero
        for string in tags ?? [] {
            let label = UILabel(frame: CGRectZero)
            label.textAlignment = .Left
            label.textColor = MalaColor_636363_0
            label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
            label.text = string
            var size = (string as NSString).sizeWithAttributes([NSFontAttributeName: UIFont.systemFontOfSize(MalaLayout_FontSize_14)])
            size.width += RightPadding
            
            var newRect = CGRectZero
            
            // 如果当前行宽度不足够加入新的label
            if CGRectGetMaxX(previousFrame) + size.width > self.bounds.size.width {
                newRect.origin = CGPoint(x: 0, y: previousFrame.origin.y + size.height + BottomMargin)
                totalHeight += size.height + BottomMargin
            }else {
                newRect.origin = CGPoint(x: CGRectGetMaxX(previousFrame), y: previousFrame.origin.y)
            }
            
            newRect.size = size
            label.frame = newRect
            previousFrame = label.frame
            self.addSubview(label)
            self.setHeight(totalHeight + size.height)
        }
    }
    
    
    // MARK: - Private Method
    private func setHeight(height: CGFloat) {
        self.layoutHeight = height
        self.snp_updateConstraints { (make) -> Void in
            make.height.equalTo(self.layoutHeight)
        }
    }
}