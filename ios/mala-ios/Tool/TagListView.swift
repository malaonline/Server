//
//  ListView.swift
//
//
//  Created by Oskar Zhang on 7/13/15.
//  Copyright (c) 2015 Oskar Zhang. All rights reserved.
//

import Foundation
import UIKit
class TagListView:UIScrollView
{
    /// 标签字符串数据
    var labels: [String] = [] {
        didSet {
            if labels != oldValue {
                setupLabels()
            }
        }
    }
    
    var numberOfRows = 0
    var currentRow = 0
    var tags = [UILabel]()
    var hashtagsOffset: UIEdgeInsets = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
    /// 行高
    var rowHeight: CGFloat = 30
    /// 水平间隔
    var tagHorizontalPadding: CGFloat = 5.0
    /// 垂直间隔
    var tagVerticalPadding: CGFloat = 5.0
    /// 文字左右边界间隔
    var tagCombinedMargin: CGFloat = 10.0
    /// 默认标签背景色
    var labelBackgroundColor: UIColor = UIColor.lightGrayColor()
    /// 默认文字颜色
    var textColor: UIColor = UIColor.whiteColor()
    /// 图标名称
    var iconName: String?
    
    
    // MARK: - Instance Method
    override init(frame:CGRect) {
        super.init(frame: frame)
        numberOfRows = Int(frame.height / rowHeight)
        self.showsVerticalScrollIndicator = false
        self.scrollEnabled = true
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    /// 批量设置标签控件
    func setupLabels() {
        self.reset()
        for string in labels {
            self.addTag(string)
        }
        self.snp_updateConstraints { (make) -> Void in
            make.height.equalTo(Int((currentRow+1)*30))
        }
    }
    
    func addTag(text: String, target: AnyObject? = nil, tapAction: Selector? = nil, longPressAction: Selector? = nil, backgroundColor: UIColor? = nil, color: UIColor? = nil) {
        
        // 初始化标签控件（自定义属性）
        let label = UILabel()
        label.layer.cornerRadius = 3
        label.clipsToBounds = true
        label.textColor = UIColor.whiteColor()
        label.backgroundColor = backgroundColor ?? labelBackgroundColor
        label.text = text
        label.textColor = color ?? textColor
        label.font = UIFont.systemFontOfSize(14)
        label.sizeToFit()
        label.textAlignment = .Center
        self.tags.append(label)
        label.layer.shouldRasterize = true
        label.layer.rasterizationScale = UIScreen.mainScreen().scale
        
        // 图标
        if iconName != nil {
            label.textAlignment = .Left
            let imageView = UIImageView(image: UIImage(named: iconName!))
            label.addSubview(imageView)
            
            imageView.snp_makeConstraints { (make) -> Void in
                make.width.equalTo(14)
                make.height.equalTo(14)
                make.right.equalTo(label.snp_right).offset(-tagCombinedMargin)
                make.centerY.equalTo(label.snp_centerY)
            }
        }
        
        // 处理事件
        if tapAction != nil {
            let tap = UITapGestureRecognizer(target: target, action: tapAction!)
            label.userInteractionEnabled = true
            label.addGestureRecognizer(tap)
        }
        
        if longPressAction != nil {
            let longPress = UILongPressGestureRecognizer(target: target, action: longPressAction!)
            label.addGestureRecognizer(longPress)
        }
        
        // 计算frame
        let iconPadding: CGFloat = (iconName == nil) ? 0 : (14+3)
        label.frame = CGRectMake(label.frame.origin.x, label.frame.origin.y, label.frame.width + tagCombinedMargin + iconPadding, rowHeight - tagVerticalPadding)
        if self.tags.count == 0 {
            label.frame = CGRectMake(hashtagsOffset.left, hashtagsOffset.top, label.frame.width, label.frame.height)
            self.addSubview(label)
            
        } else {
            label.frame = self.generateFrameAtIndex(tags.count-1, rowNumber: &currentRow)
            self.addSubview(label)
        }
    }
    
    private func isOutofBounds(newPoint:CGPoint,labelFrame:CGRect) {
        let bottomYLimit = newPoint.y + labelFrame.height
        if bottomYLimit > self.contentSize.height {
            self.contentSize = CGSizeMake(self.contentSize.width, self.contentSize.height + rowHeight - tagVerticalPadding)
        }
    }
    
    func getNextPosition() -> CGPoint {
        return getPositionForIndex(tags.count-1, rowNumber: self.currentRow)
    }
    
    func getPositionForIndex(index:Int,rowNumber:Int) -> CGPoint {
        if index == 0 {
            return CGPointMake(hashtagsOffset.left, hashtagsOffset.top)
        }
        let y = CGFloat(rowNumber) * self.rowHeight + hashtagsOffset.top
        let lastTagFrame = tags[index-1].frame
        let x = lastTagFrame.origin.x + lastTagFrame.width + tagHorizontalPadding
        return CGPointMake(x, y)
    }
    
    func removeTagWithName(name:String) {
        for (index,tag) in tags.enumerate() {
            if tag.text! == name {
                removeTagWithIndex(index)
            }
        }
    }
    
    func removeTagWithIndex(index:Int) {
        if index > tags.count - 1 {
            print("ERROR: Tag Index \(index) Out of Bounds")
            return
        }
        tags[index].removeFromSuperview()
        tags.removeAtIndex(index)
        layoutTagsFromIndex(index)
    }
    
    private func getRowNumber(index:Int) -> Int {
        return Int((tags[index].frame.origin.y - hashtagsOffset.top)/rowHeight)
    }
    
    private func layoutTagsFromIndex(index:Int,animated:Bool = true) {
        if tags.count == 0 {
            return
        }
        let animation:()->() = {
            var rowNumber = self.getRowNumber(index)
            for i in index...self.tags.count - 1 {
                self.tags[i].frame = self.generateFrameAtIndex(i, rowNumber: &rowNumber)
            }
        }
        UIView.animateWithDuration(0.3, animations: animation)
    }
    
    private func generateFrameAtIndex(index:Int,inout rowNumber: Int) -> CGRect {
        var newPoint = self.getPositionForIndex(index, rowNumber: rowNumber)
        if (newPoint.x + self.tags[index].frame.width) >= self.frame.width {
            rowNumber += 1
            newPoint = CGPointMake(self.hashtagsOffset.left, CGFloat(rowNumber) * rowHeight + self.hashtagsOffset.top)
        }
        self.isOutofBounds(newPoint,labelFrame: self.tags[index].frame)
        return CGRectMake(newPoint.x, newPoint.y, self.tags[index].frame.width, self.tags[index].frame.height)
    }
    
    func removeMultipleTagsWithIndices(indexSet:Set<Int>) {
        let sortedArray = Array(indexSet).sort()
        for index in sortedArray {
            if index > tags.count - 1 {
                print("ERROR: Tag Index \(index) Out of Bounds")
                continue
            }
            tags[index].removeFromSuperview()
            tags.removeAtIndex(index)
        }
        layoutTagsFromIndex(sortedArray.first!)
    }
    
    func reset() {
        for tag in tags {
            tag.removeFromSuperview()
        }
        tags = []
        currentRow = 0
        numberOfRows = 0
    }
}