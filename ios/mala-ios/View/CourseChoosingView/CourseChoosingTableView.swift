//
//  CourseChoosingTableView.swift
//  mala-ios
//
//  Created by 王新宇 on 1/22/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

let CourseChoosingCellReuseId = [
    0: "CourseChoosingGradeCellReuseId",            // 选择授课年级
    1: "CourseChoosingPlaceCellReuseId",            // 选择上课地点
    2: "CourseChoosingClassScheduleCellReuseId",    // 选择上课时间（课程表）
    3: "CourseChoosingClassPeriodCellReuseId",      // 选择课时
    4: "CourseChoosingTimeScheduleCellReuseId",     // 上课时间表
    5: "CourseChoosingOtherServiceCellReuseId"      // 其他服务
]


class CourseChoosingTableView: UITableView, UITableViewDelegate, UITableViewDataSource {

    // MARK: - Property
    /// 教师详情模型
    var teacherModel: TeacherDetailModel? {
        didSet {
            
        }
    }
    /// 上课地点数据模型
    var schoolModel: [SchoolModel] = [] {
        didSet {
            // 刷新 [选择上课地点] Cell
            reloadSections(NSIndexSet(index: 1), withRowAnimation: .Fade)
        }
    }
    /// 上课地点Cell展开标识
    var isOpenSchoolsCell: Bool = false {
        didSet {
            if isOpenSchoolsCell != oldValue {
                reloadSections(NSIndexSet(index: 1), withRowAnimation: .Fade)
            }
        }
    }
    /// 课程表数据模型
    var classScheduleModel: [[ClassScheduleDayModel]] = [] {
        didSet {
            // 刷新 [选择上课地点][选择小时][上课时间] Cell
            reloadSections(NSIndexSet(index: 2), withRowAnimation: .Fade)
        }
    }
    /// 上课时间表数据
    var timeScheduleResult: [String]? {
        didSet {
            
        }
    }
    /// 课时需要更新标记 (控制课时只在课程改变时更新，滑动重用时不变)
    var isPeriodNeedUpdate: Bool = false
    var selectedIndexPath: NSIndexPath?
    
    
    // MARK: - Constructed
    override init(frame: CGRect, style: UITableViewStyle) {
        super.init(frame: frame, style: style)
        configration()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    // MARK: - Private Method
    private func configration() {
        delegate = self
        dataSource = self
        backgroundColor = MalaTeacherCellBackgroundColor
        estimatedRowHeight = 400
        separatorStyle = .None
        bounces = false
        contentInset = UIEdgeInsets(top: -40, left: 0, bottom: 4, right: 0)
        
        
        registerClass(CourseChoosingGradeCell.self, forCellReuseIdentifier: CourseChoosingCellReuseId[0]!)
        registerClass(CourseChoosingPlaceCell.self, forCellReuseIdentifier: CourseChoosingCellReuseId[1]!)
        registerClass(CourseChoosingClassScheduleCell.self, forCellReuseIdentifier: CourseChoosingCellReuseId[2]!)
        registerClass(CourseChoosingClassPeriodCell.self, forCellReuseIdentifier: CourseChoosingCellReuseId[3]!)
        registerClass(CourseChoosingTimeScheduleCell.self, forCellReuseIdentifier: CourseChoosingCellReuseId[4]!)
        registerClass(CourseChoosingOtherServiceCell.self, forCellReuseIdentifier: CourseChoosingCellReuseId[5]!)
    }
    
    // MARK: - Delegate
    func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        
    }
    
    func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return section == 0 ? 0 : MalaLayout_Margin_4
    }
    
    func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return MalaLayout_Margin_4
    }
    
    
    // MARK: - DataSource
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return CourseChoosingCellReuseId.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let reuseCell = tableView.dequeueReusableCellWithIdentifier(CourseChoosingCellReuseId[indexPath.section]!, forIndexPath: indexPath)
        reuseCell.selectionStyle = .None
        (reuseCell as! MalaBaseCell).title.text = MalaCourseChoosingCellTitle[indexPath.section+1]
        
        switch indexPath.section {
        case 0:
            let cell = reuseCell as! CourseChoosingGradeCell
            cell.prices = (teacherModel?.prices) ?? []
            return cell
            
        case 1:
            let cell = reuseCell as! CourseChoosingPlaceCell
            cell.schools = schoolModel
            cell.isOpen = self.isOpenSchoolsCell
            cell.selectedIndexPath = self.selectedIndexPath
            cell.tableViewReloadData()
            return cell
            
        case 2:
            let cell = reuseCell as! CourseChoosingClassScheduleCell
            cell.classScheduleModel = self.classScheduleModel
            return cell
            
        case 3:
            let cell = reuseCell as! CourseChoosingClassPeriodCell
            // 更新已选择课时数
            dispatch_async(dispatch_get_main_queue(), { [weak self] () -> Void in
                if ((self?.isPeriodNeedUpdate) == true) {
                    cell.updateSetpValue()
                }
            })
            self.isPeriodNeedUpdate = false
            return cell
            
        case 4:
            let cell = reuseCell as! CourseChoosingTimeScheduleCell
            cell.timeScheduleResult = self.timeScheduleResult
            return cell
            
        case 5:
            let cell = reuseCell as! CourseChoosingOtherServiceCell
            cell.price = MalaCourseChoosingObject.getPrice() ?? 0
            return cell
            
        default:
            break
        }
        
        return reuseCell
    }
    
    
    ///  清空订单条件
    private func resetOrder() {
        MalaClassPeriod_StepValue = 2
        MalaCourseChoosingObject.reset()
    }
    
    deinit {
        print("choosing TableView deinit")
        resetOrder()
    }
}