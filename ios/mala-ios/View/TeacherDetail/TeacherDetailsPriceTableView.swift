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
    
    // MARK: - Variables
    var prices: [GradePriceModel] {
        didSet {
            reloadData()
        }
    }
    
    
    // MARK: - Constructed
    override init(frame: CGRect, style: UITableViewStyle) {
        self.prices = []
        super.init(frame: frame, style: style)
        
        delegate = self
        dataSource = self
        registerClass(TeacherDetailsPriceTableViewCell.self, forCellReuseIdentifier: TeacherDetailsPriceTableViewCellReuseId)
        
        // Style
        estimatedRowHeight = 60
        scrollEnabled = false
        separatorColor = MalaDetailsButtonBorderColor
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - DataSource
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return prices.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(TeacherDetailsPriceTableViewCellReuseId, forIndexPath: indexPath)
        (cell as! TeacherDetailsPriceTableViewCell).price = prices[indexPath.row]
        cell.separatorInset = UIEdgeInsetsZero
        cell.layoutMargins = UIEdgeInsetsZero
        cell.preservesSuperviewLayoutMargins = false
        return cell
    }
    
    func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return false
    }
    
}


// MARK: - TeacherDetailsPriceTableViewCell
class TeacherDetailsPriceTableViewCell: UITableViewCell {
    
    // MARK: - Variables
    var price: GradePriceModel? {
        didSet {
            subjectLabel.text = price!.grade!.name
            priceLabel.text = String(format: "¥%d/课时", price!.price!)
        }
    }
    
    
    // MARK: - Components
    private lazy var subjectLabel: UILabel = {
        let subjectLabel = UILabel()
        subjectLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        subjectLabel.textColor = MalaDetailsCellLabelColor
        return subjectLabel
    }()
    
    private lazy var priceLabel: UILabel = {
        let priceLabel = UILabel()
        priceLabel.font = UIFont.systemFontOfSize(14)
        priceLabel.textColor = MalaDetailsPriceRedColor
        return priceLabel
    }()
    
    private lazy var discountLabel: UILabel = {
        let discountLabel = UILabel()
        
        return discountLabel
    }()
    
    private lazy var signupButton: UIButton = {
        let signupButton = UIButton(frame: CGRect(x: 0, y: 0, width: 40, height: 22))
        signupButton.setTitle("报名", forState: .Normal)
        signupButton.setTitleColor(MalaDetailsButtonBlueColor, forState: .Normal)
        signupButton.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        signupButton.layer.cornerRadius = 3.0
        signupButton.layer.masksToBounds = true
        signupButton.layer.borderWidth = MalaScreenOnePixel
        signupButton.layer.borderColor = MalaDetailsButtonBlueColor.CGColor
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
        
        // SubView
        contentView.addSubview(subjectLabel)
        contentView.addSubview(priceLabel)
        contentView.addSubview(discountLabel)
        contentView.addSubview(signupButton)
        
        // Autolayout
        subjectLabel.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_14)
            make.left.equalTo(self.contentView.snp_left)
            make.right.equalTo(self.contentView.snp_right)
            make.height.equalTo(MalaLayout_FontSize_14)
        })
        priceLabel.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.subjectLabel.snp_bottom).offset(MalaLayout_Margin_13)
            make.left.equalTo(self.subjectLabel.snp_left)
            make.height.equalTo(MalaLayout_FontSize_14)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-MalaLayout_Margin_16)
        })
        discountLabel.snp_makeConstraints(closure: { (make) -> Void in
            make.left.equalTo(self.priceLabel.snp_right).offset(MalaLayout_Margin_8)
            make.height.equalTo(MalaLayout_FontSize_12)
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
