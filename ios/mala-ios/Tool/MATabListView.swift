//
//  MATabListView.swift
//  mala-ios
//
//  Created by Elors on 1/6/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

@objc protocol MATabListViewDelegate: NSObjectProtocol {
    optional func tagDidTap(sender: UILabel, tabListView: MATabListView)
    optional func tagShourldDisplayBorder(sender: UILabel, tabListView: MATabListView) -> Bool
}


private let RightPadding: CGFloat = 12.0
private let BottomMargin: CGFloat = 5.0

class MATabListView: UIView {

    // MARK: - Property
    weak var delegate: MATabListViewDelegate?
    var layoutHeight: CGFloat = 0
    private var previousFrame = CGRectZero
    private var totalHeight: CGFloat = 0
    private var tagCount = 0
    
    
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
            
            guard string != "" else {
                continue
            }
            
            let label = UILabel(frame: CGRectZero)
            label.textAlignment = .Left
            label.textColor = MalaColor_636363_0
            label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
            
            label.text = string
            var size = (string as NSString).sizeWithAttributes([NSFontAttributeName: UIFont.systemFontOfSize(MalaLayout_FontSize_14)])
            size.width += RightPadding
            
            var newRect = CGRectZero
            
            var x = CGRectGetMaxX(previousFrame)
            
            if let isDisplayBorder = delegate?.tagShourldDisplayBorder?(label, tabListView: self) where isDisplayBorder {
                
                size.height += 10
                x = CGRectGetMaxX(previousFrame) == 0 ? CGRectGetMaxX(previousFrame) : CGRectGetMaxX(previousFrame)+RightPadding
                
                label.layer.borderWidth = 1
                label.layer.borderColor = MalaColor_C4C4C4_0.CGColor
                label.layer.cornerRadius = 3
                label.layer.masksToBounds = true
                label.textAlignment = .Center
                label.userInteractionEnabled = true
                label.addGestureRecognizer(UITapGestureRecognizer(target: self, action: "labelDidTap:"))
                label.tag = tagCount
                tagCount += 1
            }
            
            // 如果当前行宽度不足够加入新的label
            if CGRectGetMaxX(previousFrame) + size.width > self.bounds.size.width {
                newRect.origin = CGPoint(x: 0, y: previousFrame.origin.y + size.height + BottomMargin)
                totalHeight += size.height + BottomMargin
            }else {
                newRect.origin = CGPoint(x: x, y: previousFrame.origin.y)
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
    
    
    // MARK: - Event Response
    @objc private func labelDidTap(gesture: UITapGestureRecognizer) {
        
        guard let label = gesture.view as? UILabel else {
            return
        }
        delegate?.tagDidTap?(label, tabListView: self)
    }
}