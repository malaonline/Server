//
//  TeacherDetailsPriceTableView.swift
//  mala-ios
//
//  Created by Elors on 1/8/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let TeacherDetailsPriceTableViewCellReuseId = "TeacherDetailsPriceTableViewCellReuseId"

class TeacherDetailsPriceTableView: UITableView, UITableViewDelegate, UITableViewDataSource {
    
    // MARK: - Property
    var prices: [GradePriceModel?] = [] {
        didSet {
            reloadData()
        }
    }
    
    
    // MARK: - Constructed
    override init(frame: CGRect, style: UITableViewStyle) {
        super.init(frame: frame, style: style)
        configureTableView()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Delegate
    func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return false
    }
    
    
    // MARK: - DataSource
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return prices.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(TeacherDetailsPriceTableViewCellReuseId, forIndexPath: indexPath)
        (cell as! TeacherDetailsPriceTableViewCell).price = prices[indexPath.row]
        return cell
    }
    
    
    // MARK: - Private Method
    private func configureTableView() {
        // settings
        delegate = self
        dataSource = self
        scrollEnabled = false
        estimatedRowHeight = 60
        separatorColor = MalaColor_E5E5E5_0
        registerClass(TeacherDetailsPriceTableViewCell.self, forCellReuseIdentifier: TeacherDetailsPriceTableViewCellReuseId)
    }
}


// MARK: - TeacherDetailsPriceTableViewCell
class TeacherDetailsPriceTableViewCell: UITableViewCell {
    
    // MARK: - Property
    var price: GradePriceModel? {
        didSet {
            subjectLabel.text = price!.grade!.name
            priceLabel.text = String(format: "¥%d/课时", price!.price)
        }
    }
    
    
    // MARK: - Components
    /// 课程名称label
    private lazy var subjectLabel: UILabel = {
        let subjectLabel = UILabel()
        subjectLabel.font = UIFont.systemFontOfSize(14)
        subjectLabel.textColor = MalaColor_636363_0
        return subjectLabel
    }()
    /// 价格label
    private lazy var priceLabel: UILabel = {
        let priceLabel = UILabel()
        priceLabel.font = UIFont.systemFontOfSize(14)
        priceLabel.textColor = MalaColor_E36A5D_0
        return priceLabel
    }()
    /// 折扣label
    private lazy var discountLabel: UILabel = {
        let discountLabel = UILabel()
        return discountLabel
    }()
    /// 报名按钮
    private lazy var signupButton: UIButton = {
        let signupButton = UIButton(frame: CGRect(x: 0, y: 0, width: 40, height: 22))
        signupButton.setTitle("报名", forState: .Normal)
        signupButton.setTitleColor(MalaColor_82B4D9_0, forState: .Normal)
        signupButton.titleLabel?.font = UIFont.systemFontOfSize(14)
        signupButton.layer.cornerRadius = 3.0
        signupButton.layer.masksToBounds = true
        signupButton.layer.borderWidth = MalaScreenOnePixel
        signupButton.layer.borderColor = MalaColor_82B4D9_0.CGColor
        return signupButton
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
        separatorInset = UIEdgeInsetsZero
        layoutMargins = UIEdgeInsetsZero
        preservesSuperviewLayoutMargins = false
        
        // SubView
        contentView.addSubview(subjectLabel)
        contentView.addSubview(priceLabel)
        contentView.addSubview(discountLabel)
        contentView.addSubview(signupButton)
        
        // Autolayout
        subjectLabel.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(14)
            make.left.equalTo(self.contentView.snp_left)
            make.right.equalTo(self.contentView.snp_right)
            make.height.equalTo(14)
        })
        priceLabel.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.subjectLabel.snp_bottom).offset(13)
            make.left.equalTo(self.subjectLabel.snp_left)
            make.height.equalTo(14)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-16)
        })
        discountLabel.snp_makeConstraints(closure: { (make) -> Void in
            make.left.equalTo(self.priceLabel.snp_right).offset(8)
            make.height.equalTo(12)
            make.bottom.equalTo(self.priceLabel.snp_bottom)
        })
        signupButton.snp_makeConstraints(closure: { (make) -> Void in
            make.width.equalTo(40)
            make.height.equalTo(22)
            make.right.equalTo(self.contentView.snp_right)
            make.bottom.equalTo(self.priceLabel.snp_bottom)
        })
    }
}
