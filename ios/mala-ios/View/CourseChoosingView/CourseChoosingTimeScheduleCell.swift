//
//  CourseChoosingTimeScheduleCell.swift
//  mala-ios
//
//  Created by 王新宇 on 1/22/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class CourseChoosingTimeScheduleCell: MalaBaseCell {

    // MARK: - Property
    /// 上课时间表字符串数组
    var timeScheduleResult: [String] = [] {
        didSet {
            
            // 若结果为空时，收起Cell
            if timeScheduleResult.count == 0 {
                self.tableView.timeSchedule = timeScheduleResult
                self.isOpen = false
            }
            
            // 若下拉箭头显示且已经展开，加载时间表
            if (dropArrow.hidden == false) && (isOpen == true) {
                self.tableView.timeSchedule = timeScheduleResult
                self.tableView.snp_updateConstraints { (make) -> Void in
                    make.height.equalTo(Int(MalaLayout_FontSize_28)*(timeScheduleResult.count))
                }
                self.tableView.reloadData()
            }else {
                self.tableView.timeSchedule = timeScheduleResult
            }
        }
    }
    /// 展开标记
    var isOpen: Bool = false {
        didSet {
            if isOpen {
                self.tableView.snp_updateConstraints { (make) -> Void in
                    make.height.equalTo(Int(MalaLayout_FontSize_28)*(timeScheduleResult.count))
                }
            }else {
                self.tableView.snp_updateConstraints { (make) -> Void in
                    make.height.equalTo(0)
                }
            }
        }
    }
    private var myContext = 0
    
    
    // MARK: - Components
    private lazy var tableView: TimeScheduleCellTableView = {
        let tableView = TimeScheduleCellTableView()
        tableView.bounces = false
        return tableView
    }()
    
    
    // MARK: - Contructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        setupUserInterface()
        configura()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Public Method
    func reloadTableView () {
        tableView.timeSchedule = self.timeScheduleResult
        tableView.reloadData()
    }
    
    
    // MARK: - Override
    ///  cell点击事件
    func cellDidTap() {
        if dropArrow.hidden == false {
//            isOpen = !isOpen
            NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_OpenTimeScheduleCell, object: !isOpen)
        }
    }
    
    override func observeValueForKeyPath(keyPath: String?, ofObject object: AnyObject?, change: [String : AnyObject]?, context: UnsafeMutablePointer<Void>) {
        // 若当前选择课时数为零，隐藏下拉箭头
        if let courseChoosingObject = object as? CourseChoosingObject  {
            dropArrow.hidden = (courseChoosingObject.selectedTime.count == 0)
        }
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        dropArrow.userInteractionEnabled = false
        dropArrow.hidden = true
        
        // SubViews
        content.addSubview(tableView)
        
        // Autolayout
        content.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(title.snp_bottom).offset(MalaLayout_FontSize_14/2)
            make.bottom.equalTo(contentView.snp_bottom).offset(-MalaLayout_FontSize_14/2)
        }
        
        tableView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.content.snp_top)
            make.bottom.equalTo(self.content.snp_bottom)
            make.left.equalTo(self.content.snp_left)
            make.right.equalTo(self.content.snp_right)
            make.height.equalTo(0)
        }
    }
    
    private func configura() {
        // 通过观察originalPrice属性来监听筛选条件的变化
        MalaCourseChoosingObject.addObserver(self, forKeyPath: "originalPrice", options: .New, context: &myContext)
    }
    
    deinit {
        MalaCourseChoosingObject.removeObserver(self, forKeyPath: "originalPrice", context: &myContext)
    }
}


private let TimeScheduleCellTableViewCellReuseId = "TimeScheduleCellTableViewCellReuseId"
class TimeScheduleCellTableView: UITableView, UITableViewDelegate, UITableViewDataSource {
    
    // MARK: - Property
    var timeSchedule: [String] = [] {
        didSet {
            println("上课时间表结果 \(timeSchedule)")
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
    
    
    // MARK: - Private Method
    private func configure() {
        delegate = self
        dataSource = self
        self.separatorStyle = .None
        registerClass(TimeScheduleCellTableViewCell.self, forCellReuseIdentifier: TimeScheduleCellTableViewCellReuseId)
    }
    
    
    // MARK: - Delegate
    func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return MalaLayout_FontSize_28
    }
    
    func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return false
    }

    
    // MARK: - DataSource
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return timeSchedule.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(TimeScheduleCellTableViewCellReuseId, forIndexPath: indexPath) as! TimeScheduleCellTableViewCell
        let title = timeSchedule[indexPath.row]
        cell.setTitle(title)
        return cell
    }
}


class TimeScheduleCellTableViewCell: UITableViewCell {
    
    // MARK: - Property
    private lazy var label: UILabel = {
        let label = UILabel()
        label.frame = CGRect(x: 0, y: MalaLayout_FontSize_14/2, width: 0, height: 0)
        label.font = UIFont(name: "Courier New", size: MalaLayout_FontSize_14)
        label.textColor = MalaColor_WhiteColor
        label.sizeToFit()
        return label
    }()
    
    
    // MARK: - Constructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        self.contentView.addSubview(label)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Public Method
    func setTitle(title: String) {
        label.text = title
        label.sizeToFit()
    }
}