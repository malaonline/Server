//
//  CourseChoosingServiceTableView.swift
//  mala-ios
//
//  Created by 王新宇 on 2/18/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let CourseChoosingServiceTableViewCellReuseId = "CourseChoosingServiceTableViewCellReuseId"

class CourseChoosingServiceTableView: UITableView, UITableViewDelegate, UITableViewDataSource {

    // MARK: - Property
    var services: [OtherServiceModel] = MalaOtherService
    
    // MARK: - Constructed
    override init(frame: CGRect, style: UITableViewStyle) {
        super.init(frame: frame, style: style)
        
        configure()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func configure() {
        delegate = self
        dataSource = self
        bounces = false
        separatorColor = MalaColor_E5E5E5_0
        registerClass(CourseChoosingServiceTableViewCell.self, forCellReuseIdentifier: CourseChoosingServiceTableViewCellReuseId)
        
    }
    
    
    // MARK: - Delegate
    func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return MalaLayout_OtherServiceCellHeight
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        if let viewController = (services[indexPath.row].viewController) as? UIViewController.Type {
            self.viewController()?.navigationController?.pushViewController(viewController.init(), animated: true)
        }
    }
    
    
    // MARK: - DataSource
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // 若非首次购课，不显示第二项[建档测评服务]
        return MalaIsHasBeenEvaluatedThisSubject == true ? MalaOtherService.count : (MalaOtherService.count-1)
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(CourseChoosingServiceTableViewCellReuseId, forIndexPath: indexPath)
        (cell as! CourseChoosingServiceTableViewCell).service = self.services[indexPath.row]
        
        cell.selectionStyle = .None
        cell.separatorInset = UIEdgeInsetsZero
        cell.layoutMargins = UIEdgeInsetsZero
        cell.preservesSuperviewLayoutMargins = false
        return cell
    }
}


// MARK: - CourseChoosingServiceTableViewCell
class CourseChoosingServiceTableViewCell: UITableViewCell {
    
    // MARK: Property
    var service: OtherServiceModel? {
        didSet{
            
            if service?.type == .Coupon {
                configure()
            }
            
            self.titleLabel.text = service?.title

            if service?.priceHandleType == .Discount {
             
                self.priceHandleLabel.text = "-"
                self.priceLabel.text = String(format: "￥%@", (service?.price ?? 0).money)
            }else if service?.priceHandleType == .Reduce {

                let oldPrice = String(format: "￥%d", (service?.price ?? 0))
                let attr = NSMutableAttributedString(string: oldPrice)
                attr.addAttribute(NSStrikethroughStyleAttributeName, value: NSNumber(integer: 1), range: NSMakeRange(0, oldPrice.characters.count))
                self.priceHandleLabel.attributedText = attr
                
                self.priceLabel.text = String(format: "￥%d", 0)
            }
        }
    }
    private var myContext = 0
    private var didAddObserve = false
    
    
    // MARK: - Components
    /// 标题Label
    private lazy var titleLabel: UILabel = {
        let titleLabel = UILabel()
        titleLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        titleLabel.textColor = MalaColor_WhiteColor
        return titleLabel
    }()
    /// 右箭头标示
    private lazy var detailImageView: UIImageView = {
        let detailImageView = UIImageView(image: UIImage(named: "rightArrow"))
        return detailImageView
    }()
    /// 价格Label
    private lazy var priceLabel: UILabel = {
        let priceLabel = UILabel()
        priceLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        priceLabel.textColor = MalaColor_333333_0
        return priceLabel
    }()
    /// 价格处理Label
    private lazy var priceHandleLabel: UILabel = {
        let priceHandleLabel = UILabel()
        priceHandleLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        priceHandleLabel.textColor = MalaColor_333333_0
        return priceHandleLabel
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
        
        // Subviews
        contentView.addSubview(titleLabel)
        contentView.addSubview(detailImageView)
        contentView.addSubview(priceLabel)
        contentView.addSubview(priceHandleLabel)
        
        // Autolayout
        titleLabel.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(contentView.snp_left)
            make.height.equalTo(MalaLayout_FontSize_14)
            make.centerY.equalTo(contentView.snp_centerY)
        }
        detailImageView.snp_makeConstraints { (make) -> Void in
            make.right.equalTo(contentView.snp_right)
            make.centerY.equalTo(contentView.snp_centerY)
        }
        priceLabel.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(MalaLayout_FontSize_14)
            make.right.equalTo(detailImageView.snp_left).offset(-MalaLayout_Margin_6)
            make.centerY.equalTo(contentView.snp_centerY)
        }
        priceHandleLabel.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(MalaLayout_FontSize_14)
            make.right.equalTo(priceLabel.snp_left).offset(-MalaLayout_Margin_6)
            make.centerY.equalTo(contentView.snp_centerY)
        }
    }
    
    private func configure() {
        MalaCourseChoosingObject.addObserver(self, forKeyPath: "coupon", options: .New, context: &myContext)
        didAddObserve = true
    }
    
    
    // MARK: - Override
    override func observeValueForKeyPath(keyPath: String?, ofObject object: AnyObject?, change: [String : AnyObject]?, context: UnsafeMutablePointer<Void>) {
        // 选择优惠券时更新UI
        self.titleLabel.text = MalaCourseChoosingObject.coupon?.name
        self.priceLabel.text = String(format: "￥%d", (MalaCourseChoosingObject.coupon?.amount ?? 0))
    }
    
    
    deinit {
        if didAddObserve {
            MalaCourseChoosingObject.removeObserver(self, forKeyPath: "coupon", context: &myContext)
        }
    }
}