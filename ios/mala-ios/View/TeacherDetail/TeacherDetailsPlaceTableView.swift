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

    // MARK: - Property
    var model: [SchoolModel] = [] {
        didSet {
            reloadData()
        }
    }
    var isOpen: Bool = false {
        didSet {
            reloadData()
        }
    }

    // MARK: - Constructed
    override init(frame: CGRect, style: UITableViewStyle) {
        super.init(frame: frame, style: style)
        configure()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Delegate
    func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return false
    }
    
    func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return MalaLayout_DetailSchoolsTableViewCellHeight
    }
    
    
    // MARK: - DataSource
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return isOpen ? model.count : 1
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(TeacherDetailsSchoolsTableViewCellReuseId, forIndexPath: indexPath)
        (cell as! TeacherDetailsSchoolsTableViewCell).model = model[indexPath.row]
        return cell
    }
    
    
    // MARK: - Private Method
    private func configure() {
        delegate = self
        dataSource = self
        scrollEnabled = false
        separatorColor = MalaColor_E5E5E5_0
        registerClass(TeacherDetailsSchoolsTableViewCell.self, forCellReuseIdentifier: TeacherDetailsSchoolsTableViewCellReuseId)
    }
}


// MARK: - TeacherDetailsSchoolsTableViewCell
class TeacherDetailsSchoolsTableViewCell: UITableViewCell {
    
    // MARK: - Property
    var model: SchoolModel? {
        didSet {
            
            guard model != nil else {
                return
            }
            
            photoView.kf_setImageWithURL(NSURL(string: (model?.thumbnail) ?? "")!, placeholderImage: UIImage(named: "detailPicture_placeholder"))
            titleLabel.text = model!.name
            addressLabel.text = model!.address
            distanceLabel.text = String(showDistance: (model?.distance ?? 0.0))
        }
    }
    
    
    // MARK: - Components
    private lazy var photoView: UIImageView = {
        let photoView = UIImageView(image: UIImage(named: "detailPicture_placeholder"))
        return photoView
    }()
    private lazy var titleLabel: UILabel = {
        let titleLabel = UILabel()
        titleLabel.textColor = MalaColor_333333_0
        titleLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_15)
        return titleLabel
    }()
    private lazy var addressLabel: UILabel = {
        let addressLabel = UILabel()
        addressLabel.textColor = MalaColor_939393_0
        addressLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        return addressLabel
    }()
    private lazy var distanceLabel: UILabel = {
        let distanceLabel = UILabel()
        distanceLabel.text = "未知"
        distanceLabel.textColor = MalaColor_939393_0
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
        // Style
        separatorInset = UIEdgeInsetsZero
        layoutMargins = UIEdgeInsetsZero
        preservesSuperviewLayoutMargins = false
        
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
