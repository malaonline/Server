//
//  InfoModifyViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 3/11/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class InfoModifyViewController: UIViewController {
    
    // MARK: - Property
    
    
    // MARK: - Components
    /// 输入区域背景
    private lazy var inputBackground: UIView = {
        let inputBackground = UIView()
        inputBackground.backgroundColor = UIColor.whiteColor()
        return inputBackground
    }()
    /// 输入控件
    private lazy var inputField: UITextField = {
        let inputField = UITextField()
        inputField.textAlignment = .Center
        inputField.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        inputField.textColor = MalaDetailsCellLabelColor
        inputField.tintColor = MalaDetailsButtonBlueColor
        return inputField
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        setupUserInterface()
        configure()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        self.view.backgroundColor = MalaProfileBackgroundColor
        
        // SubViews
        view.addSubview(inputBackground)
        view.addSubview(inputField)
        
        // Autolayout
        inputBackground.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(view.snp_top)
            make.left.equalTo(view.snp_left)
            make.right.equalTo(view.snp_right)
            make.height.equalTo(MalaLayout_ProfileModifyViewHeight)
        }
        inputField.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(inputBackground.snp_left)
            make.right.equalTo(inputBackground.snp_right)
            make.centerY.equalTo(inputBackground.snp_centerY)
        }
    }
    
    
    private func configure() {
        // leftBarButtonItem
        let spacerLeft = UIBarButtonItem(barButtonSystemItem: .FixedSpace, target: nil, action: nil)
        spacerLeft.width = -MalaLayout_Margin_12
        let leftBarButtonItem = UIBarButtonItem(customView:
            UIButton(
                imageName: "leftArrow_normal",
                highlightImageName: "leftArrow_press",
                target: self,
                action: "popSelf"
            )
        )
        navigationItem.leftBarButtonItems = [spacerLeft, leftBarButtonItem]
        
        // rightBarButtonItem
        let spacerRight = UIBarButtonItem(barButtonSystemItem: .FixedSpace, target: nil, action: nil)
        spacerRight.width = -MalaLayout_Margin_5
        let rightBarButtonItem = UIBarButtonItem(customView:
            UIButton(
                title: "保存",
                titleColor: MalaDetailsButtonBlueColor,
                target: self,
                action: "saveChange"
            )
        )
        navigationItem.rightBarButtonItems = [rightBarButtonItem, spacerRight]
    }

    
    // MARK: - Event Response
    @objc private func popSelf() {
        self.navigationController?.popViewControllerAnimated(true)
    }
    
    @objc private func saveChange() {
        println("保存成功")
        self.navigationController?.popViewControllerAnimated(true)
    }
}