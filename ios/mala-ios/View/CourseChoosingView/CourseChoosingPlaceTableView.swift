//
//  CourseChoosingPlaceTableView.swift
//  mala-ios
//
//  Created by 王新宇 on 1/24/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let CourseChoosingPlaceTableViewCellReuseId = "CourseChoosingPlaceTableViewCellReuseId"

class CourseChoosingPlaceTableView: UITableView, UITableViewDelegate, UITableViewDataSource {
    
    // MARK: - Property
    /// 上课地点模型数组
    var schools: [SchoolModel]? 
    /// 上课地点Cell展开标识
    var isOpen: Bool = false {
        didSet {
            if isOpen {
                button.hidden = true
            }else {
                button.hidden = false
                button.snp_updateConstraints(closure: { (make) -> Void in
                    make.height.equalTo(38)
                })
            }
        }
    }
    /// 当前选中项标记
    var currentSelectedSchool: SchoolModel?
    var selectedIndexPath: NSIndexPath = NSIndexPath(forRow: 0, inSection: 0)
    
    
    // MARK: - Components
    private lazy var button: UIButton = {
        let button = UIButton()
        button.setTitle("展开查看其它地点", forState: .Normal)
        button.setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forState: .Normal)
        button.setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forState: .Highlighted)
        button.titleLabel?.font = UIFont.systemFontOfSize(15)
        button.setTitleColor(MalaColor_636363_0, forState: .Normal)
        button.setImage(UIImage(named: "dropArrow"), forState: .Normal)
        button.addTarget(self, action: "buttonDidTap:", forControlEvents: .TouchUpInside)
        button.imageEdgeInsets = UIEdgeInsets(top: 0, left: -5, bottom: 0, right: 5)
        return button
    }()
    
    
    // MARK: - Constructed
    override init(frame: CGRect, style: UITableViewStyle) {
        super.init(frame: frame, style: style)
        configuraTableView()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Delegate
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let cell = tableView.cellForRowAtIndexPath(indexPath) as! CourseChoosingPlaceTableViewCell
        
        // 发送用户选中的上课地点
        if cell.model != nil {
            
            currentSelectedSchool = cell.model
            
            NSNotificationCenter.defaultCenter().postNotificationName(
                MalaNotification_ChoosingSchool,
                object: schoolChoosingObject(isOpen: false, school: cell.model!, indexPath: indexPath)
            )
        }
        
    }
    
    func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return 64
    }
    
    
    // MARK: - DataSource
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return isOpen ? (schools?.count ?? 0) : 1
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(CourseChoosingPlaceTableViewCellReuseId, forIndexPath: indexPath) as! CourseChoosingPlaceTableViewCell
        
        if schools?.count != 0 {
            cell.model = schools?[indexPath.row]
        }
        
        cell.selectionStyle = .None
        cell.separatorInset = UIEdgeInsetsZero
        cell.layoutMargins = UIEdgeInsetsZero
        cell.preservesSuperviewLayoutMargins = false
        return cell
    }
    
    
    // MARK: - Private Method
    private func configuraTableView() {
        delegate = self
        dataSource = self
        scrollEnabled = false
        separatorColor = MalaColor_E5E5E5_0
        backgroundColor = UIColor.whiteColor()
        registerClass(CourseChoosingPlaceTableViewCell.self, forCellReuseIdentifier: CourseChoosingPlaceTableViewCellReuseId)
        
        // 若Cell尚未展开，则显示展开按钮
        if !isOpen {
            addSubview(button)
            button.snp_makeConstraints { (make) -> Void in
                make.height.equalTo(40)
                make.width.equalTo(MalaScreenWidth - (MalaLayout_Margin_6*2))
                make.top.equalTo(self.snp_top).offset(64)
            }
        }
    }
    
    
    
    // MARK: - Override 
    override func reloadData() {
        // 同步执行保证所有Cell加载完毕后，设置选中样式
        dispatch_async(dispatch_get_main_queue(), {
            
            super.reloadData()
            
            if self.numberOfRowsInSection(0) == 1 {
                self.cellForRowAtIndexPath(NSIndexPath(forRow: 0, inSection: 0))?.selected = true
            }else {
                self.cellForRowAtIndexPath(self.selectedIndexPath)?.selected = true
            }
            
        })
    }
    
    
    // MARK: - Event Response
    @objc private func buttonDidTap(sender: UIButton) {
        NSNotificationCenter.defaultCenter().postNotificationName(
            MalaNotification_ChoosingSchool,
            object: schoolChoosingObject(isOpen: true, school: nil, indexPath: nil)
        )
    }
}



// MARK: - CourseChoosingPlaceTableViewCell
class CourseChoosingPlaceTableViewCell: UITableViewCell {
    
    // MARK: - Property
    /// 上课地点数据模型
    var model: SchoolModel? {
        didSet {
            
            guard model != nil else {
                return
            }
            
            titleLabel.text = model!.name
            addressLabel.text = model!.address
            
            let distance = (model?.distance ?? 0.0)
            if distance > 1000 {
                positionLabel.text = String(format: "%.1fkm", distance/1000)
            }else {
                positionLabel.text = String(format: "%.1fm", distance)
            }
        }
    }
    /// 选中状态
    override var selected: Bool {
        didSet {
            selectButton.selected = selected
        }
    }
    
    
    // MARK: - Components
    /// 上课地点名称label
    private lazy var titleLabel: UILabel = {
        let titleLabel = UILabel()
        titleLabel.textColor = MalaColor_333333_0
        titleLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        return titleLabel
    }()
    /// 上课地点详细地址label
    private lazy var addressLabel: UILabel = {
        let addressLabel = UILabel()
        addressLabel.textColor = MalaColor_939393_0
        addressLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        return addressLabel
    }()
    /// 距上课地点距离
    private lazy var positionLabel: UILabel = {
        let positionLabel = UILabel()
        positionLabel.text = "未知"
        positionLabel.textColor = MalaColor_939393_0
        positionLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        positionLabel.textAlignment = .Right
        return positionLabel
    }()
    /// 选择按钮
    private lazy var selectButton: UIButton = {
        let selectButton = UIButton()
        selectButton.setBackgroundImage(UIImage(named: "unselected"), forState: .Normal)
        selectButton.setBackgroundImage(UIImage(named: "selected"), forState: .Selected)
        // 冻结按钮交互功能，其只作为视觉显示效果使用
        selectButton.userInteractionEnabled = false
        return selectButton
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
        contentView.addSubview(titleLabel)
        contentView.addSubview(addressLabel)
        contentView.addSubview(positionLabel)
        contentView.addSubview(selectButton)
        
        // Autolayout
        titleLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_14)
            make.left.equalTo(self.contentView.snp_left)
            make.height.equalTo(MalaLayout_FontSize_14)
        }
        selectButton.snp_makeConstraints { (make) -> Void in
            make.right.equalTo(self.contentView.snp_right)
            make.centerY.equalTo(self.contentView.snp_centerY)
            make.width.equalTo(MalaLayout_Margin_18)
            make.height.equalTo(MalaLayout_Margin_18)
        }
        positionLabel.snp_makeConstraints { (make) -> Void in
            make.right.equalTo(self.selectButton.snp_left).offset(-MalaLayout_Margin_27)
            make.bottom.equalTo(self.titleLabel.snp_bottom)
            make.height.equalTo(MalaLayout_FontSize_12)
        }
        addressLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.titleLabel.snp_bottom).offset(MalaLayout_Margin_10)
            make.left.equalTo(self.titleLabel.snp_left)
            make.right.equalTo(self.selectButton.snp_left).offset(-MalaLayout_Margin_27)
//            make.bottom.equalTo(self.contentView.snp_bottom).offset(-MalaLayout_Margin_14)
            make.height.equalTo(MalaLayout_FontSize_12)
        }
    }
}


class schoolChoosingObject: NSObject {
    
    // MARK: - Property
    var isOpenCell: Bool = false
    var schoolModel: SchoolModel?
    var selectedIndexPath: NSIndexPath?
    
    
    // MARK: - Constructed
    init(isOpen: Bool, school: SchoolModel?, indexPath: NSIndexPath?) {
        super.init()
        self.isOpenCell = isOpen
        self.schoolModel = school
        self.selectedIndexPath = indexPath
    }
}