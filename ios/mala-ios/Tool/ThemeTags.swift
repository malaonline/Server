//
//  ThemeTags.swift
//  mala-ios
//
//  Created by 王新宇 on 1/19/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class ThemeTags: UIView {
    
    
    enum TagsArray {
        case HigherArray
        case LowerArray
    }
    
    // MARK: - Property
    /// 标签高度
    var itemHeight: CGFloat = 26
    /// 水平间距
    var verticalMargin: CGFloat = 7
    /// 垂直间距
    var horizontalMargin: CGFloat = 7
    /// 折行字数临界值
    var critical: Int = 4
    /// [不限] 按钮
    var allButton: UIButton?
    /// 字数小于临界值-字符串数组
    private var lowerArray: [String] = []
    /// 字数较少标签--每行最大个数
    var numbersForLowerInRow: Int = 3
    /// 字数大于临界值-字符串数组
    private var higherArray: [String] = []
    /// 字数较多标签--每行最大个数
    var numbersForHigherInRow: Int = 2
    /// 标签字符串数组
    var tags: [String]? {
        didSet {
            // 遍历数组，过滤出高、低临界值数组
            tags?.insert("不限", atIndex: 0)
            for string in tags ?? [] {
                string.characters.count <= critical ? lowerArray.append(string) : higherArray.append(string)
            }
            self.layoutTags()
        }
    }
    // 当前布局高度
    private var currentHeight: CGFloat = 0
    // 当前布局宽度
    private var currentWidth: CGFloat = 0
    // 小标签宽度
    private var lowWidth: CGFloat {
        get {
            return (self.frame.width - 1 - verticalMargin*2) / 3
        }
    }
    // 大标签宽度
    private var highWidth: CGFloat {
        get {
            return (self.frame.width - 1 - verticalMargin) / 2
        }
    }
    

    // MARK: - Contructed
    init(frame: CGRect, tags: [String]) {
        super.init(frame: frame)
        backgroundColor = UIColor.whiteColor()
        self.tags = tags
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    
    // MARK: - Private Method
    private func layoutTags() {
        
        print("low 标签：\(lowerArray)")
        print("high标签：\(higherArray)")
        
        // 将两数组可整行显示的标签，进行排列布局显示
        layoutFillRowWithArray(.LowerArray)
        layoutFillRowWithArray(.HigherArray)
        
        // 排列显示低于临界值数组 剩余标签
        // 若两数组均剩余一字符串，则同行显示，否则分行显示。
        if lowerArray.count == 1 {
            layoutRemainder(.LowerArray)
        }else {
            layoutRemainder(.LowerArray)
            currentHeight += horizontalMargin + itemHeight
            currentWidth = 0
        }
        
        // 排列显示高于临界值数组 剩余标签
        layoutRemainder(.HigherArray)
    }
    
    ///  将指定数组的可整行排列的标签，进行显示
    ///
    ///  - parameter arrayType: 数组类型
    private func layoutFillRowWithArray(arrayType: TagsArray) {
        let numbers = arrayType == .HigherArray ? numbersForHigherInRow : numbersForLowerInRow
        repeat {
            for _ in 1...numbers {
                arrayType == .HigherArray ? setupItem(.HigherArray) : setupItem(.LowerArray)
            }
            currentHeight += horizontalMargin + itemHeight
            currentWidth = 0
        } while (arrayType == .HigherArray ? higherArray : lowerArray).count >= numbers
    }
    
    ///  排列指定数组余下的标签
    ///
    ///  - parameter array: 标签数组
    private func layoutRemainder(arrayType: TagsArray) {
        for _ in (arrayType == .HigherArray ? higherArray : lowerArray) {
            setupItem(arrayType)
        }
    }
    
    ///  排列指定数组中的第一个标签
    ///
    ///  - parameter arrayType: 数组类型
    private func setupItem(arrayType: TagsArray) {
        let buttonWidth = arrayType == .HigherArray ? highWidth : lowWidth
        // 创建一个标签并布局、设置变量之后，从数组中remove这个标签文字
        let button = UIButton(
            title: (arrayType == .HigherArray ? higherArray : lowerArray)[0],
            borderColor: MalaRandomColor(),
            target: self,
            action: "buttonDidTap:"
        )
        self.addSubview(button)
        
        // 每行首个按钮无Margin
        let x = currentWidth == 0 ? currentWidth+1 : currentWidth + verticalMargin
        button.frame = CGRect(x: x, y: currentHeight, width: buttonWidth, height: itemHeight)
        currentWidth = CGRectGetMaxX(button.frame)
        if arrayType == .HigherArray {
            higherArray.removeAtIndex(0)
        }else if arrayType == .LowerArray {
            lowerArray.removeAtIndex(0)
        }
    }
    
    
    // MARK: - Event Response
    @objc private func buttonDidTap(sender: UIButton) {
        sender.highlighted = !sender.highlighted
        sender.selected = !sender.selected
        print(sender.titleLabel?.text)
    }
}

extension UIButton {
    
    ///  便利构造函数
    ///
    ///  - parameter title:       标题
    ///  - parameter borderColor: Normal状态边框颜色，Highlighted状态背景颜色
    ///
    ///  - returns: UIButton对象
    convenience init(title: String, borderColor: UIColor, target: AnyObject?, action: Selector) {
        self.init()
        // 文字及其状态颜色
        self.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        self.setTitle(title, forState: .Normal)
        self.setTitleColor(MalaFilterViewTagsTextColor, forState: .Normal)
        self.setTitleColor(UIColor.whiteColor(), forState: .Highlighted)
        self.setTitleColor(UIColor.whiteColor(), forState: .Selected)
        // 背景状态颜色
        self.setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forState: .Normal)
        self.setBackgroundImage(UIImage.withColor(borderColor), forState: .Highlighted)
        self.setBackgroundImage(UIImage.withColor(borderColor), forState: .Selected)
        // 圆角和边框
        self.layer.cornerRadius = 5
        self.layer.masksToBounds = true
        self.layer.borderColor = borderColor.CGColor
        self.layer.borderWidth = MalaScreenOnePixel
        self.addTarget(target, action: action, forControlEvents: .TouchUpInside)
    }
}