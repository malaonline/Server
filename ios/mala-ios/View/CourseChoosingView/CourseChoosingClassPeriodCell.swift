//
//  CourseChoosingClassPeriodCell.swift
//  mala-ios
//
//  Created by 王新宇 on 1/22/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class CourseChoosingClassPeriodCell: MalaBaseCell {
    
    // MARK: - Components
    private lazy var legendView: PeriodStepper = {
        let legendView = PeriodStepper()
        return legendView
    }()
    
    
    // MARK: - Contructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // SubViews
        content.removeFromSuperview()
        contentView.addSubview(legendView)
        
        // Autolayout
        // Remove margin
        title.snp_updateConstraints { (make) -> Void in
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-MalaLayout_Margin_16)
        }

        legendView.snp_makeConstraints { (make) -> Void in
            make.width.equalTo(97)
            make.height.equalTo(27)
            make.centerY.equalTo(self.title.snp_centerY)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
        }
    }

}


class PeriodStepper: UIView, UITextFieldDelegate {
    
    // MARK: - Property
    
    
    // MARK: - Compontents
    /// 计数器
    var stepper: KWStepper!
    /// 输入框
    private lazy var textField: UITextField = {
        let textField = UITextField()
        textField.text = String(format: "%d", 2)
        textField.textAlignment = .Center
        return textField
    }()
    /// 计数器减按钮
    private lazy  var decrementButton: UIButton = {
        let decrementButton = UIButton()
        decrementButton.setImage(UIImage(named: "minus"), forState: .Normal)
        decrementButton.setBackgroundImage(UIImage(named: "grayBackground"), forState: .Normal)
        return decrementButton
    }()
    /// 计数器加按钮
    private lazy var incrementButton: UIButton = {
        let incrementButton = UIButton()
        incrementButton.setImage(UIImage(named: "plus"), forState: .Normal)
        incrementButton.setBackgroundImage(UIImage(named: "grayBackground"), forState: .Normal)
        return incrementButton
    }()
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        configura()
        setupUserInterface()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func configura() {
        stepper = KWStepper(decrementButton: decrementButton, incrementButton: incrementButton)
        
        stepper.autoRepeat = true
        stepper.wraps = false
        stepper.minimumValue = 2
        stepper.maximumValue = 10000
        stepper.value = 2
        stepper.incrementStepValue = 2
        stepper.decrementStepValue = 2
        
        stepper.valueChangedCallback = {
            self.textField.text = String(format: "%d", Int(self.stepper.value))
        }
        
        textField.delegate = self
        textField.keyboardType = .NumberPad
    }
    
    private func setupUserInterface() {
        // SubViews
        addSubview(decrementButton)
        addSubview(textField)
        addSubview(incrementButton)
        
        // Autolayout
        decrementButton.snp_makeConstraints { (make) -> Void in
            make.width.equalTo(31)
            make.height.equalTo(27)
            make.top.equalTo(self.snp_top)
            make.left.equalTo(self.snp_left)
        }
        incrementButton.snp_makeConstraints { (make) -> Void in
            make.width.equalTo(31)
            make.height.equalTo(27)
            make.top.equalTo(decrementButton.snp_top)
            make.right.equalTo(self.snp_right)
        }
        textField.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(27)
            make.top.equalTo(decrementButton.snp_top)
            make.left.equalTo(decrementButton.snp_right)
            make.right.equalTo(incrementButton.snp_left)
        }
    }
    
    
    // MARK: - Delegate
    func textFieldDidEndEditing(textField: UITextField) {
        var value = Int(textField.text ?? "") ?? 0
        let num = value%2
        value = num == 0 ? value : value+1
        self.stepper.value = Double(value)
    }
}