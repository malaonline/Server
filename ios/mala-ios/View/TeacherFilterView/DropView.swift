//
//  DropView.swift
//  mala-ios
//
//  Created by Elors on 12/24/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit
import SnapKit

// MARK: - DropViewDelegate
protocol DropViewDelegate: class, NSObjectProtocol {
    
    // DidSelectedItem(Cell)
    func dropViewDidTapButtonForContentView(contentView: UIView)
}

class DropView: UIView {

    // MARK: - Components
    private lazy var commitButton: UIButton = {
        let button = UIButton(title: "筛选", titleColor: UIColor.whiteColor(), selectedTitleColor: nil, bgColor: UIColor.redColor(), selectedBgColor: UIColor.redColor())
        button.frame = CGRect(x: 0, y: self.frame.size.height - 40, width: self.frame.size.width, height: 40)
        button.addTarget(self, action: "commitButtonDidTap", forControlEvents: .TouchUpInside)
        return button
    }()
    
    
    // MARK: - Variables
    // Delegate
    weak var delegate: DropViewDelegate?
    var contentView: UIView?
    var isShow: Bool = false
    var originFrame: CGRect = CGRectZero
    
    
    // MARK: - Contructed
    init(frame: CGRect, viewController: UIViewController, contentView: UIView) {
        super.init(frame: frame)
        
        self.backgroundColor = UIColor.whiteColor()
        originFrame = frame ?? CGRectZero
        viewController.view.addSubview(self)
        self.contentView = contentView
        self.contentView?.frame = CGRect(x: 0, y: 0, width: self.frame.size.width, height: self.frame.size.height - 40)
        self.addSubview(contentView)
        self.addSubview(commitButton)
        
        commitButton.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self.snp_bottom)
            make.centerX.equalTo(self.snp_centerX)
            make.height.equalTo(40)
            make.width.equalTo(self.snp_width)
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    
    // MARK: - API
    func show() {
        if let view = self.contentView as? TeacherFilterView {
            view.loadFilterCondition()
        }
        
        UIView.animateWithDuration(0.25, animations: { () -> Void in
            self.frame.origin.y = 0
            }) { (isCompletion) -> Void in
                self.isShow = true
        }
    }
    
    func dismiss() {
        UIView.animateWithDuration(0.25, animations: { () -> Void in
            self.frame.origin.y = self.originFrame.origin.y - MalaScreenNaviHeight
            }) { (isCompletion) -> Void in
                self.isShow = false
        }
    }
    
    
    // MARK: - Event Response
    @objc private func commitButtonDidTap() {
        delegate?.dropViewDidTapButtonForContentView(self.contentView!)
    }
    
}
