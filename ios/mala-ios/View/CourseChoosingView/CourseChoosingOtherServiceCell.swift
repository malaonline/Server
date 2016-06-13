//
//  CourseChoosingOtherServiceCell.swift
//  mala-ios
//
//  Created by 王新宇 on 1/22/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class CourseChoosingOtherServiceCell: MalaBaseCell {

    // MARK: - Property
    /// 价格
    var price: Int = 0 {
        didSet {
            self.priceView.price = price
        }
    }
    
    
    // MARK: - Components
    private lazy var tableView: CourseChoosingServiceTableView = {
        let tableView = CourseChoosingServiceTableView(frame: CGRectZero, style: .Plain)
        return tableView
    }()
    private lazy var priceView: PriceResultView = {
        let priceView = PriceResultView()
        return priceView
    }()
    
    
    // MARK: - Constructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        setupUserInterface()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        
        
        // SubViews
        contentView.addSubview(tableView)
        contentView.addSubview(priceView)
        
        // Autolayout
        priceView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.tableView.snp_bottom)
            make.left.equalTo(self.contentView.snp_left)
            make.right.equalTo(self.contentView.snp_right)
            make.bottom.equalTo(self.contentView.snp_bottom)
            make.height.equalTo(MalaLayout_OtherServiceCellHeight)
        }
        
        let otherServiceCount = (MalaIsHasBeenEvaluatedThisSubject == true ? MalaOtherService.count : (MalaOtherService.count-1))
        tableView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top)
            make.bottom.equalTo(priceView.snp_top)
            make.left.equalTo(self.contentView.snp_left).offset(12)
            make.right.equalTo(self.contentView.snp_right).offset(-12)
            make.height.equalTo(otherServiceCount*Int(MalaLayout_OtherServiceCellHeight))
        }
    }
}


// MARK: - PriceResultView
class PriceResultView: UIView {
    
    // MARK: - Property
    /// 价格
    var price: Int = 0 {
        didSet{
            self.priceLabel.text = price.moneyCNY
        }
    }
    private var myContext = 0
    
    // MARK: - Components
    /// 价格说明标签
    private lazy var stringLabel: UILabel = {
        let stringLabel = UILabel()
        stringLabel.textColor = MalaColor_333333_0
        stringLabel.font = UIFont.systemFontOfSize(14)
        stringLabel.text = "原价:"
        return stringLabel
    }()
    /// 金额标签
    private lazy var priceLabel: UILabel = {
        let priceLabel = UILabel()
        priceLabel.textColor = MalaColor_E36A5D_0
        priceLabel.font = UIFont.systemFontOfSize(14)
        priceLabel.text = "￥0.00"
        return priceLabel
    }()
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        setupUserInterface()
        configure()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Override
    override func observeValueForKeyPath(keyPath: String?, ofObject object: AnyObject?, change: [String : AnyObject]?, context: UnsafeMutablePointer<Void>) {
        // 当选课条件改变时，更新总价
        self.price = MalaCourseChoosingObject.originalPrice
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        self.backgroundColor = UIColor.whiteColor()
        
        // SubViews
        addSubview(stringLabel)
        addSubview(priceLabel)
        
        // Autolayout
        priceLabel.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(14)
            make.centerY.equalTo(self.snp_centerY)
            make.right.equalTo(self.snp_right).offset(-12)
        }
        stringLabel.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(14)
            make.bottom.equalTo(self.priceLabel.snp_bottom)
            make.right.equalTo(priceLabel.snp_left)
        }
    }
    
    private func configure() {
        MalaCourseChoosingObject.addObserver(self, forKeyPath: "originalPrice", options: .New, context: &myContext)
    }
    
    
    deinit {
        MalaCourseChoosingObject.removeObserver(self, forKeyPath: "originalPrice", context: &myContext)
    }
}