//
//  ThemeRefreshView.swift
//  mala-ios
//
//  Created by 王新宇 on 2/17/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class ThemeRefreshView: UIView {
    
    // MARK: - Property
    private lazy var view: UIView = UIView()
    private lazy var imageView: UIImageView = UIImageView(image: UIImage(named: "refreshImage"))
    private lazy var label: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        label.textColor = MalaDetailsCellSubTitleColor
        label.text = "下拉可刷新"
        label.sizeToFit()
        return label
    }()
    /// 可刷新标记
    var isCanRefresh = false {
        didSet {
            changeTitle()
        }
    }
    /// 动画标记
    var animating = false {
        didSet {
            setAnimating()
        }
    }
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        setupUserInterface()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    // MARK: - Private Method
    private func setupUserInterface() {
        // SubViews
        view.addSubview(imageView)
        view.addSubview(label)
        addSubview(view)
        
        // Autolayout
        view.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(100)
            make.width.equalTo(115)
            make.center.equalTo(self.snp_center)
        }
        imageView.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(self.view.snp_centerY)
            make.left.equalTo(self.view.snp_left)
        }
        label.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(self.view.snp_centerY)
            make.right.equalTo(self.view.snp_right)
        }
        
    }
    
    private func changeTitle() {
        if isCanRefresh {
            label.text = "释放可刷新"
        }else {
            label.text = "下拉可刷新"
        }
    }
    
    private func setAnimating() {
        if animating {
            imageView.image = (UIScreen.mainScreen().scale == 3 ? UIImage(named: "refreshImage@3x.gif") : UIImage(named: "refreshImage@2x.gif"))
            label.text = "正在加载.."
        }else {
            imageView.image = UIImage(named: "refreshImage")
            label.text = "下拉可刷新"
        }
    }
}
