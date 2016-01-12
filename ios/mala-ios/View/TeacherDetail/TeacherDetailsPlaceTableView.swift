//
//  TeacherDetailsSchoolsTableViewCell.swift
//  mala-ios
//
//  Created by Elors on 1/12/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let TeacherDetailsSchoolsTableViewCellReuseId = "TeacherDetailsSchoolsTableViewCellReuseId"

class TeacherDetailsSchoolsTableView: UITableView, UITableViewDelegate, UITableViewDataSource {

    // MARK: - Variables
    var model: [SchoolModel] = [] {
        didSet {
            reloadData()
        }
    }
    var isOpen: Bool = false {
        didSet {
            if isOpen {
                button.removeFromSuperview()
                reloadData()
            }
        }
    }
    
    
    // MARK: - Components
    private lazy var button: UIButton = {
        let button = UIButton()
        button.setTitle("距离您最近的社区中心", forState: .Normal)
        button.titleLabel?.font = UIFont.systemFontOfSize(15)
        button.setTitleColor(MalaDetailsCellLabelColor, forState: .Normal)
        button.setImage(UIImage(named: "dropArrow"), forState: .Normal)        
        button.addTarget(self, action: "buttonDidTap", forControlEvents: .TouchUpInside)
        button.imageEdgeInsets = UIEdgeInsets(top: 0, left: -5, bottom: 0, right: 5)
        return button
    }()
    
    
    // MARK: - Constructed
    override init(frame: CGRect, style: UITableViewStyle) {
        super.init(frame: frame, style: style)
        
        delegate = self
        dataSource = self
        registerClass(TeacherDetailsSchoolsTableViewCell.self, forCellReuseIdentifier: TeacherDetailsSchoolsTableViewCellReuseId)
        
        // Style
        estimatedRowHeight = 107
        scrollEnabled = false
        separatorColor = MalaDetailsButtonBorderColor
        
        if !isOpen {
            addSubview(button)
            button.snp_makeConstraints { (make) -> Void in
                make.height.equalTo(40)
                make.width.equalTo(MalaScreenWidth - (MalaLayout_Margin_6*2))
                make.top.equalTo(self.snp_top).offset(108)
            }
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - DataSource
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return isOpen ? model.count : 1
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(TeacherDetailsSchoolsTableViewCellReuseId, forIndexPath: indexPath)
        (cell as! TeacherDetailsSchoolsTableViewCell).model = model[indexPath.row]
        cell.separatorInset = UIEdgeInsetsZero
        cell.layoutMargins = UIEdgeInsetsZero
        cell.preservesSuperviewLayoutMargins = false
        return cell
    }
    
    func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return false
    }
    
    
    // MARK: - Private Method
    @objc private func buttonDidTap() {
        // Post Notification to Refresh TeacherDetailsTableView
        NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_OpenSchoolsCell, object: nil)
    }
}


// MARK: - TeacherDetailsSchoolsTableViewCell
class TeacherDetailsSchoolsTableViewCell: UITableViewCell {
    
    // MARK: - Variables
    var model: SchoolModel? {
        didSet {
            photoView.kf_setImageWithURL(NSURL(string: (model?.thumbnail) ?? "")!, placeholderImage: nil)
            titleLabel.text = model!.name
            addressLabel.text = model!.address
        }
    }
    
    
    // MARK: - Components
    private lazy var photoView: UIImageView = {
        let photoView = UIImageView()
        photoView.backgroundColor = UIColor.lightGrayColor()
        return photoView
    }()
    private lazy var titleLabel: UILabel = {
        let titleLabel = UILabel()
        titleLabel.textColor = MalaDetailsCellTitleColor
        titleLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_15)
        return titleLabel
    }()
    private lazy var addressLabel: UILabel = {
        let addressLabel = UILabel()
        addressLabel.textColor = MalaDetailsCellSubTitleColor
        addressLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        return addressLabel
    }()
    private lazy var distanceLabel: UILabel = {
        let distanceLabel = UILabel()
        distanceLabel.text = "未知"
        distanceLabel.textColor = MalaDetailsCellSubTitleColor
        distanceLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        distanceLabel.textAlignment = .Right
        return distanceLabel
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
        contentView.addSubview(photoView)
        contentView.addSubview(titleLabel)
        contentView.addSubview(addressLabel)
        contentView.addSubview(distanceLabel)

        // Autolayout
        photoView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_14)
            make.left.equalTo(self.contentView.snp_left)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-MalaLayout_Margin_14)
            make.width.equalTo(110)
            make.height.equalTo(79)
        }
        titleLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.photoView.snp_top).offset(MalaLayout_Margin_10)
            make.left.equalTo(self.photoView.snp_right).offset(MalaLayout_Margin_10)
            make.height.equalTo(MalaLayout_FontSize_15)
            make.right.equalTo(self.contentView.snp_right)
        }
        addressLabel.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.photoView.snp_right).offset(MalaLayout_Margin_10)
            make.bottom.equalTo(self.photoView.snp_bottom).offset(-MalaLayout_Margin_10)
            make.height.equalTo(MalaLayout_FontSize_12)
        }
        distanceLabel.snp_makeConstraints { (make) -> Void in
            make.right.equalTo(self.contentView.snp_right)
            make.bottom.equalTo(addressLabel.snp_bottom)
            make.height.equalTo(MalaLayout_FontSize_12)
        }   
    }
    
}
